package tech.blastmc.holograms.api.models.line;

import org.bukkit.Color;
import org.bukkit.entity.TextDisplay.TextAligment;
import tech.blastmc.holograms.api.models.Hologram;

/**
 * An instance of {@link HologramLine HologramLine} that represents a String
 * After setting a value here, {@link Hologram#update() Hologram#update} must be called for changes to apply
 */
public interface TextLine extends HologramLine {

	String getText();
	void setText(String text);
	Integer getLineWidth();
	void setLineWidth(Integer lineWidth);
	Color getBackground();
	void setBackground(Color color);
	Byte getOpacity();
	void setOpacity(Byte opacity);
	Boolean getShadowed();
	void setShadowed(Boolean shadowed);
	Boolean getSeeThrough();
	void setSeeThrough(Boolean seeThrough);
	TextAligment getAlignment();
	void setAlignment(TextAligment alignment);
	Boolean getWithMirror();
	void setWithMirror(Boolean withMirror);

}
