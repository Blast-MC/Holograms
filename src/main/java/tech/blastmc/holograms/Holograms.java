package tech.blastmc.holograms;

import gg.projecteden.commands.Commands;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.line.Offset;
import tech.blastmc.holograms.commands.HologramCommand;
import tech.blastmc.holograms.listeners.PlayerListeners;
import tech.blastmc.holograms.listeners.WorldLoadListener;
import tech.blastmc.holograms.models.HologramAPIImpl;
import tech.blastmc.holograms.utils.protocol.ProtocolManager;

/**
 * TODO
 *  - Scale
 */
public final class Holograms extends JavaPlugin implements Listener {

    private static Holograms INSTANCE;
	@Getter
	private ProtocolManager protocol;

    public Holograms() {
		Holograms.INSTANCE = this;
    }

	public static Holograms getInstance() {
		return Holograms.INSTANCE;
	}

	@Override
    public void onEnable() {
		protocol = new ProtocolManager(this);
        new HologramAPIImpl();

		new Reflections("tech.blastmc.holograms").getTypesAnnotatedWith(SerializableAs.class).forEach(clazz -> {
			if (!clazz.isAnnotationPresent(SerializableAs.class)) return;
			String alias = clazz.getAnnotation(SerializableAs.class).value();
			ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) clazz, alias);
		});
		ConfigurationSerialization.registerClass(Offset.class, "Offset");

		registerListener(new PlayerListeners());
		registerListener(new WorldLoadListener());

		new Commands(this)
			.add(HologramCommand.class)
			.registerAll();

		for (World world : Bukkit.getWorlds())
			Database.load(world);
    }

	@Override
	public void onDisable() {
		Commands.unregisterAll();

		Bukkit.getOnlinePlayers().forEach(player -> HologramsAPI.getHolograms(player.getWorld()).forEach(holo -> holo.hideFromPlayer(player)));
	}

    public static void log(String message) {
        INSTANCE.getServer().getLogger().info(message);
    }

	public static void warn(String message) {
		INSTANCE.getServer().getLogger().warning(message);
	}

	public static void registerListener(Listener listener) {
		INSTANCE.getServer().getPluginManager().registerEvents(listener, INSTANCE);
	}

}
