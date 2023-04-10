package tech.blastmc.holograms.utils;

import gg.projecteden.commands.exceptions.postconfigured.InvalidInputException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tech.blastmc.holograms.Holograms;
import tech.blastmc.holograms.utils.protocol.ProtocolManager.PacketListener;
import tech.blastmc.holograms.utils.protocol.Reflection;
import tech.blastmc.holograms.utils.protocol.Reflection.FieldAccessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class SignInputGUIListener extends PacketListener implements Listener {

	static Map<UUID, SignInputGUI> map = new HashMap<>();
	private static Class<?> packet = Reflection.getClass("net.minecraft.network.protocol.game.PacketPlayInUpdateSign");
	private FieldAccessor<String[]> linesAccessor = Reflection.getField(packet, String[].class, 0);

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		map.remove(event.getPlayer().getUniqueId());
	}

	@Override
	public void onReceive(Player sender, Object packet) {
		if (!linesAccessor.hasField(packet))
			throw new InvalidInputException("Input packet does not have lines object");

		if (!map.containsKey(sender.getUniqueId())) return;

		SignInputGUI gui = map.get(sender.getUniqueId());
		if (gui.onFinish != null)
			gui.onFinish.accept(sender, linesAccessor.get(packet));

		map.remove(sender.getUniqueId());

		super.onReceive(sender, packet);
	}

	static {
		SignInputGUIListener inst = new SignInputGUIListener();
		Holograms.getInstance().getProtocol().registerListener(packet, inst);
		Holograms.registerListener(inst);
	}

	public static class SignInputGUI {

		private String[] lines;
		private BiConsumer<Player, String[]> onFinish;

		private SignInputGUI(String[] lines) {
			this.lines = lines;
		}

		public static SignInputGUI of(String... lines) {
			return new SignInputGUI(lines);
		}

		public static SignInputGUI of(List<String> lines) {
			return of(lines.toArray(new String[0]));
		}

		public SignInputGUI onFinish(BiConsumer<Player, String[]> onFinish) {
			this.onFinish = onFinish;
			return this;
		}

		public void open(Player player) {
			SignInputGUIListener.map.put(player.getUniqueId(), this);

			Location loc = player.getLocation().toBlockLocation().clone().add(0, 20, 0);
				while (loc.getBlock().getType() != Material.AIR)
					loc.subtract(0, 1, 0);
			player.sendBlockChange(loc, Material.OAK_SIGN.createBlockData());
			player.sendSignChange(loc, lines);

			ClientboundOpenSignEditorPacket openPacket = new ClientboundOpenSignEditorPacket(new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			PacketUtils.send(player, openPacket);

			player.sendBlockChange(loc, Material.AIR.createBlockData());
		}

	}

}
