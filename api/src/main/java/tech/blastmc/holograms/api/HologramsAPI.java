package tech.blastmc.holograms.api;

import org.bukkit.World;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.HologramBuilder;

import java.util.List;

/**
 * TODO:
 *  - Javadoc
 *      - TextLine
 *  - Powerups
 */
public abstract class HologramsAPI {

	protected static HologramsAPI instance;

	/**
	 * Constructs a {@link HologramBuilder HologramBuilder} instance
	 * @return an instance of {@link HologramBuilder HologramBuilder}
	 */
	public static HologramBuilder builder() {
		return HologramsAPI.instance.builderImpl();
	}

	/**
	 * Finds all holograms loaded in the specified world
	 * @param world The world in which to retrieve holograms
	 * @return A list with {@link Hologram Hologram} instances, or empty if none exist
	 */
	public static List<Hologram> getHolograms(World world) {
		return HologramsAPI.instance.getHologramsImpl(world);
	}

	/**
	 * Retrieves a {@link Hologram hologram} by world and id
	 * @param world The world of the hologram
	 * @param id The id of the hologram
	 * @return An instance of Hologram, or else null if none
	 */
	public static Hologram byId(World world, String id) {
		return HologramsAPI.instance.byIdImpl(world, id);
	}

	protected abstract Hologram byIdImpl(World world, String id);

	protected abstract HologramBuilder builderImpl();

	protected abstract List<Hologram> getHologramsImpl(World world);

}
