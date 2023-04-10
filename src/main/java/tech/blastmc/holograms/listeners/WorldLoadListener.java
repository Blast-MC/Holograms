package tech.blastmc.holograms.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import tech.blastmc.holograms.Database;

public class WorldLoadListener implements Listener {

	@EventHandler
	public void onWorldInit(WorldLoadEvent event) {
		Database.load(event.getWorld());
	}

}
