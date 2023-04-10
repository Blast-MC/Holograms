package tech.blastmc.holograms.models;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import tech.blastmc.holograms.Holograms;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.PowerUp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Data
public class PowerUpImpl implements PowerUp, Listener {

	private static final AtomicInteger index = new AtomicInteger(0);

	private int id = PowerUpImpl.index.getAndIncrement();
	private Location location;
	private ItemStack item;
	private List<String> lines;
	private Consumer<Player> onPickup;
	private float pickupRange = 1;

	private Hologram hologram;

	@Override
	public PowerUp location(Location location) {
		this.location = location;
		return this;
	}

	@Override
	public PowerUp item(ItemStack item) {
		this.item = item;
		return this;
	}

	@Override
	public PowerUp lines(String... lines) {
		this.lines = new ArrayList<>(List.of(lines));
		return this;
	}

	@Override
	public PowerUp onPickup(Consumer<Player> onPickup) {
		this.onPickup = onPickup;
		return this;
	}

	@Override
	public PowerUp pickupRange(float range) {
		this.pickupRange = range;
		return this;
	}

	@Override
	public void spawn() {
		List<Object> lines = new ArrayList<>();
		if (this.lines != null)
			lines.addAll(this.lines);
		if (this.item != null)
			lines.add(this.item);
		this.hologram = HologramsAPI.builder()
			.location(this.location)
			.lines(lines)
			.spawn();
		Holograms.registerListener(this);
	}

	@Override
	public void remove() {
		this.hologram.remove();
		HandlerList.unregisterAll(this);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!player.getWorld().equals(this.location.getWorld())) return;
		if (player.getLocation().distanceSquared(this.location) > pickupRange) return;

		remove();
		this.onPickup.accept(player);
	}

}
