package tech.blastmc.holograms.models.line;

import gg.projecteden.commands.util.JsonBuilder;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.jetbrains.annotations.NotNull;
import tech.blastmc.holograms.api.models.line.TextLine;
import tech.blastmc.holograms.models.HologramImpl;
import tech.blastmc.holograms.utils.PacketUtils;
import tech.blastmc.holograms.utils.protocol.Reflection;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@SerializableAs("TextLine")
public class TextLineImpl extends HologramLineImpl implements ConfigurationSerializable, TextLine {

	@NonNull
	private String text = "Text Line";
	private Integer lineWidth;
	private Color background;
	private Byte opacity;
	private Boolean shadowed;
	private Boolean seeThrough;
	private TextAlignment alignment;
	private Boolean withMirror;

	private Display mirror;

	public TextLineImpl(HologramImpl hologram, @NonNull String text, Integer lineWidth, Color background, Byte opacity, Boolean shadowed, Boolean seeThrough, TextAlignment alignment, Boolean withMirror) {
		super(hologram);
		this.text = text;
		this.lineWidth = lineWidth;
		this.background = background;
		this.opacity = opacity;
		this.shadowed = shadowed;
		this.seeThrough = seeThrough;
		this.alignment = alignment;
		this.withMirror = withMirror;
	}

	public TextLineImpl(Map<String, Object> map) {
		super(map);
		this.text = map.getOrDefault("text", text).toString();
		if (map.containsKey("lineWidth"))
			this.lineWidth = Integer.parseInt(map.get("lineWidth").toString());
		if (map.containsKey("background"))
			this.background = (Color) map.get("background");
		if (map.containsKey("opacity"))
			this.opacity = Byte.parseByte(map.get("opacity").toString());
		if (map.containsKey("shadowed"))
			this.shadowed = (boolean) map.get("shadowed");
		if (map.containsKey("seeThrough"))
			this.seeThrough = (boolean) map.get("seeThrough");
		if (map.containsKey("alignment"))
			this.alignment = TextAlignment.valueOf(map.get("alignment").toString());
		if (map.containsKey("withMirror"))
			this.withMirror = (boolean) map.get("withMirror");
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("text", text);
		if (lineWidth != null && !lineWidth.equals(getHologram().getLineWidth()))
			map.put("lineWidth", lineWidth);
		if (background != null && !background.equals(getHologram().getBackground()))
			map.put("background", background);
		if (opacity != null && !opacity.equals(getHologram().getOpacity()))
			map.put("opacity", opacity);
		if (shadowed != null && !shadowed.equals(getHologram().getShadowed()))
			map.put("shadowed", shadowed);
		if (seeThrough != null && !seeThrough.equals(getHologram().getSeeThrough()))
			map.put("seeThrough", seeThrough);
		if (alignment != null && !alignment.equals(getHologram().getAlignment()))
			map.put("alignment", alignment.name());
		if (withMirror != null && !withMirror.equals(getHologram().getMirror()))
			map.put("withMirror", withMirror);
		return map;
	}

	@Override
	public String getData() {
		return text;
	}

	@Override
	public void setData(Object data) {
		this.text = (String) data;
	}

	@Override
	public Display render(Location location) {
		TextDisplay display = new TextDisplay(EntityType.TEXT_DISPLAY, PacketUtils.toNMS(location.getWorld()));
		display.setText(PacketUtils.toNMS(text));
		if (lineWidth != null) {
			Reflection.MethodInvoker method = Reflection.getMethod(Display.TextDisplay.class, "b", int.class);
			method.invoke(display, lineWidth.intValue());
		}
		if (background != null) {
			Reflection.MethodInvoker method = Reflection.getMethod(Display.TextDisplay.class, "c", int.class);
			method.invoke(display, background.asARGB());
		}
		if (opacity != null)
			display.setTextOpacity(opacity);
		if (shadowed != null)
			display.setSharedFlag(Display.TextDisplay.FLAG_SHADOW, shadowed);
		if (seeThrough != null)
			display.setSharedFlag(TextDisplay.FLAG_SEE_THROUGH, seeThrough);
		if (alignment != null) {
			switch (alignment) {
				case LEFT -> {
					display.setSharedFlag(TextDisplay.FLAG_ALIGN_LEFT, true);
					display.setSharedFlag(TextDisplay.FLAG_ALIGN_RIGHT, false);
				}
				case RIGHT -> {
					display.setSharedFlag(TextDisplay.FLAG_ALIGN_LEFT, false);
					display.setSharedFlag(TextDisplay.FLAG_ALIGN_RIGHT, true);
				}
				case CENTER -> {
					display.setSharedFlag(TextDisplay.FLAG_ALIGN_LEFT, false);
					display.setSharedFlag(TextDisplay.FLAG_ALIGN_RIGHT, false);
				}
			}
		}
		display.setPos(PacketUtils.toNMS(location));
		display.setRot(location.getYaw(), 0);
		return display;
	}

	@Override
	public void applyTypeDefaults(Object... objects) {
		applyDefaultsToDisplay(getDisplay(), objects);
		if (this.withMirror == null)
			this.withMirror = (Boolean) objects[6];
		if (mirror != null)
			applyDefaultsToDisplay(mirror, objects);
	}

	private void applyDefaultsToDisplay(Display display, Object... objects) {
		TextDisplay text = (TextDisplay) display;
		if (objects[0] != null) {
			Reflection.MethodInvoker method = Reflection.getMethod(TextDisplay.class, "b", int.class);
			method.invoke(display, ((Integer) objects[0]).intValue());
		}
		if (objects[1] != null && background != null) {
			Reflection.MethodInvoker method = Reflection.getMethod(Display.TextDisplay.class, "c",  int.class);
			method.invoke(display, ((Color) objects[1]).asARGB());
		}
		if (objects[2] != null)
			text.setTextOpacity((Byte) objects[2]);
		if (objects[3] != null)
			text.setSharedFlag(TextDisplay.FLAG_SHADOW, (Boolean) objects[3]);
		if (objects[4] != null)
			text.setSharedFlag(TextDisplay.FLAG_SEE_THROUGH, (Boolean) objects[4]);
		if (objects[5] != null)
			switch ((TextAlignment) objects[5]) {
				case LEFT -> {
					text.setSharedFlag(TextDisplay.FLAG_ALIGN_LEFT, true);
					text.setSharedFlag(TextDisplay.FLAG_ALIGN_RIGHT, false);
				}
				case RIGHT -> {
					text.setSharedFlag(TextDisplay.FLAG_ALIGN_LEFT, false);
					text.setSharedFlag(TextDisplay.FLAG_ALIGN_RIGHT, true);
				}
				case CENTER -> {
					text.setSharedFlag(TextDisplay.FLAG_ALIGN_LEFT, false);
					text.setSharedFlag(TextDisplay.FLAG_ALIGN_RIGHT, false);
				}
			}
	}

	@Override
	public JsonBuilder renderHover(String color, int index) {
		return new JsonBuilder(color + "Text").hover(text);
	}

}
