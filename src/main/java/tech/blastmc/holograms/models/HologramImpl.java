package tech.blastmc.holograms.models;

import com.mojang.math.Transformation;
import gg.projecteden.commands.exceptions.postconfigured.InvalidInputException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.minecraft.network.protocol.game.*;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Display.BlockDisplay;
import net.minecraft.world.entity.Display.ItemDisplay;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.phys.AABB;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import tech.blastmc.holograms.Database;
import tech.blastmc.holograms.Holograms;
import tech.blastmc.holograms.api.events.HologramLineSpawnEvent;
import tech.blastmc.holograms.api.events.HologramSpawnEvent;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.line.HologramLine;
import tech.blastmc.holograms.api.models.line.Offset;
import tech.blastmc.holograms.commands.edit.GlobalPage.GlobalSetting;
import tech.blastmc.holograms.models.line.BlockLineImpl;
import tech.blastmc.holograms.models.line.HologramLineImpl;
import tech.blastmc.holograms.models.line.ItemLineImpl;
import tech.blastmc.holograms.models.line.TextLineImpl;
import tech.blastmc.holograms.utils.LocationWrapper;
import tech.blastmc.holograms.utils.PacketUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

@ToString
@SerializableAs("Hologram")
@AllArgsConstructor
@NoArgsConstructor
public class HologramImpl implements ConfigurationSerializable, Hologram {

	@Getter
	@Setter
	private String id;
	protected LocationWrapper location;
	@Getter
	@Setter
	private boolean persistent = false;
	@Getter
	private Float range = 9999f;
	@Getter
	private Float shadowRadius;
	@Getter
	private Float shadowStrength;
	@Getter
	private Billboard billboard = Billboard.VERTICAL;
	@Getter
	private Color glowColor;
	@Getter
	private Integer blockLight;
	@Getter
	private Integer skyLight;
	@Getter
	protected List<HologramLine> lines;
	@Getter
	private Integer lineWidth = 9999;
	@Getter
	private Color background;
	@Getter
	private Byte opacity;
	@Getter
	private Boolean shadowed = false;
	@Getter
	private Boolean seeThrough = false;
	@Getter
	private TextAlignment alignment = TextAlignment.CENTER;
	@Getter
	private Boolean mirror;
	@Getter
	private ItemDisplayTransform itemTransform = ItemDisplayTransform.GROUND;

	Map<UUID, Map<Integer, Object>> playerDataMap = new HashMap<>();

	public HologramImpl(Map<String, Object> map) {
		this.location = (LocationWrapper) map.getOrDefault("location", location);
		if (map.containsKey("range"))
			this.range = Float.parseFloat(map.get("range").toString());
		if (map.containsKey("shadowRadius"))
			this.shadowRadius = Float.parseFloat(map.get("shadowRadius").toString());
		if (map.containsKey("shadowStrength"))
			this.shadowStrength = Float.parseFloat(map.get("shadowStrength").toString());
		if (map.containsKey("billboard"))
			this.billboard = Billboard.valueOf(map.get("billboard").toString());
		if (map.containsKey("glowColor"))
			this.glowColor = (Color) map.get("glowColor");
		if (map.containsKey("blockLight"))
			this.blockLight = Integer.parseInt(map.get("blockLight").toString());
		if (map.containsKey("skyLight"))
			this.skyLight = Integer.parseInt(map.get("skyLight").toString());
		if (map.containsKey("lineWidth"))
			this.lineWidth = Integer.parseInt(map.get("lineWidth").toString());
		if (map.containsKey("background"))
			this.background = (Color) map.get("background");
		if (map.containsKey("opacity"))
			this.opacity = Byte.parseByte(map.get("opacity").toString());
		if (map.containsKey("shadowed"))
			this.shadowed = (boolean) map.get("shadowed");
		if (map.containsKey("seeThrough"))
			this.seeThrough = (boolean) map.get("seeThrough");
		if (map.containsKey("alignment"))
			this.alignment = TextAlignment.valueOf(map.get("alignment").toString());
		if (map.containsKey("withMirror"))
			this.mirror = (Boolean) map.get("withMirror");
		if (map.containsKey("itemTransform"))
			this.itemTransform = ItemDisplayTransform.valueOf(map.get("itemTransform").toString());

		this.lines = (ArrayList<HologramLine>) map.getOrDefault("lines", lines);
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		return new LinkedHashMap<>() {{
			put("location", location);
			if (range != null && range != 9999F)
				put("range", range);
			if (shadowRadius != null && shadowRadius != 0F)
				put("shadowRadius", shadowRadius);
			if (shadowStrength != null && shadowStrength != 0F)
				put("shadowStrength", shadowStrength);
			if (billboard != null && billboard != Billboard.VERTICAL)
				put("billboard", billboard.name());
			if (glowColor != null)
				put("glowColor", glowColor);
			if (blockLight != null)
				put("blockLight", blockLight);
			if (skyLight != null)
				put("skyLight", skyLight);
			if (lineWidth != null && lineWidth != 9999)
				put("lineWidth", lineWidth);
			if (background != null)
				put("background", background);
			if (opacity != null)
				put("opacity", opacity);
			if (shadowed != null && shadowed)
				put("shadowed", shadowed);
			if (seeThrough != null && seeThrough)
				put("seeThrough", seeThrough);
			if (alignment != null && alignment != TextAlignment.CENTER)
				put("alignment", alignment.name());
			if (mirror != null)
				put("withMirror", mirror);
			if (itemTransform != null && itemTransform != ItemDisplayTransform.GROUND)
				put("itemTransform", itemTransform.name());
			put("lines", lines);
		}};
	}

	@Override
	public void spawn() {
		generate();
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getWorld().getName().equalsIgnoreCase(location.getWorld()))
				if (new HologramSpawnEvent(player, this).callEvent())
					showToPlayer(player);
	}

	public void generate() {
		if (lines == null || lines.isEmpty())
			throw new NullPointerException("Attempted to spawn a hologram with no lines");

		despawn();

		Location loc;
		try {
			loc = location.toLocation();
		} catch (InvalidInputException ignore) {
			return;
		}

		Collections.reverse(lines);
		int index = lines.size() - 1;
		for (HologramLine line : lines) {
			if (line instanceof Offset offset) {
				loc.add(0, offset.getValue(), 0);
			}
			else if (line != null) {

				HologramLineImpl holoLine = (HologramLineImpl) line;
				holoLine.setHologram(this);
				Display displayEntity = holoLine.render(loc.clone());

				if (index == (lines.size() - 1)) {
					if (shadowRadius != null && shadowStrength != null) {
						displayEntity.setShadowRadius(shadowRadius);
						displayEntity.setShadowStrength(shadowStrength);
					}
				}

				holoLine.setDisplay(displayEntity);
				holoLine.setIndex(index--);

				if (holoLine instanceof TextLineImpl text) {
					text.setMirror(null);
					if (GlobalSetting.BILLBOARD.get(holoLine) == Billboard.FIXED && GlobalSetting.MIRROR.get(holoLine) != null && ((Boolean) GlobalSetting.MIRROR.get(holoLine))) {
						Location mirrorLoc = loc.clone();
						mirrorLoc.setYaw(mirrorLoc.getYaw() + 180);
						mirrorLoc.setPitch(-mirrorLoc.getPitch());
						Display mirrorDisplay = holoLine.render(mirrorLoc);
						text.setMirror(mirrorDisplay);
					}
				}
				holoLine.applyDefaults(range, billboard, glowColor, itemTransform, lineWidth, background, opacity, shadowed, seeThrough, alignment, mirror);

				if (holoLine.getOnClick() != null) {
					ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, PacketUtils.toNMS(loc.getWorld()));
					armorStand.setInvisible(true);
					armorStand.setSmall(true);
					armorStand.setNoGravity(true);
					armorStand.setPos(PacketUtils.toNMS(loc.clone().subtract(0, .25, 0)));
					armorStand.setRot(loc.getYaw(), loc.getPitch());
					holoLine.setInteractEntity(armorStand);
				}

				loc.add(0, getYAdditional(holoLine), 0);
			}
			else
				throw new NullPointerException("Invalid hologram type: " + line.getClass().getSimpleName());
		}
		Collections.reverse(lines);
	}

	@Override
	public void showToPlayer(Player player) {
		hideFromPlayer(player);
		if (!player.getWorld().getName().equalsIgnoreCase(location.getWorld()))
			return;
		for (HologramLine line : lines) {
			if (line instanceof Offset) continue;
			HologramLineSpawnEvent event = new HologramLineSpawnEvent(player, this, line);
			if (!event.callEvent())
				continue;

			if (!line.getData().equals(event.getData())) {
				Map<Integer, Object> map = playerDataMap.getOrDefault(player.getUniqueId(), new HashMap<>());
				map.put(line.getIndex(), event.getData());
				playerDataMap.put(player.getUniqueId(), map);
			}

			HologramLineImpl impl = (HologramLineImpl) line;

			sendSpawnPacket(player, impl);
			sendMetaPacket(player, impl, impl.getDisplay());
		}
	}

	@Override
	public void hideFromPlayer(Player player) {
		if (lines == null)
			return;
		lines.stream().filter(Objects::nonNull).filter(line -> !(line instanceof Offset)).map(line -> (HologramLineImpl) line).forEach(line -> {
			if (line == null || line.getDisplay() == null) return;
			ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(line.getDisplay().getId());
			PacketUtils.send(player, packet);
			if (line instanceof TextLineImpl text)
				if (text.getMirror() != null) {
					ClientboundRemoveEntitiesPacket mirror = new ClientboundRemoveEntitiesPacket(text.getMirror().getId());
					PacketUtils.send(player, mirror);
				}
		});
	}

	public void sendSpawnPacket(Player player, HologramLineImpl line) {
		ClientboundAddEntityPacket addPacket = new ClientboundAddEntityPacket(line.getDisplay());
		PacketUtils.send(player, addPacket);

		ClientboundTeleportEntityPacket teleportEntityPacket = new ClientboundTeleportEntityPacket(line.getDisplay());
		PacketUtils.send(player, teleportEntityPacket);

		if (line instanceof TextLineImpl text) {
			if (text.getMirror() != null) {
				ClientboundAddEntityPacket mirrorPacket = new ClientboundAddEntityPacket(((TextLineImpl) line).getMirror());
				PacketUtils.send(player, mirrorPacket);
			}
		}

		if (line.getInteractEntity() != null) {
			ClientboundAddEntityPacket interactPacket = new ClientboundAddEntityPacket(line.getInteractEntity());
			PacketUtils.send(player, interactPacket);

			ClientboundTeleportEntityPacket teleportEntityPacket2 = new ClientboundTeleportEntityPacket(line.getInteractEntity());
			PacketUtils.send(player, teleportEntityPacket2);
		}
	}

	public void sendMetaPacket(Player player, HologramLineImpl line, Display display) {
		Object data = line.getData();
		Object original = data;
		if (playerDataMap.getOrDefault(player.getUniqueId(), new HashMap<>()).containsKey(line.getIndex())) {
			data = playerDataMap.get(player.getUniqueId()).get(line.getIndex());
			try { setDisplayData(display, data); }
			catch (IllegalArgumentException ex) { playerDataMap.get(player.getUniqueId()).remove(line.getIndex()); } // Updated line type
		}
		ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(line.getDisplay().getId(), PacketUtils.packAll(line.getDisplay()));
		PacketUtils.send(player, dataPacket);
		setDisplayData(display, original);

		if (line.getInteractEntity() != null) {
			ClientboundSetEntityDataPacket interactPacket = new ClientboundSetEntityDataPacket(line.getInteractEntity().getId(), PacketUtils.packAll(line.getInteractEntity()));
			PacketUtils.send(player, interactPacket);
		}

		if (line instanceof TextLineImpl text)
			if (text.getMirror() != null) {
				if (playerDataMap.getOrDefault(player.getUniqueId(), new HashMap<>()).containsKey(line.getIndex())) {
					try { setDisplayData(text.getMirror(), data); }
					catch (IllegalArgumentException ignore) { }
				}
				ClientboundSetEntityDataPacket mirrorPacket = new ClientboundSetEntityDataPacket(text.getMirror().getId(), PacketUtils.packAll(line.getDisplay()));
				PacketUtils.send(player, mirrorPacket);
				setDisplayData(text.getMirror(), original);
			}
	}

	private void setDisplayData(Display display, Object data) {
		if (data instanceof String str && display instanceof TextDisplay textDisplay)
			textDisplay.setText(PacketUtils.toNMS(str));
		else if (data instanceof BlockData blockData && display instanceof BlockDisplay blockDisplay)
			blockDisplay.setBlockState(PacketUtils.toNMS(blockData));
		else if (data instanceof ItemStack item && display instanceof ItemDisplay itemDisplay)
			itemDisplay.setItemStack(PacketUtils.toNMS(item));
		else
			throw new IllegalArgumentException(String.format("Invalid data '%s' for type %s", data.getClass().getSimpleName(), display.getClass().getSimpleName()));
	}

	private double getYAdditional(HologramLineImpl line) {
		Display displayEntity = line.getDisplay();
		Transformation transformation = Display.createTransformation(displayEntity.getEntityData());
		float y = transformation.getTranslation().y();
		float height = 0;
		if (displayEntity instanceof TextDisplay)
			height = .3F;
		if (displayEntity instanceof ItemDisplay)
			height = .35F;
		if (displayEntity instanceof BlockDisplay)
			height = 1.3F;

		y += transformation.getScale().y() * height;
		return y;
	}

	public List<HologramLine> setLines(List<Object> lines) {
		for (int i = 0; i < lines.size(); i++)
			setLine(i, lines.get(i));
		return this.lines;
	}

	public void setLinesRaw(List<HologramLine> lines) {
		this.lines = lines;
	}

	@Override
	public HologramLine setLine(int index, Object line) {
		if (lines == null)
			lines = new ArrayList<>();
		if (line instanceof String str && lines.size() > index && lines.get(index) instanceof TextLineImpl text) {
			((TextDisplay) text.getDisplay()).setText(PacketUtils.toNMS(str));
			if (text.getMirror() != null)
				((TextDisplay) text.getMirror()).setText(PacketUtils.toNMS(str));
			text.setText(str);
			updateBasics();
			return text;
		}
		else {
			HologramLine holoLine = convert(line);
			despawn();
			if (lines.size() <= index)
				lines.add(holoLine);
			else
				lines.set(index, holoLine);
			spawn();
			return holoLine;
		}
	}

	@Override
	public HologramLine addLine(Object line) {
		if (lines == null)
			lines = new ArrayList<>();
		HologramLine holoLine = convert(line);
		lines.add(holoLine);
		update();
		return holoLine;
	}

	@Override
	public HologramLine addLine(int index, Object line) {
		if (lines == null)
			lines = new ArrayList<>();
		HologramLine holoLine = convert(line);
		lines.add(index, holoLine);
		update();
		return holoLine;
	}

	@Override
	public void removeLine(int index) {
		if (lines == null)
			lines = new ArrayList<>();
		despawn();
		lines.remove(index);
		if (!lines.isEmpty())
			spawn();
	}

	@Override
	public void setRange(float range) {
		this.range = range;
		updateBasics();
	}

	@Override
	public void setShadow(Float shadowRadius, Float shadowStrength) {
		this.shadowRadius = shadowRadius;
		this.shadowStrength = shadowStrength;
		update();
	}

	@Override
	public void setBillboard(Billboard billboard) {
		this.billboard = billboard;
		update();
	}

	@Override
	public void setGlowColor(Color color) {
		this.glowColor = color;
		updateBasics();
	}

	@Override
	public void setBrightness(Integer blockLight, Integer skyLight) {
		this.blockLight = blockLight;
		this.skyLight = skyLight;
		updateBasics();
	}

	@Override
	public Location getLocation() {
		try {
			return this.location.toLocation();
		} catch (InvalidInputException ex) {
			Holograms.warn(ex.getMessage());
		}
		return null;
	}

	public LocationWrapper getLocationWrapper() {
		return this.location;
	}

	@Override
	public void setLocation(Location location) {
		this.location = new LocationWrapper(location);
		update();
	}

	@Override
	public void setAlignment(TextAlignment alignment) {
		this.alignment = alignment;
		update();
	}

	@Override
	public void setMirror(Boolean mirror) {
		this.mirror = mirror;
		update();
	}

	@Override
	public void setLineWidth(Integer width) {
		this.lineWidth = width;
		update();
	}

	@Override
	public void setBackground(Color color) {
		this.background = color;
		update();
	}

	@Override
	public void setOpacity(Byte opacity) {
		this.opacity = opacity;
		update();
	}

	@Override
	public void setShadowed(Boolean shadowed) {
		this.shadowed = shadowed;
		update();
	}

	@Override
	public void setSeeThrough(Boolean seeThrough) {
		this.seeThrough = seeThrough;
		update();
	}

	@Override
	public void setItemTransform(ItemDisplayTransform transform) {
		this.itemTransform = transform;
		update();
	}

	@Override
	public void update() {
		spawn();
	}

	private void updateBasics() {
		lines.stream().filter(line -> !(line instanceof Offset)).map(line -> (HologramLineImpl) line).forEach(line -> {
			basicsConsumer.accept(line, line.getDisplay());
			if (line instanceof TextLineImpl text)
				if (text.getMirror() != null)
					basicsConsumer.accept(line, text.getMirror());
		});
	}

	private final BiConsumer<HologramLineImpl, Display> basicsConsumer = (line, display) -> {
		if (range != null)
			display.setViewRange(range);
		if (shadowRadius != null)
			display.setShadowRadius(shadowRadius);
		if (shadowStrength != null)
			display.setShadowStrength(shadowStrength);

		if (glowColor != null)
			display.setGlowColorOverride(glowColor.asARGB());
		if (blockLight != null && skyLight != null)
			display.setBrightnessOverride(new Brightness(blockLight, skyLight));

		for (Player player : Bukkit.getOnlinePlayers())
			sendMetaPacket(player, line, line.getDisplay());
	};

	public void despawn() {
		if (lines == null)
			return;
		lines.stream().filter(Objects::nonNull).filter(line -> !(line instanceof Offset)).map(line -> (HologramLineImpl) line).forEach(line -> {
			despawn(line);
		});
	}

	public void despawn(HologramLineImpl line) {
		if (line.getDisplay() == null) return;
		ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(line.getDisplay().getId());
		for (Player player : Bukkit.getOnlinePlayers()) {
			PacketUtils.send(player, packet);
		}
		if (line instanceof TextLineImpl text) {
			if (text.getMirror() != null) {
				ClientboundRemoveEntitiesPacket mirror = new ClientboundRemoveEntitiesPacket(text.getMirror().getId());
				for (Player player : Bukkit.getOnlinePlayers()) {
					PacketUtils.send(player, mirror);
				}
			}
		}

		if (line.getInteractEntity() != null) {
			ClientboundRemoveEntitiesPacket interact = new ClientboundRemoveEntitiesPacket(line.getInteractEntity().getId());
			for (Player player : Bukkit.getOnlinePlayers())
				PacketUtils.send(player, interact);
		}
	}

	@Override
	public void remove() {
		despawn();
		lines.clear();
		Database.remove(this);
	}

	public List<HologramLine> convert(List<Object> lines) {
		return new ArrayList<>(lines.stream().map(this::convert).toList());
	}

	public HologramLine convert(Object line) {
		HologramLine holoLine;
		if (line instanceof String str)
			holoLine = new TextLineImpl(this, str, lineWidth, background, opacity, shadowed, seeThrough, alignment, mirror);
		else if (line instanceof ItemStack item)
			holoLine = new ItemLineImpl(this, item, itemTransform);
		else if (line instanceof BlockData block)
			holoLine = new BlockLineImpl(this, block);
		else if (line instanceof Offset offset)
			holoLine = offset;
		else throw new NullPointerException("Invalid hologram type: " + line.getClass().getSimpleName());
		return holoLine;
	}

	@Override
	public void save() {
		Database.save(this);
	}
}
