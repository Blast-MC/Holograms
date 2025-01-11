package tech.blastmc.holograms.utils;

import gg.projecteden.commands.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.commands.util.JsonBuilder;
import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tech.blastmc.holograms.Holograms;
import tech.blastmc.holograms.utils.protocol.ProtocolManager.PacketListener;
import tech.blastmc.holograms.utils.protocol.Reflection;
import tech.blastmc.holograms.utils.protocol.Reflection.FieldAccessor;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

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

		sender.sendBlockChange(map.get(sender.getUniqueId()).location, Material.AIR.createBlockData());
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
		private Location location;

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

			Location loc = player.getLocation().toBlockLocation().clone();
			this.location = loc;

			List<Component> lines = Arrays.stream(this.lines)
				.map(line -> new JsonBuilder(line).build().asComponent())
				.collect(Collectors.toList());

			while (lines.size() < 4)
				lines.add(Component.text(""));

			BlockPos pos = new BlockPos(this.location.getBlockX(), this.location.getBlockY(), this.location.getBlockZ());

			SignBlockEntity sign = new SignBlockEntity(pos, Blocks.OAK_SIGN.defaultBlockState());
			SignText signText = sign.getText(true);

			for (int i = 0; i < lines.size(); i++)
				signText = signText.setMessage(i, new AdventureComponent(lines.get(i)));
			sign.setText(signText, true);

			player.sendBlockChange(loc, Material.OAK_SIGN.createBlockData());
			sign.setLevel(PacketUtils.toNMS(player.getWorld()));
			PacketUtils.send(player, sign.getUpdatePacket());
			sign.setLevel(null);

			ClientboundOpenSignEditorPacket openPacket = new ClientboundOpenSignEditorPacket(pos, true);
			PacketUtils.send(player, openPacket);
		}

	}

}
