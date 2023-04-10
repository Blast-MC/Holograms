package tech.blastmc.holograms.api.models;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay.TextAligment;
import tech.blastmc.holograms.api.models.line.HologramLine;

import java.util.List;

/**
 * An interface for interacting with Holograms in the world.<br>
 * Use {@link HologramBuilder HologramBuilder} to create a new instance.
 */
public interface Hologram {

	/**
	 * Represents the ID of this hologram<br>
	 * This value must be set inside of {@link HologramBuilder the builder}.
	 * @return The string value of the id, or null
	 */
	String getId();

	/**
	 * The location of this hologram, including yaw
	 * @return The location value of this hologram
	 */
	Location getLocation();

	/**
	 * Sets the location of this hologram, including yaw, but not pitch
	 * @param location The location value for this hologram
	 */
	void setLocation(Location location);

	/**
	 * Sets if this hologram is persistent of not<br>
	 * If a hologram is persistent, it will be saved over reload<br>
	 * The hologram's id must also be set through the method in {@link HologramBuilder#id HologramBuilder#id}
	 * @param persistent If the hologram should be saved over reload or not
	 */
	void setPersistent(boolean persistent);

	/**
	 * Gets if this hologram will be saved over reloads
	 * @return The persistence value of this hologram
	 */
	boolean isPersistent();

	/**
	 * Sets the view range of this hologram<br>
	 * The hologram will unrender when a player is over this distance away
	 * @param range The block distance from the hologram to unrender at
	 */
	void setRange(float range);

	/**
	 * Gets the range at which a hologram unrenders from a player
	 * @return The float block distance, or null if not set
	 */
	Float getRange();

	/**
	 * Sets if this hologram has a shadow or not<br>
	 * Only the bottom line of a hologram will render a shadow<br>
	 * If either value is null, there will not be a shadow
	 * @param shadowRadius The radius of the shadow, or null to remove shadow
	 * @param shadowStrength The strength of the shadow, or null to remove shadow
	 */
	void setShadow(Float shadowRadius, Float shadowStrength);

	/**
	 * Gets the shadow radius of the hologram
	 * @return The radius is blocks, or null if none
	 */
	Float getShadowRadius();

	/**
	 * Gets the shadow strength of the hologram
	 * @return The darkness of the hologram from 0 to 1, or null if none
	 */
	Float getShadowStrength();

	/**
	 * The setting for how this hologram will rotate towards players.<br>
	 * CENTER - Will face the player on x-axis and y-axis<br>
	 * VERTICAL - Will face the player in the y-axis<br>
	 * HORIZONTAL - Will face the player on the x-axis<br>
	 * FIXED - Will never face the player, and is static<br>
	 * @param billboard The Billboard setting for this hologram
	 */
	void setBillboard(Billboard billboard);
	/**
	 * The setting for how this hologram will rotate towards players
	 * @return The Billboard setting for this hologram
	 */
	Billboard getBillboard();

	/**
	 * The color this hologram will glow. Alpha values not supported
	 * @param color The color this line will glow
	 */
	void setGlowColor(Color color);

	/**
	 * @return The color this hologram glows, regardless of team
	 */
	Color getGlowColor();

	/**
	 * The overriding block light of this hologram
	 * @return The overriding block light of this hologram, null if default
	 */
	Integer getBlockLight();

	/**
	 * The overriding sky light of this hologram
	 * @return The overriding sky light of this hologram, null if default
	 */
	Integer getSkyLight();

	/**
	 * The overriding light of this hologram. Neither value can be null, or else the hologram will have no overriding light
	 * @param blockLight An integer between 0 and 15 (inclusive), or null to reset
	 * @param skyLight An integer between 0 and 15 (inclusive), or null to reset
	 */
	void setBrightness(Integer blockLight, Integer skyLight);

	/**
	 * Sets how text lines will wrap when past their maximum length
	 * @param alignment The alignment type for text lines
	 */
	void setAlignment(TextAligment alignment);

	/**
	 * Gets the alignment of text lines when they are past their maximum length
	 * @return The text alignment of text lines
	 */
	TextAligment getAlignment();

	/**
	 * Sets if this hologram has mirrored text lines that render on both sides or one.<br>
	 * This will spawn two holograms per text line, facing opposite of each other.<br>
	 * The billboard setting must be FIXED for this to apply.
	 * @param mirror The mirror setting for this hologram, null to reset
	 */
	void setMirror(Boolean mirror);

	/**
	 * Gets if this hologram will have mirrored text lines
	 * @return The mirror setting, or null if absent
	 */
	Boolean getMirror();

	/**
	 * Sets how item lines will be rendered in this hologram<br>
	 * Default is GROUND
	 * @param transform The item transform for the hologram, or null for no transform
	 */
	void setItemTransform(ItemDisplayTransform transform);

	/**
	 * Gets the item transform for this hologram
	 * @return How items will be rendered for this hologram, or null for default
	 */
	ItemDisplayTransform getItemTransform();

	/**
	 * Gets the {@link HologramLine line objects} for the hologram
	 * @return A list of line objects for this hologram
	 */
	List<HologramLine> getLines();

	/**
	 * Sets the lines for this hologram from basic objects<br>
	 * Accepts a list of String, BlockData, ItemStack, or {@link tech.blastmc.holograms.api.models.line.Offset Offsets}
	 * @param lines
	 */
	void setLines(List<Object> lines);

	/**
	 * Sets the line of this hologram from basic object
	 * @param index The index of the line to replace
	 * @param line The object to create a line from<br>
	 * Accepts a String, BlockData, ItemStack, or an {@link tech.blastmc.holograms.api.models.line.Offset Offset}
	 */
	void setLine(int index, Object line);

	/**
	 * Adds a line from basic object to the bottom of this hologram
	 * @param line Accepts a String, BlockData, ItemStack, or an {@link tech.blastmc.holograms.api.models.line.Offset Offset}
	 */
	void addLine(Object line);

	/**
	 * Adds a line at the specified index from a basic object to this hologram
	 * @param index The index of the line to insert at
	 * @param line Accepts a String, BlockData, ItemStack, or an {@link tech.blastmc.holograms.api.models.line.Offset Offset}
	 */
	void addLine(int index, Object line);

	/**
	 * Removes a line from the hologram
	 * @param index The index of the line to remove
	 */
	void removeLine(int index);

	/**
	 * Spawns the hologram at the location with the given lines.<br>
	 * This will show to all players in the world.<br>
	 * Use {@link tech.blastmc.holograms.api.events.HologramSpawnEvent HologramSpawnEvent} to cancel showing to a player.
	 */
	void spawn();

	/**
	 * Updates the hologram with any changes made.<br>
	 * Most changes will need an update call for changes to take effect
	 */
	void update();

	/**
	 * Removes the hologram from the world, hiding from all players
	 */
	void remove();

	/**
	 * Shows the hologram to a specified player
	 * @param player The player to show the hologram to
	 */
	void showToPlayer(Player player);

	/**
	 * Hides a hologram from a player
	 * @param player The player to hide the hologram from
	 */
	void hideFromPlayer(Player player);

	/**
	 * Saves the hologram to file.<br>
	 * Both the id and persistence values must be set for this to have an effect.
	 */
	void save();
}
