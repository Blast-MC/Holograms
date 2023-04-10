package tech.blastmc.holograms.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import tech.blastmc.holograms.api.models.Hologram;

public class HologramSpawnEvent extends PlayerEvent implements Cancellable {

	private boolean cancelled;
	@Getter
	private Hologram hologram;

	public HologramSpawnEvent(Player player, Hologram hologram) {
		super(player);
		this.hologram = hologram;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	private static final HandlerList handlers = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return HologramSpawnEvent.handlers;
	}

	public static HandlerList getHandlerList() {
		return HologramSpawnEvent.handlers;
	}
}
