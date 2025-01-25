package tech.blastmc.holograms.api.models;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.TextDisplay.TextAlignment;

import java.util.List;

/**
 * A builder interface for creating new {@link Hologram Holograms} in the world.
 * Use {@link tech.blastmc.holograms.api.HologramsAPI HologramsAPI} to create a new builder instance.
 */
public interface HologramBuilder {

	/**
	 * Sets the ID of the hologram for reference in game or in the database
	 * @param id The String id, must be unique per world
	 * @return The HologramBuilder instance
	 */
	HologramBuilder id(String id);

	/**
	 * Sets the location the hologram is at, using yaw for rotation of fixed billboards
	 * @param location The location the hologram is at, must not be null
	 * @return The HologramBuilder instance
	 */
	HologramBuilder location(Location location);

	/**
	 * Sets if this hologram will be saved across reload or not
	 * @param persistent true to save across reloads, false to be deleted
	 * @return The HologramBuilder instance
	 */
	HologramBuilder persistent(boolean persistent);

	/**
	 * Sets the view range of this hologram<br>
	 * The hologram will unrender when a player is over this distance away
	 * @param range The block distance from the hologram to unrender at, or null for default
	 * @return The HologramBuilder instance
	 */
	HologramBuilder range(Integer range);

	/**
	 * The setting for how this hologram will rotate towards players.<br>
	 * CENTER - Will face the player on x-axis and y-axis<br>
	 * VERTICAL - Will face the player in the y-axis<br>
	 * HORIZONTAL - Will face the player on the x-axis<br>
	 * FIXED - Will never face the player, and is static<br>
	 * @param billboard The Billboard setting for this hologram
	 * @return The HologramBuilder instance
	 */
	HologramBuilder billboard(Billboard billboard);

	/**
	 * The color this hologram will glow. Alpha values not supported
	 * @param color The color this line will glow
	 * @return The HologramBuilder instance
	 */
	HologramBuilder glowColor(Color color);

	/**
	 * Sets the lines for this hologram from basic objects<br>
	 * Accepts a list of String, BlockData, ItemStack, or {@link tech.blastmc.holograms.api.models.line.Offset Offsets}
	 * @param lines The objects in order from top to bottom
	 * @return The HologramBuilder instance
	 */
	HologramBuilder lines(List<Object> lines);

	/**
	 * Sets the point at which text lines will wrap to new lines
	 * @param width The maximum width of text lines
	 * @return The HologramBuilder instance
	 */
	HologramBuilder lineWidth(Integer width);

	/**
	 * The opacity of text lines for this hologram
	 * @param opacity A byte between 0 and 1, 0 being completely transparent
	 * @return The HologramBuilder instance
	 */
	HologramBuilder opacity(Byte opacity);

	/**
	 * Sets if text lines will have drop shadows on the text
	 * @param shadowed true for drop shadows, false for none, null for default
	 * @return The HologramBuilder instance
	 */
	HologramBuilder shadowed(Boolean shadowed);

	/**
	 * Sets if text lines will be visible through blocks
	 * @param seeThrough true for see through blocks
	 * @return The HologramBuilder instance
	 */
	HologramBuilder seeThrough(Boolean seeThrough);

	/**
	 * Sets the text alignment rule for text lines<br>
	 * This value will only be used if a line passes the max line width
	 * @param alignment The alignment rule of text lines
	 * @return The HologramBuilder instance
	 */
	HologramBuilder alignment(TextAlignment alignment);

	/**
	 * Sets how item lines will be rendered in this hologram<br>
	 * Default is GROUND
	 * @param itemTransform The item transform for the hologram, or null for no transform
	 * @return The HologramBuilder instance
	 */
	HologramBuilder itemTransform(ItemDisplayTransform itemTransform);

	/**
	 * Sets the attributes for the shadow this hologram casts on the ground<br>
	 * If either value is null, the hologram will have no shadow<br>
	 * The shadow is only rendered on the bottom line of the hologram
	 * @param radius The block radius of the shadow, or null for no shadow
	 * @param strength The strength of the shadow, or null for no shadow
	 * @return The HologramBuilder instance
	 */
	HologramBuilder shadow(Float radius, Float strength);

	/**
	 * Sets the brightness overrides for this hologram<br>
	 * The hologram will always have these values, regardless of light around them<br>
	 * Neither value can be null for these to apply
	 * @param blockLight The block light override of the hologram
	 * @param skyLight The sky light override for the hologram
	 * @return The HologramBuilder instance
	 */
	HologramBuilder brightness(Integer blockLight, Integer skyLight);

	/**
	 * Sets the lines of the hologram from basic objects<br>
	 * Accepts a list of String, BlockData, ItemStack or {@link tech.blastmc.holograms.api.models.line.Offset Offset}
	 * @param lines The lines of the hologram, from top to bottom
	 * @return The HologramBuilder instance
	 */
	HologramBuilder lines(Object... lines);

	/**
	 * The background color of text lines<br>
	 * Alpha values below 24 will be completely transparent
	 * @param background The Bukkit color of the text backgrounds
	 * @return The HologramBuilder instance
	 */
	HologramBuilder background(Color background);

	/**
	 * The background color of text lines<br>
	 * Alpha values below 24 will be completely transparent
	 * @param a The alpha value of the background
	 * @param r The red value of the background
	 * @param g The green value of the background
	 * @param b The blue value of the background
	 * @return The HologramBuilder instance
	 */
	HologramBuilder background(int a, int r, int g, int b);

	/**
	 * Sets the background color of text lines to be completely transparent
	 * @return The HologramBuilder instance
	 */
	HologramBuilder transparent();

	/**
	 * Sets {@link tech.blastmc.holograms.api.models.line.TextLine TextLines} of the hologram to have a mirrored line<br>
	 * This will spawn two lines, facing opposite directions so both sides have text<br>
	 * This only applies with the {@link #billboard(Billboard) Billboard} setting is FIXED
	 * @return The HologramBuilder instance
	 */
	HologramBuilder withMirror();

	HologramBuilder interactable(Boolean interactable);

	/**
	 * Builds and spawns the hologram in the world
	 * Use {@link tech.blastmc.holograms.api.events.HologramSpawnEvent HologramSpawnEvent} to cancel showing to a player.
	 * @return The hologram built from this builder
	 */
	Hologram spawn();

	/**
	 * Builds the hologram, but does not spawn it.<br>
	 * Use {@link Hologram#spawn() Hologram#spawn} to spawn it later
	 * @return The Hologram built from this builder
	 */
	Hologram build();

}
