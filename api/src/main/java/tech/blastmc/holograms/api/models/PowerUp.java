package tech.blastmc.holograms.api.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * An interface which executes code when a player is near a hologram
 */
public interface PowerUp extends Hologram {

	/**
	 * Sets the location of this power up<br>
	 * Once spawned, remove and spawned must be called for this to update.
	 * @param location The location of the power up
	 * @return The Power up instance
	 */
	PowerUp location(Location location);

	/**
	 * The ItemStack on the bottom of the hologram
	 * @param item The ItemStack to render
	 * @return The Power up instance
	 */
	PowerUp item(ItemStack item);

	/**
	 * The String lines to render above the item
	 * @param lines The lines, from top to bottom
	 * @return The Power up instance
	 */
	PowerUp title(String... lines);

	/**
	 * What should happen when a player pickups the Power up
	 * @param onPickup A consumer that provides the player that picked up the powerup
	 * @return The Power up instance
	 */
	PowerUp onPickup(Consumer<Player> onPickup);

	/**
	 * Sets how far a player should be able to pickup the Power up
	 * @param range The block distance a player can pick this up from
	 * @return The Power up instance
	 */
	PowerUp pickupRange(float range);

	/**
	 * Gets the item rendered for this power up
	 * @return The ItemStack used to render
	 */
	ItemStack getItem();


	/**
	 * Gets the range at which a player can pick up the power up
	 * @return The block distance away from the power up
	 */
	float getPickupRange();

}
