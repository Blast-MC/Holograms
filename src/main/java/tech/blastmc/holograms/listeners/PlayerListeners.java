package tech.blastmc.holograms.listeners;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import gg.projecteden.commands.util.Cooldown;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import tech.blastmc.holograms.Holograms;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.events.HologramInteractEvent;
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
		if (event.getHand() != EquipmentSlot.HAND) return;

		HologramsAPI.getHolograms(event.getPlayer().getWorld()).forEach(holo -> {
			holo.getLines().forEach(line -> {
				if (!(line instanceof HologramLineImpl impl)) return;
				if (!impl.isInteractable() || impl.getInteractEntity() == null) return;
				if (impl.getInteractEntity().getId() != event.getEntityId()) return;
				if (!Cooldown.of(event.getPlayer()).check("hg-interact-" + event.getEntityId(), 1L)) return;
				if (!new HologramInteractEvent(event.getPlayer(), impl.getHologram(), line).callEvent()) return;

				if (impl.getOnClick() == null) return;
				impl.getOnClick().accept(event.getPlayer());
			});
		});
	}

}
