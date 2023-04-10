package tech.blastmc.holograms.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.line.HologramLine;

public class HologramLineSpawnEvent extends HologramSpawnEvent {

	@Getter
	@Setter
	private Object data;
	@Getter
	private final HologramLine line;

	public HologramLineSpawnEvent(Player player, Hologram hologram, HologramLine line) {
		super(player, hologram);
		this.line = line;
		this.data = line.getData();
	}

	private static final HandlerList handlers = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return HologramLineSpawnEvent.handlers;
	}

	public static HandlerList getHandlerList() {
		return HologramLineSpawnEvent.handlers;
	}
}
