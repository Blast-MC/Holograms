package tech.blastmc.holograms.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import tech.blastmc.holograms.Holograms;
import tech.blastmc.holograms.api.models.PowerUp;
import tech.blastmc.holograms.api.models.line.Offset;
import tech.blastmc.holograms.utils.LocationWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
@Data
@SerializableAs("PowerUp")
public class PowerUpImpl extends HologramImpl implements PowerUp, Listener {

	private ItemStack item;
	private List<String> title;
	private Consumer<Player> onPickup;
	private float pickupRange = 1;

	@Override
	public PowerUp location(Location location) {
		this.location = new LocationWrapper(location);
		return this;
	}

	@Override
	public PowerUp item(ItemStack item) {
		this.item = item;
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
		this.lines = new ArrayList<>();
		if (this.title != null)
			this.title.forEach(line -> this.lines.add(convert(line)));
		if (this.item != null) {
			this.lines.add(Offset.of(0.25F));
			this.lines.add(convert(this.item));
		}

		super.spawn();

		Holograms.registerListener(this);
	}

	@Override
	public PowerUp title(String... lines) {
		this.title = new ArrayList<>(Arrays.asList(lines));
		return this;
	}

	@Override
	public void remove() {
		super.remove();
		HandlerList.unregisterAll(this);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!player.getWorld().getName().equalsIgnoreCase(this.location.getWorld())) return;
		if (player.getLocation().distanceSquared(this.location.toLocation()) > pickupRange * pickupRange)
			if (player.getEyeLocation().distanceSquared(this.location.toLocation()) > pickupRange * pickupRange)
				return;

		remove();
		this.onPickup.accept(player);
	}

	@Override
	public void setPersistent(boolean persistent) {
		throw new UnsupportedOperationException("PowerUps cannot be saved to file");
	}
}
