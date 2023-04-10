package tech.blastmc.holograms.models;

import org.bukkit.World;
import tech.blastmc.holograms.Database;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.HologramBuilder;

import java.util.HashSet;
import java.util.List;

public class HologramAPIImpl extends HologramsAPI {

	public HologramAPIImpl() {
		HologramsAPI.instance = this;
	}

	@Override
	protected Hologram byIdImpl(World world, String id) {
		return Database.getWorldsMap().getOrDefault(world.getName().toLowerCase().replace(" ", "_"), new HashSet<>()).stream()
			.filter(holo -> holo.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
	}

	@Override
	protected HologramBuilder builderImpl() {
		return new HologramBuilderImpl();
	}

	@Override
	protected List<Hologram> getHologramsImpl(World world) {
		return Database.getWorldsMap().getOrDefault(world.getName().toLowerCase().replace(" ", "_"), new HashSet<>()).stream()
			.map(holo -> (Hologram) holo).toList();
	}

}
