package tech.blastmc.holograms.api.models.line;

import lombok.Data;
import org.bukkit.Color;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Display.Billboard;
import org.jetbrains.annotations.NotNull;
import tech.blastmc.holograms.api.models.Hologram;

import java.util.HashMap;
import java.util.Map;

/**
 * An instance of {@link HologramLine HologramLine} that adds spacing between the lines adjacent to it<br>
 * Use {@link #of(float) Offset#of(float)} to create a new instance<br>
 * After setting a value here, {@link Hologram#update() Hologram#update} must be called for changes to apply
 */
@Data
@SerializableAs("Offset")
public class Offset implements HologramLine, ConfigurationSerializable {

	private float value;
	private int index;

	public static Offset of(float value) {
		return new Offset(value);
	}

	private Offset(float value) { this.value = value; }

	public Offset(Map<String, Object> map) {
		this.value =  Float.valueOf(map.getOrDefault("value", 0).toString());
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		return new HashMap<>() {{
			put("value", value);
		}};
	}

	@Override
	public Object getData() {
		return value;
	}

	@Override
	public void setData(Object data) {
		this.value = (float) data;
	}

	@Override
	public Hologram getHologram() {
		return null;
	}

	@Override
	public Float getRange() {
		return null;
	}

	@Override
	public void setRange(Float range) { }

	@Override
	public Billboard getBillboard() {
		return null;
	}

	@Override
	public void setBillboard(Billboard billboard) { }

	@Override
	public Color getGlowColor() {
		return null;
	}

	@Override
	public void setGlowColor(Color color) { }

	@Override
	public Integer getBlockLight() {
		return null;
	}

	@Override
	public Integer getSkyLight() {
		return null;
	}

	@Override
	public void setBrightness(Integer blockLight, Integer skyLight) { }
}
