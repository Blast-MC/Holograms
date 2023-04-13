package tech.blastmc.holograms.utils;

import lombok.SneakyThrows;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.List;

import static tech.blastmc.holograms.utils.StringUtils.colorize;

public class PacketUtils {

	public static Level toNMS(World world) {
		return ((CraftWorld) world).getHandle();
	}


	public static BlockState toNMS(BlockData blockData) {
		return ((CraftBlockData) blockData).getState();
	}

	public static Vec3 toNMS(Location location) {
		return new Vec3(location.getX(), location.getY(), location.getZ());
	}

	public static ItemStack toNMS(org.bukkit.inventory.ItemStack item) {
		return CraftItemStack.asNMSCopy(item);
	}

	@SneakyThrows
	public static List<DataValue<?>> packAll(Entity display) {
		final SynchedEntityData entityData = display.getEntityData();
		final Method packAll = entityData.getClass().getDeclaredMethod("packAll");
		packAll.setAccessible(true);
		return (List<DataValue<?>>) packAll.invoke(entityData);
	}

	public static Component toNMS(String text) {
		return CraftChatMessage.fromString(colorize(text), true)[0];
	}

	public static void send(Player player, Packet... packets) {
		for (Packet packet : packets)
			((CraftPlayer) player).getHandle().connection.send(packet);
	}

}
