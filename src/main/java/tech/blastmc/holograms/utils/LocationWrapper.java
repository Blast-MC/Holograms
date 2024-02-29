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
	float yaw, pitch;

	public LocationWrapper(Location location) {
		this.world = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}

	public LocationWrapper(Map<String, Object> map) {
		this.world = (String) map.getOrDefault("world", null);
		this.x = (double) map.getOrDefault("x", x);
		this.y = (double) map.getOrDefault("y", y);
		this.z = (double) map.getOrDefault("z", z);
		try {
			this.yaw = Float.parseFloat(map.getOrDefault("yaw", yaw).toString());
			this.pitch = Float.parseFloat(map.getOrDefault("pitch", pitch).toString());
		} catch (Exception ignore) { }
	}

	public LocationWrapper(String world, double x, double y, double z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		return new LinkedHashMap<>() {{
			put("world", LocationWrapper.this.world);
			put("x", LocationWrapper.this.x);
			put("y", LocationWrapper.this.y);
			put("z", LocationWrapper.this.z);
			if (yaw != 0)
				put("yaw", LocationWrapper.this.yaw);
			if (pitch != 0)
				put("pitch", LocationWrapper.this.pitch);
		}};
	}

	public Location toLocation() throws InvalidInputException {
		World world;
		try {
			world = Bukkit.getWorld(this.world);
		} catch (Exception ignore) {
			throw new InvalidInputException("World " + this.world + " does not exist");
		}
		return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
	}
}
