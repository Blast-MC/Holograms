package tech.blastmc.holograms.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.line.HologramLine;

public class HologramInteractEvent extends PlayerEvent {

	@Getter
	private Hologram hologram;
	@Getter
	private HologramLine line;

	public HologramInteractEvent(@NotNull Player who, Hologram hologram, HologramLine line) {
		super(who);
		this.hologram = hologram;
		this.line = line;
	}

	private static final HandlerList handlers = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return HologramInteractEvent.handlers;
	}

	public static HandlerList getHandlerList() {
		return HologramInteractEvent.handlers;
	}
}
