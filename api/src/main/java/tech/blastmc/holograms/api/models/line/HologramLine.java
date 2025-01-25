package tech.blastmc.holograms.api.models.line;

import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Player;
import tech.blastmc.holograms.api.models.Hologram;

import java.util.function.Consumer;

/**
 * An interface for each line of a {@link Hologram Hologram}. This can control many general stats per line.<br>
 * Use {@link TextLine TextLine}, {@link ItemLine ItemLine}, or {@link BlockLine BlockLine} for further customization.<br>
 * After setting a value here, {@link Hologram#update() Hologram#update} must be called for changes to apply
 */
public interface HologramLine<T> {

	/**
	 * Returns the data for the line. This is what is displayed on the line.
	 * @return BlockData, ItemStack, String, or float for Offsets
	 */
	T getData();

	void setData(T data);

	/**
	 * @return The index of the line for the hologram as a 0-indexed integer.
	 */
	int getIndex();

	/**
	 * @return The {@link Hologram Hologram} this line belongs to
	 */
	Hologram getHologram();

	/**
	 * The line is un-rendered to the client when out of this range.
	 * @return The view range of the line, null if default
	 */
	Float getRange();

	/**
	 * The line is un-rendered to the client when out of this range.
	 * @param range the range in which the line is un-rendered.
	 */
	void setRange(Float range);

	/**
	 * The setting for how this line will rotate towards players
	 * @return The Billboard setting for this line, null if default
	 */
	Billboard getBillboard();

	/**
	 * The setting for how this line will rotate towards players.<br>
	 * CENTER - Will face the player on x-axis and y-axis<br>
	 * VERTICAL - Will face the player in the y-axis<br>
	 * HORIZONTAL - Will face the player on the x-axis<br>
	 * FIXED - Will never face the player, and is static<br>
	 * @param billboard The Billboard setting for this line
	 */
	void setBillboard(Billboard billboard);

	/**
	 * @return The color this line glows, regardless of team, null if default
	 */
	Color getGlowColor();

	/**
	 * The color this line will glow. Alpha values not supported
	 * @param color The color this line will glow
	 */
	void setGlowColor(Color color);

	/**
	 * The overriding block light of this line
	 * @return The overriding block light of this line, null if default
	 */
	Integer getBlockLight();

	/**
	 * The overriding sky light of this line
	 * @return The overriding sky light of this line, null if default
	 */
	Integer getSkyLight();

	/**
	 * The overriding light of this line. Neither value can be null, or else the line will have no overriding light
	 * @param blockLight An integer between 0 and 15 (inclusive), or null to reset to the hologram's default
	 * @param skyLight An integer between 0 and 15 (inclusive), or null to reset to the hologram's default
	 */
	void setBrightness(Integer blockLight, Integer skyLight);

	/**
	 * Sets what will happen when a player clicks this line<br>
	 * This is not persistent and must be set each time
	 * @param clickListener The action on click to trigger
	 */
	void setClickListener(Consumer<Player> clickListener);

	void setInteractable(Boolean interactable);

	Boolean getInteractable();

	default boolean isInteractable() {
		if (this instanceof Offset)
			return false;
		if (this.getInteractable() != null && !this.getInteractable())
			return false;
		if (this.getInteractable() != null && this.getInteractable())
			return true;
		return this.getHologram().getInteractable() != null && this.getHologram().getInteractable();
	}

}
