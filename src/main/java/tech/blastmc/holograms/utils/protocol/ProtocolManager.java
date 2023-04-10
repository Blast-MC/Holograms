package tech.blastmc.holograms.utils.protocol;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtocolManager extends TinyProtocol {

	Map<String, List<PacketListener>> listeners = new HashMap<>();

	public ProtocolManager(Plugin plugin) {
		super(plugin);
	}

	public void registerListener(Class packet, PacketListener listener) {
		listeners.putIfAbsent(packet.getSimpleName(), new ArrayList<>());
		listeners.get(packet.getSimpleName()).add(listener);
	}

	@Override
	public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
		if (listeners.containsKey(packet.getClass().getSimpleName()))
			listeners.get(packet.getClass().getSimpleName()).forEach(listener -> listener.onReceive(sender, packet));

		return super.onPacketInAsync(sender, channel, packet);
	}

	@Override
	public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
		if (listeners.containsKey(packet.getClass().getSimpleName()))
			listeners.get(packet.getClass().getSimpleName()).forEach(listener -> listener.onSend(receiver, packet));

		return super.onPacketOutAsync(receiver, channel, packet);
	}

	public abstract static class PacketListener {

		public void onReceive(Player sender, Object packet) {
		}

		public void onSend(Player receiver, Object packet) {
		}

	}



}
