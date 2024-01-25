package tech.blastmc.holograms.models.line;

import com.mojang.math.Transformation;
import gg.projecteden.commands.util.JsonBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Display.ItemDisplay;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import tech.blastmc.holograms.api.models.line.ItemLine;
import tech.blastmc.holograms.models.HologramImpl;
import tech.blastmc.holograms.utils.PacketUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@SerializableAs("ItemLine")
public class ItemLineImpl extends HologramLineImpl implements ConfigurationSerializable, ItemLine {

	@NonNull
	private ItemStack item;
	private ItemDisplayTransform itemTransform;

	public ItemLineImpl(HologramImpl hologram, @NonNull ItemStack item, ItemDisplayTransform itemTransform) {
		super(hologram);
		this.item = item;
		this.itemTransform = itemTransform;
	}

	public ItemLineImpl(Map<String, Object> map) {
		super(map);
		this.item = (ItemStack) map.get("item");
		if (map.containsKey("itemTransform"))
			this.itemTransform = ItemDisplayTransform.valueOf(map.get("itemTransform").toString().toUpperCase());
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("item", item);
		if (itemTransform != null && !itemTransform.equals(getHologram().getItemTransform()))
			map.put("itemTransform", itemTransform.name());
		return map;
	}

	@Override
	public ItemStack getData() {
		return item;
	}

	@Override
	public void setData(Object data) {
		this.item = (ItemStack) data;
	}

	@Override
	public Display render(Location location) {
		ItemDisplay display = new ItemDisplay(EntityType.ITEM_DISPLAY, PacketUtils.toNMS(location.getWorld()));
		display.setItemStack(PacketUtils.toNMS(item));
		if (itemTransform != null) {
			ItemDisplayContext context = switch (itemTransform) {
				case FIRSTPERSON_LEFTHAND -> ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
				case FIRSTPERSON_RIGHTHAND -> ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
				case THIRDPERSON_LEFTHAND -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
				case THIRDPERSON_RIGHTHAND -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
				default -> ItemDisplayContext.valueOf(itemTransform.name());
			};
			display.setItemTransform(context);
		}

		display.setPos(PacketUtils.toNMS(location));
		display.setRot(location.getYaw(), 0);

		Transformation trans = Display.createTransformation(display.getEntityData());
		Transformation newTrans = new Transformation(trans.getTranslation(), trans.getLeftRotation(), null, trans.getRightRotation());
		display.setTransformation(newTrans);

		return display;
	}

	@Override
	public void applyTypeDefaults(Object... objects) {
		ItemDisplayTransform transform = (ItemDisplayTransform) objects[0];
		if (getLineValue(this.itemTransform, transform) != null) {
			ItemDisplayContext context = switch (getLineValue(this.itemTransform, transform)) {
				case FIRSTPERSON_LEFTHAND -> ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
				case FIRSTPERSON_RIGHTHAND -> ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
				case THIRDPERSON_LEFTHAND -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
				case THIRDPERSON_RIGHTHAND -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
				default -> ItemDisplayContext.valueOf(transform.name());
			};
			((ItemDisplay) getDisplay()).setItemTransform(context);
		}
	}

	@Override
	public JsonBuilder renderHover(String color, int index) {
		return new JsonBuilder(color + "Item").hover(item);
	}

}
