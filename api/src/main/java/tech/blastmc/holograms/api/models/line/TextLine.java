package tech.blastmc.holograms.api.models.line;

import org.bukkit.Color;
import org.bukkit.entity.TextDisplay.TextAligment;
import tech.blastmc.holograms.api.models.Hologram;

/**
 * An instance of {@link HologramLine HologramLine} that represents a String
 * After setting a value here, {@link Hologram#update() Hologram#update} must be called for changes to apply
 */
public interface TextLine extends HologramLine {

	/**
	 * Gets the text of this line
	 * @return The pre-colored text
	 */
	String getText();

	/**
	 * Sets the text of this line<br>
	 * Use of chat colors and hex is supported here
	 * @param text The pre-colored text of this line
	 */
	void setText(String text);

	/**
	 * Gets the line width of this line<br>
	 * The text will wrap once this point is passed
	 * @return The line width at which the text will wrap, null if default
	 */
	Integer getLineWidth();

	/**
	 * Sets the line width of this line
	 * @param lineWidth The maximum length of a line before it wraps, null to reset
	 */
	void setLineWidth(Integer lineWidth);

	/**
	 * Gets the background color of this text line
	 * @return The ARGB color of the background, null if default
	 */
	Color getBackground();

	/**
	 * Sets the background color of this line<br>
	 * Alpha values below 24 will be fully transparent
	 * @param color The background color for this line, null to reset
	 */
	void setBackground(Color color);

	/**
	 * Gets the opacity of the line
	 * @return The opacity of the text line, null if default
	 */
	Byte getOpacity();

	/**
	 * Sets the opacity of this line
	 * @param opacity The opacity for the line, between 0 and 1
	 */
	void setOpacity(Byte opacity);

	/**
	 * If the line has a drop shadow on the text or not
	 * @return true if the line has a drop shadow, null if default
	 */
	Boolean getShadowed();

	/**
	 * Sets if the line will have a drop shadow or not
	 * @param shadowed true for drop shadow, null to reset to default
	 */
	void setShadowed(Boolean shadowed);

	/**
	 * If the line will be seen through walls or not
	 * @return If the line can be seen through walls or not, null if default
	 */
	Boolean getSeeThrough();

	/**
	 * Sets if this line will be seen through walls
	 * @param seeThrough true to see through walls, null to reset to default
	 */
	void setSeeThrough(Boolean seeThrough);

	/**
	 * Gets the alignment rule for this line
	 * @return The alignment rule, or null for default
	 */
	TextAligment getAlignment();

	/**
	 * Sets the alignment rule of this line
	 * @param alignment The alignment rule, or null to reset to default
	 */
	void setAlignment(TextAligment alignment);

	/**
	 * If this line has a mirror line and will show on both sides.<br>
	 * This only applies if the billboard is FIXED
	 * @return The mirror strategy, or null if default
	 */
	Boolean getWithMirror();

	/**
	 * Sets if this line will have a mirror when billboard is FIXED
	 * @param withMirror true for mirror, or null to reset to default
	 */
	void setWithMirror(Boolean withMirror);

}
