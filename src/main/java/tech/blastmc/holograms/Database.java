package tech.blastmc.holograms;

import joptsimple.internal.Strings;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import tech.blastmc.holograms.api.models.HologramBuilder;
import tech.blastmc.holograms.models.HologramBuilderImpl;
import tech.blastmc.holograms.models.HologramImpl;
import tech.blastmc.holograms.utils.LocationWrapper;
import tech.blastmc.holograms.utils.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
		String world = hologram.getLocationWrapper().getWorld().toLowerCase().replace(" ", "_");
		worldsMap.putIfAbsent(world, new HashSet<>());
		worldsMap.get(world).add(hologram);

		if (!hologram.isPersistent() || StringUtils.isNullOrEmpty(hologram.getId()))
			return;
		File file = getFile(world, true);
		YamlConfiguration config = loadConfig(file);
		config.set(hologram.getId(), hologram);
		config.save(file);
	}

	private static File getFile(String name, boolean forWrite) {
		File file = Holograms.getInstance().getDataFolder().toPath().resolve("database/" + name + ".yml").toFile();
		if (!file.getParentFile().exists() && forWrite)
			file.getParentFile().mkdirs();
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

		if (Strings.isNullOrEmpty(hologram.getId()))
			return;

		File file = getFile(world, true);
		YamlConfiguration config = loadConfig(file);
		config.set(hologram.getId(), null);
		config.save(file);
	}

	public enum Converter {
		HOLOGRAPHICDISPLAYS {
			@Override
			public int convert(File file) {
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				int amount = 0;

				for (String section : config.getKeys(false)) {
					try {
						List<String> lines = config.getStringList(section + ".lines");
						LocationWrapper wrapper = new LocationWrapper(
							config.getString(section + ".position.world"),
							config.getDouble(section + ".position.x"),
							config.getDouble(section + ".position.y"),
							config.getDouble(section + ".position.z")
						);

						new HologramBuilderImpl()
							.location(wrapper)
							.lines(lines.stream().map(s -> (Object) s).collect(Collectors.toList()))
							.id(section)
							.persistent(true)
							.build();

						amount++;
					} catch (Exception e) {
						Holograms.log("Exception while converting hologram: " + section);
						e.printStackTrace();
					}
				}

				return amount;
			}
		};

		public abstract int convert(File file);

		public static Optional<Converter> ofPath(String path) {
			for (Converter converter : values())
				if (path.toLowerCase().contains(converter.name().toLowerCase()))
					return Optional.of(converter);
			return Optional.empty();
		}
	}

}
