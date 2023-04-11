package tech.blastmc.holograms.listeners;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.models.line.HologramLineImpl;

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

	@EventHandler
	public void onClickUnknownEntity(PlayerUseUnknownEntityEvent event) {
		HologramsAPI.getHolograms(event.getPlayer().getWorld()).forEach(holo -> {
			holo.getLines().forEach(line -> {
				if (((HologramLineImpl) line).getDisplay().getId() == event.getEntityId())
					((HologramLineImpl) line).getOnClick().accept(event.getPlayer());
			});
		});
	}

}
