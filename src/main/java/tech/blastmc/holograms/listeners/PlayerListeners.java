package tech.blastmc.holograms.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import tech.blastmc.holograms.api.HologramsAPI;

public class PlayerListeners implements Listener {

	@EventHandler
	public void onJoinWorld(PlayerChangedWorldEvent event) {
		HologramsAPI.getHolograms(event.getFrom()).forEach(holo -> holo.hideFromPlayer(event.getPlayer()));
		HologramsAPI.getHolograms(event.getPlayer().getWorld()).forEach(holo -> holo.showToPlayer(event.getPlayer()));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		HologramsAPI.getHolograms(event.getPlayer().getWorld()).forEach(holo -> holo.showToPlayer(event.getPlayer()));
	}

}
