package tech.blastmc.holograms.utils;

import gg.projecteden.commands.exceptions.postconfigured.InvalidInputException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@SerializableAs("Location")
@AllArgsConstructor
@NoArgsConstructor
public class LocationWrapper implements ConfigurationSerializable {

	String world;
	double x, y, z;

	public LocationWrapper(Location location) {
		this.world = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
	}

	public LocationWrapper(Map<String, Object> map) {
		this.world = (String) map.getOrDefault("world", null);
		this.x = (Double) map.getOrDefault("x", x);
		this.y = (Double) map.getOrDefault("y", y);
		this.z = (Double) map.getOrDefault("z", z);
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		return new LinkedHashMap<>() {{
			put("world", LocationWrapper.this.world);
			put("x", LocationWrapper.this.x);
			put("y", LocationWrapper.this.y);
			put("z", LocationWrapper.this.z);
		}};
	}

	public Location toLocation() throws InvalidInputException {
		World world;
		try {
			world = Bukkit.getWorld(this.world);
		} catch (Exception ignore) {
			throw new InvalidInputException("World " + this.world + " does not exist");
		}
		return new Location(world, this.x, this.y, this.z);
	}
}
