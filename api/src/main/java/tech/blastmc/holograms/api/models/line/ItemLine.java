package tech.blastmc.holograms.api.models.line;

import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import tech.blastmc.holograms.api.models.Hologram;

/**
 * An instance of {@link HologramLine HologramLine} that represents an ItemStack<br>
 * After setting a value here, {@link Hologram#update() Hologram#update} must be called for changes to apply
 */
public interface ItemLine extends HologramLine {

	/**
	 * The Item this line renders as
	 * @return The ItemStack this line renders as
	 */
	ItemStack getItem();

	/**
	 * Set the ItemStack this line renders as
	 * @param item The ItemStack
	 */
	void setItem(ItemStack item);

	/**
	 * The Item Display Transform that controls how this item looks
	 * @return The transform type
	 */
	ItemDisplayTransform getItemTransform();

	/**
	 * Sets the Item Display Transform for this line
	 * @param itemTransform The Item Transform, or null to reset to the hologram's default
	 */
	void setItemTransform(ItemDisplayTransform itemTransform);

}
