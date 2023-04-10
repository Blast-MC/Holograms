package tech.blastmc.holograms.api.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * TODO - javadoc
 */
public interface PowerUp {

	PowerUp location(Location location);

	PowerUp item(ItemStack item);

	PowerUp lines(String... lines);

	PowerUp onPickup(Consumer<Player> onPickup);

	PowerUp pickupRange(float range);

	void spawn();

	void remove();

	Location getLocation();

	ItemStack getItem();

	List<String> getLines();

	float getPickupRange();

}
