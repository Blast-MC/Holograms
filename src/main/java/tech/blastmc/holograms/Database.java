package tech.blastmc.holograms;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import tech.blastmc.holograms.models.HologramImpl;
import tech.blastmc.holograms.utils.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Database {

	@Getter
	private static Map<String, Set<HologramImpl>> worldsMap = new HashMap<>();

	public static void load(World world) {
		String name = world.getName().toLowerCase().replace(" ", "_");
		YamlConfiguration config = loadConfig(getFile(name, false));
		if (config == null)
			return;
		config.getConfigurationSection("").getKeys(false).forEach(key -> {
			try {
				HologramImpl hologram = config.getSerializable(key, HologramImpl.class);
				hologram.setId(key);
				hologram.setPersistent(true);
				hologram.spawn();
				worldsMap.putIfAbsent(name, new HashSet<>());
				worldsMap.get(name).add(hologram);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	@SneakyThrows
	public static void save(HologramImpl hologram) {
		if (!hologram.isPersistent() || StringUtils.isNullOrEmpty(hologram.getId()))
			return;
		String world = hologram.getLocation().getWorld().getName().toLowerCase().replace(" ", "_");
		worldsMap.putIfAbsent(world, new HashSet<>());
		worldsMap.get(world).add(hologram);
		File file = getFile(world, true);
		YamlConfiguration config = loadConfig(file);
		config.set(hologram.getId(), hologram);
		config.save(file);
	}

	private static File getFile(String name, boolean forWrite) {
		File file = Holograms.getInstance().getDataFolder().toPath().resolve("database/" + name + ".yml").toFile();
		if (!file.getParentFile().exists() && forWrite)
			file.mkdirs();
		if (!file.exists()) {
			if (forWrite) {
				try {
					file.createNewFile();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else return null;
		}
		return file;
	}

	private static YamlConfiguration loadConfig(File file) {
		if (file == null)
			return null;
		return YamlConfiguration.loadConfiguration(file);
	}

	@SneakyThrows
	public static void remove(HologramImpl hologram) {
		String world = hologram.getLocation().getWorld().getName().toLowerCase().replace(" ", "_");
		worldsMap.getOrDefault(world, new HashSet<>()).remove(hologram);
		File file = getFile(world, true);
		YamlConfiguration config = loadConfig(file);
		config.set(hologram.getId(), null);
		config.save(file);
	}
}
