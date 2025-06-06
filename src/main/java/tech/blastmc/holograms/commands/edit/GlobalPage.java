package tech.blastmc.holograms.commands.edit;

import com.google.common.base.Strings;
import gg.projecteden.commands.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.commands.util.JsonBuilder;
import gg.projecteden.commands.util.Tasks;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.inventory.Book.Builder;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay.TextAlignment;
import tech.blastmc.holograms.Holograms;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.line.HologramLine;
import tech.blastmc.holograms.models.line.ItemLineImpl;
import tech.blastmc.holograms.models.line.TextLineImpl;
import tech.blastmc.holograms.utils.Percentage;
import tech.blastmc.holograms.utils.SignInputGUIListener.SignInputGUI;
import tech.blastmc.holograms.utils.StringUtils;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class GlobalPage extends EditPage {

	public GlobalPage(@NonNull Hologram hologram) {
		super(hologram);
	}

	@Override
	protected Builder render(Builder book) {
		JsonBuilder json = new JsonBuilder();
		addBackButton(json, Page.MAIN, 0);

		json.next("   &6&lGlobal Settings");
		json.group().next("\n")
			.group().next("\n")
			.next("        &3Location")
			.hover("&eModify location")
			.command("hologram shift " + hologram.getId() + " --gui");

		int i = 4;

		for (GlobalSetting setting : GlobalSetting.values()) {
			json.group().next("\n")
				.next("&f" + Strings.repeat(" ", setting.spacing))
				.next("&3" + StringUtils.camelCase(setting.name()));
			if (setting.type.isEnum())
				json.hover("&3Current Value: &e" + StringUtils.camelCase(((Enum) setting.get(hologram)).name()));
			else
				json.hover("&3Current Value: &e" + setting.get(hologram));
			String command = "hologram edit " + hologram.getId() + " global " + setting.name().toLowerCase();
			if (setting.type.isEnum() || setting.type == Boolean.class)
				command += " next";
			json.command(command)
				.group();

			if (++i == 14) {
				book.addPage(json.build());

				json = new JsonBuilder();
				addBackButton(json, Page.MAIN, 0);
				json.next("   &6&lGlobal Settings")
					.group().next("\n")
					.group().next("\n");
				i = 4;
			}
		}

		book.addPage(json.build());
		return book;
	}

	public enum GlobalSetting implements LineSetting {
		VIEW_RANGE(6, Float.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setRange((Float) data);
			}

			@Override
			public void apply(HologramLine line, Object data) {
				line.setRange((Float) data);
			}

			@Override
			public Object get(Hologram hologram) {
				return hologram.getRange();
			}

			@Override
			public Object get(HologramLine line) {
				if (line.getRange() != null && line.getRange() != get(line.getHologram()))
					return line.getRange();
				return get(line.getHologram());
			}
		},
		SCALE(9, Float.class) { // TODO
			@Override
			public void apply(Hologram hologram, Object data) {
				// TODO
			}

			@Override
			public void apply(HologramLine line, Object data) {

			}

			@Override
			public Object get(Hologram hologram) {
				return null;
			}

			@Override
			public Object get(HologramLine line) {
				return null;
			}
		},
		SHADOW_RADIUS(4, Float.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setShadow((Float) data, hologram.getShadowStrength());
			}

			@Override
			public void apply(HologramLine line, Object data) { }

			@Override
			public Object get(Hologram hologram) {
				return hologram.getShadowRadius();
			}

			@Override
			public Object get(HologramLine line) {
				return get(line.getHologram());
			}
		},
		SHADOW_STRENGTH(3, Float.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setShadow(hologram.getShadowRadius(), (Float) data);
			}

			@Override
			public void apply(HologramLine line, Object data) { }

			@Override
			public Object get(Hologram hologram) {
				return hologram.getShadowStrength();
			}

			@Override
			public Object get(HologramLine line) {
				return get(line.getHologram());
			}
		},
		BILLBOARD(7, Billboard.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setBillboard((Billboard) data);
			}

			@Override
			public void apply(HologramLine line, Object data) {
				line.setBillboard((Billboard) data);
			}

			@Override
			public Object get(Hologram hologram) {
				return hologram.getBillboard();
			}

			@Override
			public Object get(HologramLine line) {
				if (line.getBillboard() != null && line.getBillboard() != get(line.getHologram()))
					return line.getBillboard();
				return get(line.getHologram());
			}
		},
		GLOW_COLOR(6, Color.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setGlowColor((Color) data);
			}

			@Override
			public void apply(HologramLine line, Object data) {
				line.setGlowColor((Color) data);
			}

			@Override
			public Object get(Hologram hologram) {
				if (hologram.getGlowColor() == null)
					return null;
				return hologram.getGlowColor().getRed() + ", " + hologram.getGlowColor().getGreen() + ", " + hologram.getGlowColor().getBlue();
			}

			@Override
			public Object get(HologramLine line) {
				if (line.getGlowColor() != null && line.getGlowColor() != get(line.getHologram()))
					return line.getGlowColor();
				return get(line.getHologram());
			}

			@Override
			public String getName() {
				return "Glow Color (R,G,B,A)";
			}
		},
		BLOCK_LIGHT(6, Integer.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setBrightness(hologram.getBlockLight(), (Integer) data);
			}

			@Override
			public void apply(HologramLine line, Object data) {
				line.setBrightness((Integer) data, line.getSkyLight());
			}

			@Override
			public Object get(Hologram hologram) {
				return hologram.getBlockLight();
			}

			@Override
			public Object get(HologramLine line) {
				if (line.getBlockLight() != null && line.getBlockLight() != get(line.getHologram()))
					return line.getBlockLight();
				return get(line.getHologram());
			}
		},
		SKY_LIGHT(7, Integer.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setBrightness(hologram.getBlockLight(), (Integer) data);
			}

			@Override
			public void apply(HologramLine line, Object data) {
				line.setBrightness(line.getBlockLight(), (Integer) data);
			}

			@Override
			public Object get(Hologram hologram) {
				return hologram.getSkyLight();
			}

			@Override
			public Object get(HologramLine line) {
				if (line.getSkyLight() != null && line.getSkyLight() != get(line.getHologram()))
					return line.getSkyLight();
				return get(line.getHologram());
			}
		},
		LINE_WIDTH(6, Integer.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setLineWidth((Integer) data);
			}

			@Override
			public void apply(HologramLine line, Object data) {
				if (line instanceof TextLineImpl text) {
					text.setLineWidth((Integer) data);
					line.getHologram().update();
				}
			}

			@Override
			public Object get(Hologram hologram) {
				return hologram.getLineWidth();
			}

			@Override
			public Object get(HologramLine line) {
				if (line instanceof TextLineImpl text) {
					if (text.getLineWidth() != null && text.getLineWidth() != get(line.getHologram()))
						return text.getLineWidth();
					return get(line.getHologram());
				}
				return get(line.getHologram());
			}
		},
		BACKGROUND_COLOR(3, Color.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setBackground((Color) data);
			}

			@Override
			public void apply(HologramLine line, Object data) {
				if (line instanceof TextLineImpl text) {
					text.setBackground((Color) data);
					line.getHologram().update();
				}
			}

			@Override
			public Object get(Hologram hologram) {
				return hologram.getBackground();
			}

			@Override
			public Object get(HologramLine line) {
				if (line instanceof TextLineImpl text) {
					if (text.getBackground() != null && !text.getBackground().equals(get(line.getHologram())))
						return text.getBackground();
					return get(line.getHologram());
				}
				return get(line.getHologram());
			}
		},
		OPACITY(8, Percentage.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				Holograms.log("op: " + ((Byte) data));
				hologram.setOpacity((Byte) data);
			}

			@Override
			public void apply(HologramLine line, Object data) {
				if (line instanceof TextLineImpl text) {
					text.setOpacity((Byte) data);
					line.getHologram().update();
				}
			}

			@Override
			public Object get(Hologram hologram) {
				return hologram.getOpacity();
			}

			@Override
			public Object get(HologramLine line) {
				if (line instanceof TextLineImpl text) {
					if (text.getOpacity() != null && text.getOpacity() != get(line.getHologram()))
						return text.getOpacity();
					return get(line.getHologram());
				}
				return get(line.getHologram());
			}
		},
		SHADOWED(8, Boolean.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setShadowed((Boolean) data);
			}

			@Override
			public void apply(HologramLine line, Object data) {
				if (line instanceof TextLineImpl text) {
					text.setShadowed((Boolean) data);
					line.getHologram().update();
				}
			}

			@Override
			public Object get(Hologram hologram) {
				return hologram.getShadowed();
			}

			@Override
			public Object get(HologramLine line) {
				if (line instanceof TextLineImpl text) {
					if (text.getShadowed() != null && text.getShadowed() != get(line.getHologram()))
						return text.getShadowed();
					return get(line.getHologram());
				}
				return get(line.getHologram());
			}
		},
		SEE_THROUGH(6, Boolean.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setSeeThrough((Boolean) data);
			}

			@Override
			public void apply(HologramLine line, Object data) {
				if (line instanceof TextLineImpl text) {
					text.setSeeThrough((Boolean) data);
					line.getHologram().update();
				}
			}

			@Override
			public Object get(Hologram hologram) {
				return hologram.getSeeThrough();
			}

			@Override
			public Object get(HologramLine line) {
				if (line instanceof TextLineImpl text) {
					if (text.getSeeThrough() != null && text.getSeeThrough() != get(line.getHologram()))
						return text.getSeeThrough();
					return get(line.getHologram());
				}
				return get(line.getHologram());
			}
		},
		TEXT_ALIGNMENT(5, TextAlignment.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setAlignment((TextAlignment) data);
			}

			@Override
			public void apply(HologramLine line, Object data) {
				if (line instanceof TextLineImpl text) {
					text.setAlignment((TextAlignment) data);
					line.getHologram().update();
				}
			}

			@Override
			public Object get(Hologram hologram) {
				return hologram.getAlignment();
			}

			@Override
			public Object get(HologramLine line) {
				if (line instanceof TextLineImpl text) {
					if (text.getAlignment() != null && text.getAlignment() != get(line.getHologram()))
						return text.getAlignment();
					return get(line.getHologram());
				}
				return get(line.getHologram());
			}
		},
		MIRROR(9, Boolean.class) {
			@Override
			public Object get(HologramLine line) {
				if (line instanceof TextLineImpl text) {
					if (text.getWithMirror() != null && text.getWithMirror() != get(line.getHologram()))
						return text.getWithMirror();
					return get(line.getHologram());
				}
				return get(line.getHologram());
			}

			@Override
			public void apply(HologramLine line, Object data) {
				if (line instanceof TextLineImpl text) {
					text.setWithMirror((Boolean) data);
					line.getHologram().update();
				}
			}

			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setMirror((Boolean) data);
			}

			@Override
			public Object get(Hologram hologram) {
				return hologram.getMirror();
			}
		},
		ITEM_TRANSFORM(4, ItemDisplayTransform.class) {
			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setItemTransform((ItemDisplayTransform) data);
			}

			@Override
			public void apply(HologramLine line, Object data) {
				if (line instanceof ItemLineImpl item) {
					item.setItemTransform((ItemDisplayTransform) data);
					line.getHologram().update();
				}
			}

			@Override
			public Object get(Hologram hologram) {
				return hologram.getItemTransform();
			}

			@Override
			public Object get(HologramLine line) {
				if (line instanceof ItemLineImpl item) {
					if (item.getItemTransform() != null && item.getItemTransform() != get(line.getHologram()))
						return item.getItemTransform();
					return get(line.getHologram());
				}
				return get(line.getHologram());
			}
		},
		INTERACTABLE(5, Boolean.class) {
			@Override
			public Object get(HologramLine line) {
				return line.getInteractable();
			}

			@Override
			public void apply(HologramLine line, Object data) {
				line.setInteractable((Boolean) data);
			}

			@Override
			public void apply(Hologram hologram, Object data) {
				hologram.setInteractable((Boolean) data);
			}

			@Override
			public Object get(Hologram hologram) {
				return hologram.getInteractable();
			}
		};

		final int spacing;
		@Getter
		final Class<?> type;

		GlobalSetting(int spacing, Class<?> type) {
			this.spacing = spacing;
			this.type = type;
		}

		public abstract void apply(Hologram hologram, Object data);

		public abstract Object get(Hologram hologram);

		@Override
		public String getName() {
			return StringUtils.camelCase(name());
		}

		public CompletableFuture<Object> process(Player player, Hologram hologram, String data) {
			CompletableFuture<Object> future = new CompletableFuture<>();
			if (data == null || data.isEmpty() || data.isBlank()) {
				SignInputGUI.of("", "▲▲▲▲▲▲▲", "Input Value", getName())
					.onFinish((p, lines) -> {
						Holograms.log(lines[0]);
						if (Strings.isNullOrEmpty(lines[0])) {
							future.complete(null);
							return;
						}
						future.complete(convertForType(getType(), lines[0]));
						Tasks.wait(1, () -> Page.GLOBAL.open(player, hologram, 0));
					})
					.open(player);
				return future;
			}
			if (getType().isEnum() && data.equalsIgnoreCase("next")) {
				future.complete(nextWithLoop(getType(), ((Enum) get(hologram)).ordinal()));
				Tasks.wait(1, () -> Page.GLOBAL.open(player, hologram, 0));
				return future;
			}
			if (getType() == Boolean.class && data.equalsIgnoreCase("next")) {
				Boolean bool = ((Boolean) get(hologram));
				if (bool == null)
					bool = false;
				future.complete(!bool);
				Tasks.wait(1, () -> Page.GLOBAL.open(player, hologram, 0));
				return future;
			}
			future.complete(convertForType(getType(), data));
			return future;
		}
	}

	public interface LineSetting {
		Object get(HologramLine line);

		void apply(HologramLine line, Object data);

		String getName();

		Class<?> getType();

		default CompletableFuture<Object> process(Player player, Hologram hologram, String data, int index) {
			CompletableFuture<Object> future = new CompletableFuture<>();
			if (data == null || data.isEmpty() || data.isBlank()) {
				SignInputGUI.of("", "▲▲▲▲▲▲▲", "Input Value", getName())
					.onFinish((p, lines) -> {
						Holograms.log("Finish Book GUI");
						if (Strings.isNullOrEmpty(lines[0])) {
							future.complete(null);
							return;
						}

						try {
							future.complete(convertForType(getType(), lines[0]));
						} catch (Exception ex) {
							player.sendMessage(
								new JsonBuilder(gg.projecteden.commands.util.StringUtils.getPrefix("Holograms"))
									.next("&c" + ex.getMessage())
							);
							future.complete(null);
						}
						Tasks.wait(1, () -> Page.LINE.open(player, hologram, index));
					})
					.open(player);
				return future;
			}
			if (getType().isEnum() && data.equalsIgnoreCase("next")) {
				future.complete(nextWithLoop(getType(), ((Enum) get(hologram.getLines().get(index))).ordinal()));
				Tasks.wait(1, () -> Page.LINE.open(player, hologram, index));
				return future;
			}
			if (getType() == Boolean.class && data.equalsIgnoreCase("next")) {
				Boolean bool = ((Boolean) get(hologram.getLines().get(index)));
				if (bool == null)
					bool = false;
				future.complete(!bool);
				Tasks.wait(1, () -> Page.LINE.open(player, hologram, index));
				return future;
			}
			future.complete(convertForType(getType(), data));
			return future;
		}

		default Object convertForType(Class<?> type, String data) {
			if (data.equalsIgnoreCase("null"))
				return null;
			if (type == Integer.class) {
				try {
					return Integer.parseInt(data);
				} catch (Exception ignore) {
					throw new InvalidInputException("Invalid type for Integer: " + data);
				}
			}
			if (type == Float.class) {
				try {
					return Float.parseFloat(data);
				} catch (Exception ignore) {
					throw new InvalidInputException("Invalid type for Float: " + data);
				}
			}
			if (type == Byte.class) {
				try {
					int value = Integer.parseInt(data);
					if (value < -128 || value > 127)
						throw new InvalidInputException("Invalid value for Byte: " + data + ". Expected between -128 and 127");
					return (byte) value;
				} catch (Exception ex) {
					if (ex instanceof InvalidInputException)
						throw new InvalidInputException(ex.getMessage());
					throw new InvalidInputException("Invalid type for Percentage: " + data);
				}
			}
			if (type == Percentage.class) {
				try {
					int value = Integer.parseInt(data);
					if (value < 0 || value > 100)
						throw new InvalidInputException("Invalid value for Percentage: " + data + ". Expected between 0 and 100");
					return (byte) value;
				} catch (Exception ex) {
					if (ex instanceof InvalidInputException)
						throw new InvalidInputException(ex.getMessage());
					throw new InvalidInputException("Invalid type for Percentage: " + data);
				}
			}
			if (type == Boolean.class) {
				try {
					if (Arrays.asList("enable", "on", "yes", "1").contains(data.toLowerCase()))
						return true;
					if (Arrays.asList("disable", "off", "no", "0").contains(data.toLowerCase()))
						return false;
					return Boolean.parseBoolean(data);
				} catch (Exception ignore) {
					throw new InvalidInputException("Invalid type for Boolean: " + data);
				}
			}
			if (type.isEnum())
				return getEnum(type, data);

			if (type == Color.class) {
				Integer r, g, b, a = null;
				String[] args = data.replace(" ", "").split(",");
				try {
					r = Integer.parseInt(args[0]);
					g = Integer.parseInt(args[1]);
					b = Integer.parseInt(args[2]);
				} catch (Exception ignore) {
					throw new InvalidInputException("You must specify RGB seperated by commas");
				}
				try {
					a = Integer.parseInt(args[3]);
				} catch (Exception ignore) { }
				return a == null ? Color.fromRGB(r, g, b) : Color.fromARGB(a, r, g, b);
			}

			return null;
		}

		default <T> T nextWithLoop(Class<? extends T> clazz, int ordinal) {
			T[] values = clazz.getEnumConstants();
			int next = ordinal + 1 % values.length;
			return next >= values.length ? values[0] : values[next];
		}

		default <T> T getEnum(Class<? extends T> clazz, String value) {
			T[] values = clazz.getEnumConstants();
			for (T enumValue : values)
				if (((Enum<?>) enumValue).name().equalsIgnoreCase(value))
					return enumValue;
			throw new IllegalArgumentException();
		}
	}

}
