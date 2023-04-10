package tech.blastmc.holograms.models.line;

import com.mojang.math.Transformation;
import gg.projecteden.commands.util.JsonBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Display.BlockDisplay;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import tech.blastmc.holograms.api.models.line.BlockLine;
import tech.blastmc.holograms.models.HologramImpl;
import tech.blastmc.holograms.utils.PacketUtils;
import tech.blastmc.holograms.utils.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SerializableAs("BlockLine")
public class BlockLineImpl extends HologramLineImpl implements ConfigurationSerializable, BlockLine {

	@NonNull
	private BlockData blockData;

	public BlockLineImpl(HologramImpl hologram, BlockData blockData) {
		super(hologram);
		this.blockData = blockData;
	}

	public BlockLineImpl(Map<String, Object> map) {
		super(map);
		this.blockData = Bukkit.createBlockData(map.get("blockData").toString());
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("blockData", blockData.getAsString(true));
		return map;
	}

	@Override
	public BlockData getData() {
		return blockData;
	}

	@Override
	public void setData(Object data) {
		this.blockData = (BlockData) data;
	}

	@Override
	public Display render(Location location) {
		BlockDisplay display = new BlockDisplay(EntityType.BLOCK_DISPLAY, PacketUtils.toNMS(location.getWorld()));
		display.setBlockState(PacketUtils.toNMS(blockData));
		display.setPos(PacketUtils.toNMS(location));
		display.setRot(location.getYaw(), 0);

		Transformation trans = Display.createTransformation(display.getEntityData());
		Vector3f translation = trans.getTranslation();
		translation.x -= .5f;
		translation.z -= .5f;
		Transformation newTrans = new Transformation(translation, trans.getLeftRotation(), trans.getScale(), trans.getRightRotation());
		display.setTransformation(newTrans);

		return display;
	}

	@Override
	public void applyTypeDefaults(Object... objects) { }

	@Override
	public JsonBuilder renderHover(String color) {
		return new JsonBuilder(color + "Block").hover(StringUtils.camelCase(blockData.getMaterial().toString()));
	}
}
