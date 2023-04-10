package tech.blastmc.holograms.api.models;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.TextDisplay.TextAligment;

import java.util.List;

/**
 * A builder interface for creating new {@link Hologram Holograms} in the world.
 * Use {@link tech.blastmc.holograms.api.HologramsAPI HologramsAPI} to create a new builder instance.
 */
public interface HologramBuilder {

	HologramBuilder id(String id);

	HologramBuilder location(Location location);

	HologramBuilder persistent(boolean persistent);

	HologramBuilder range(Integer range);

	HologramBuilder billboard(Billboard billboard);

	HologramBuilder glowColor(Color color);

	HologramBuilder lines(List<Object> lines);

	HologramBuilder lineWidth(Integer width);

	HologramBuilder background(Color background);

	HologramBuilder opacity(Byte opacity);

	HologramBuilder shadowed(Boolean shadowed);

	HologramBuilder seeThrough(Boolean seeThrough);

	HologramBuilder alignment(TextAligment alignment);

	HologramBuilder itemTransform(ItemDisplayTransform itemTransform);

	HologramBuilder shadow(float radius, float strength);

	HologramBuilder brightness(int blockLight, int skyLight);

	HologramBuilder lines(Object... lines);

	HologramBuilder background(int a, int r, int g, int b);

	HologramBuilder transparent();

	HologramBuilder withMirror();

	Hologram spawn();

	Hologram build();

}
