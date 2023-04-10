package tech.blastmc.holograms.api.models.line;

import org.bukkit.block.data.BlockData;
import tech.blastmc.holograms.api.models.Hologram;

/**
 * An instance of {@link HologramLine HologramLine} that represents a Block.<br>
 * After setting a value here, {@link Hologram#update() Hologram#update} must be called for changes to apply.
 */
public interface BlockLine extends HologramLine {

	/**
	 * @return The BlockData of the line
	 */
	BlockData getBlockData();

	/**
	 * Sets the BlockData for this line.
	 * After setting this value, {@link Hologram#update() Hologram#update} must be called for it to take effect.
	 * @param blockData An instance of BlockData
	 */
	void setBlockData(BlockData blockData);

}
