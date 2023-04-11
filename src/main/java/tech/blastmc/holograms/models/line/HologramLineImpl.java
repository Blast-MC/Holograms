package tech.blastmc.holograms.models.line;

import gg.projecteden.commands.util.JsonBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Display.BillboardConstraints;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay.TextAligment;
import org.jetbrains.annotations.NotNull;
import tech.blastmc.holograms.api.models.line.HologramLine;
import tech.blastmc.holograms.models.HologramImpl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public abstract class HologramLineImpl implements HologramLine {

	@NonNull
	private HologramImpl hologram;
	private Display display;
	private int index;

	private Float range;
	private Billboard billboard;
	private Color glowColor;
	private Integer blockLight;
	private Integer skyLight;

	private Consumer<Player> onClick;

	public abstract Display render(Location location);

	public HologramLineImpl(Map<String, Object> map) {
		if (map.containsKey("range"))
			this.range = Float.parseFloat(map.get("range").toString());
		if (map.containsKey("billboard"))
			this.billboard = Billboard.valueOf(map.get("billboard").toString());
		if (map.containsKey("glowColor"))
			this.glowColor = (Color) map.get("glowColor");
		if (map.containsKey("blockLight"))
			this.blockLight = Integer.parseInt(map.get("blockLight").toString());
		if (map.containsKey("skyLight"))
			this.skyLight = Integer.parseInt(map.get("skyLight").toString());
	}

	public @NotNull Map<String, Object> serialize() {
		return new LinkedHashMap<>() {{
			if (range != null && !range.equals(getHologram().getRange()))
				put("range", range);
			if (billboard != null && !billboard.equals(getHologram().getBillboard()))
				put("billboard", billboard.name());
			if (glowColor != null && !glowColor.equals(getHologram().getGlowColor()))
				put("glowColor", glowColor);
			if (blockLight != null && !blockLight.equals(getHologram().getBlockLight()))
				put("blockLight", blockLight);
			if (skyLight != null && !skyLight.equals(getHologram().getSkyLight()))
				put("skyLight", skyLight);
		}};
	}

	public void applyDefaults(Float range, Billboard billboard, Color glowColor, ItemDisplayTransform itemTransform,
	                          Integer lineWidth, Color background, Byte opacity, Boolean shadowed, Boolean seeThrough,
	                          TextAligment alignment, Boolean mirror) {

		if (range != null)
			getDisplay().setViewRange(range);
		if (billboard != null)
			getDisplay().setBillboardConstraints(BillboardConstraints.valueOf(billboard.name()));
		if (glowColor != null)
			getDisplay().setGlowColorOverride(glowColor.asARGB());

		if (this instanceof ItemLineImpl)
			applyTypeDefaults(itemTransform);
		if (this instanceof TextLineImpl)
			applyTypeDefaults(lineWidth, background, opacity, shadowed, seeThrough, alignment, mirror);
	}

	@Override
	public void setBrightness(Integer blockLight, Integer skyLight) {
		this.blockLight = blockLight;
		this.skyLight = skyLight;
	}

	@Override
	public void setClickListener(Consumer clickListener) {
		this.onClick = clickListener;
	}

	public abstract void applyTypeDefaults(Object... objects);

	public abstract JsonBuilder renderHover(String color);

}
