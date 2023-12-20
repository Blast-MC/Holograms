package tech.blastmc.holograms.utils;

import lombok.SneakyThrows;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import tech.blastmc.holograms.utils.protocol.Reflection;

import java.lang.reflect.Method;
import java.util.List;

import static tech.blastmc.holograms.utils.StringUtils.colorize;

public class PacketUtils {

	public static Level toNMS(World world) {
		Object craftWorld = Reflection.getCraftBukkitClass("CraftWorld").cast(world);
		Reflection.MethodInvoker method = Reflection.getMethod("{obc}.CraftWorld", "getHandle");

		return (Level) method.invoke(craftWorld);
	}

	public static BlockState toNMS(BlockData blockData) {
		Object craftBlockData = Reflection.getClass("{obc}.block.data.CraftBlockData").cast(blockData);
		Reflection.MethodInvoker method = Reflection.getMethod("{obc}.block.data.CraftBlockData", "getState");

		return (BlockState) method.invoke(craftBlockData);
	}

	public static Vec3 toNMS(Location location) {
		return new Vec3(location.getX(), location.getY(), location.getZ());
	}

	public static ItemStack toNMS(org.bukkit.inventory.ItemStack item) {
		Reflection.MethodInvoker method = Reflection.getMethod("{obc}.inventory.CraftItemStack", "asNMSCopy", org.bukkit.inventory.ItemStack.class);
		return (ItemStack) method.invoke(null, item);
	}

	@SneakyThrows
	public static List<DataValue<?>> packAll(Entity display) {
		final SynchedEntityData entityData = display.getEntityData();
		final Method packAll = entityData.getClass().getDeclaredMethod("packAll");
		packAll.setAccessible(true);
		return (List<DataValue<?>>) packAll.invoke(entityData);
	}

	public static Component toNMS(String text) {
		Class<?> clazz = Reflection.getClass("{obc}.util.CraftChatMessage");
		Reflection.MethodInvoker method = Reflection.getTypedMethod(clazz, "fromStringOrNull", Component.class, String.class);
		return ((Component) method.invoke(null, colorize(text)));
	}

	public static void send(Player player, Packet... packets) {
		Object craftPlayer = Reflection.getClass("{obc}.entity.CraftPlayer").cast(player);
		Reflection.MethodInvoker method = Reflection.getMethod("{obc}.entity.CraftPlayer", "getHandle");

		ServerPlayer serverPlayer = (ServerPlayer) method.invoke(craftPlayer);

		for (Packet packet : packets)
			serverPlayer.connection.send(packet);
	}

}
