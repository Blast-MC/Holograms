package tech.blastmc.holograms.models;

import gg.projecteden.commands.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.TextDisplay.TextAlignment;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.HologramBuilder;
import tech.blastmc.holograms.utils.LocationWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HologramBuilderImpl implements HologramBuilder {

	private String id;
	private LocationWrapper location;
	private boolean persistent = false;
	private float range = 9999;
	private float shadowRadius, shadowStrength;
	private Billboard billboard = Billboard.VERTICAL;
	private Color glowColor;
	private Integer blockLight, skyLight;
	private List<Object> lines;
	private Integer lineWidth = 9999;
	private Color background;
	private Byte opacity;
	private Boolean shadowed = false;
	private Boolean seeThrough = false;
	private TextAlignment alignment = TextAlignment.CENTER;
	private Boolean withMirror;
	private ItemDisplayTransform itemTransform = ItemDisplayTransform.GROUND;

	@Override
	public HologramBuilder id(String id) {
		this.id = id;
		return this;
	}

	@Override
	public HologramBuilder location(Location location) {
		this.location = new LocationWrapper(location);
		return this;
	}

	public HologramBuilder location(LocationWrapper wrapper) {
		this.location = wrapper;
		return this;
	}

	@Override
	public HologramBuilder persistent(boolean persistent) {
		this.persistent = persistent;
		return this;
	}

	@Override
	public HologramBuilder range(Integer range) {
		this.range = range;
		return this;
	}

	@Override
	public HologramBuilder billboard(Billboard billboard) {
		this.billboard = billboard;
		return this;
	}

	@Override
	public HologramBuilder glowColor(Color color) {
		this.glowColor = color;
		return this;
	}

	@Override
	public HologramBuilder lines(List<Object> lines) {
		this.lines = lines;
		return this;
	}

	@Override
	public HologramBuilder lineWidth(Integer width) {
		this.lineWidth = width;
		return this;
	}

	@Override
	public HologramBuilder opacity(Byte opacity) {
		this.opacity = opacity;
		return this;
	}

	@Override
	public HologramBuilder shadowed(Boolean shadowed) {
		this.shadowed = shadowed;
		return this;
	}

	@Override
	public HologramBuilder seeThrough(Boolean seeThrough) {
		this.seeThrough = seeThrough;
		return this;
	}

	@Override
	public HologramBuilder alignment(TextAlignment alignment) {
		this.alignment = alignment;
		return this;
	}

	@Override
	public HologramBuilder itemTransform(ItemDisplayTransform itemTransform) {
		this.itemTransform = itemTransform;
		return this;
	}

	@Override
	public HologramBuilderImpl shadow(Float radius, Float strength) {
		this.shadowRadius = radius;
		this.shadowStrength = strength;
		return this;
	}

	@Override
	public HologramBuilderImpl brightness(Integer blockLight, Integer skyLight) {
		this.blockLight = blockLight;
		this.skyLight = skyLight;
		return this;
	}

	@Override
	public HologramBuilderImpl lines(Object... lines) {
		this.lines = Arrays.asList(lines);
		return this;
	}

	@Override
	public HologramBuilder background(Color background) {
		this.background = background;
		return this;
	}

	@Override
	public HologramBuilderImpl background(int a, int r, int g, int b) {
		this.background = Color.fromARGB(a, r, g, b);
		return this;
	}

	@Override
	public HologramBuilderImpl transparent() {
		return this.background(0, 0, 0, 0);
	}

	@Override
	public HologramBuilder withMirror() {
		this.withMirror = true;
		return this;
	}

	@Override
	public Hologram spawn() {
		Hologram hologram = build();
		hologram.spawn();
		return hologram;
	}

	@Override
	public Hologram build() {
		if (location == null)
			throw new InvalidInputException("Location must not be null");
		if (id != null) {
			Hologram idHolo = HologramsAPI.byId(location.getWorld(), id);
			if (idHolo != null)
				throw new InvalidInputException("IDs must be unique per world");
		}
		else {
			throw new InvalidInputException("Cannot create a Hologram without an ID");
		}

		HologramImpl holo = new HologramImpl(id, location, persistent, range, shadowRadius, shadowStrength, billboard, glowColor, blockLight, skyLight, null, lineWidth, background, opacity, shadowed, seeThrough, alignment, withMirror, itemTransform,  new HashMap<>());
		holo.setLinesRaw(lines.stream().map(holo::convert).collect(Collectors.toList()));
		holo.save();
		return holo;
	}

}
