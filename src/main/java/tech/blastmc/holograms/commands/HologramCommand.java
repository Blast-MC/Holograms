package tech.blastmc.holograms.commands;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import gg.projecteden.commands.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.commands.models.CustomCommand;
import gg.projecteden.commands.models.annotations.Arg;
import gg.projecteden.commands.models.annotations.ConverterFor;
import gg.projecteden.commands.models.annotations.Path;
import gg.projecteden.commands.models.annotations.Switch;
import gg.projecteden.commands.models.annotations.TabCompleterFor;
import gg.projecteden.commands.models.events.CommandEvent;
import gg.projecteden.commands.util.JsonBuilder;
import gg.projecteden.commands.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay.TextAligment;
import org.bukkit.inventory.ItemStack;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.PowerUp;
import tech.blastmc.holograms.api.models.line.HologramLine;
import tech.blastmc.holograms.api.models.line.ItemLine;
import tech.blastmc.holograms.api.models.line.Offset;
import tech.blastmc.holograms.commands.edit.EditPage.Page;
import tech.blastmc.holograms.commands.edit.GlobalPage.GlobalSetting;
import tech.blastmc.holograms.commands.edit.GlobalPage.LineSetting;
import tech.blastmc.holograms.commands.edit.LinePage.TextSetting;
import tech.blastmc.holograms.models.HologramBuilderImpl;
import tech.blastmc.holograms.models.HologramImpl;
import tech.blastmc.holograms.models.line.BlockLineImpl;
import tech.blastmc.holograms.models.line.ItemLineImpl;
import tech.blastmc.holograms.models.line.TextLineImpl;
import tech.blastmc.holograms.utils.SignInputGUIListener.SignInputGUI;
import tech.blastmc.holograms.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class HologramCommand extends CustomCommand {

	public HologramCommand(CommandEvent event) {
		super(event);
	}

	@Path("create <name>")
	void create(String name) {
		new HologramBuilderImpl()
			.id(name)
			.persistent(true)
			.location(location())
			.lines("")
			.spawn();
		send(PREFIX + "Successfully created a new hologram at your location"); // TODO - prompt edit
	}

	@Path("(delete|remove|del) <hologram>")
	void delete(Hologram hologram) {
		hologram.remove();
		send(PREFIX + "Removed hologram &e" + hologram.getId());
	}

	@Path("list [world] [page] [--range]")
	void list(@Arg("current") World world, @Arg("1") int page, double range) {
		send(PREFIX + "Holograms for world &e" + world.getName());

		BiFunction<Hologram, String, JsonBuilder> formatter = (holo, index) ->
			json(" &e- " + holo.getId() + ": &3(" + StringUtils.getShortLocationString(holo.getLocation()) + ")")
			.hover("&eClick to Teleport")
			.command("hologram teleportTo " + holo.getId());

		List<Hologram> holograms = HologramsAPI.getHolograms(world);
		if (range > 0)
			holograms = holograms.stream().filter(hologram -> hologram.getLocation().distanceSquared(location()) < (range * range)).toList();

		paginate(holograms, formatter, "hologram list " + world.getName(), page);
	}

	@Path("teleportTo <hologram>")
	void teleportTo(Hologram hologram) {
		player().teleport(hologram.getLocation());
	}

	@Path("edit <hologram> [action] [context] [extra] [extra] [extra] [--nogui]")
	void editAction(Hologram hologram, @Arg("gui") EditActions action,
	                @Arg(context = 2, tabCompleter = HologramData.class) String context,
					@Arg(context = 3, tabCompleter = HologramExtra.class) String extra1,
					@Arg(context = 4, tabCompleter = HologramExtra2.class) String extra2,
	                @Switch boolean nogui) {
		String data = context;
		if (extra1 != null)
			data += " " + extra1;
		if (extra2 != null)
			data += " " + extra2;
		action.execute(hologram, player(), data, nogui);
	}

	@Path("moveHere <hologram>")
	void moveHere(Hologram hologram) {
		hologram.setLocation(location());
		hologram.save();
	}

	@Path("shift <hologram> [--x] [--y] [--z] [--gui]")
	void shift(Hologram hologram, @Switch double x, @Switch double y, @Switch double z, @Switch boolean gui) {
		if (x == 0 && y == 0 && z == 0) {
			Page.LOCATION.open(player(), hologram, 0);
			return;
		}
		Location loc = hologram.getLocation().clone();
		loc.add(x, y, z);
		hologram.setLocation(loc);
		hologram.save();
		if (gui)
			Page.LOCATION.open(player(), hologram, 0);
	}

	@Path("test")
	void test() {
		Hologram hologram = HologramsAPI.byId(world(), "test2");
		ItemLine itemLine = (ItemLine) hologram.getLines().get(1);
		itemLine.setClickListener(player -> ((Player) player).sendMessage("hi"));
	}

	@TabCompleterFor(Hologram.class)
	List<String> hologramTabCompleter(String filter) {
		return HologramsAPI.getHolograms(world()).stream().map(Hologram::getId).filter(id -> id.toLowerCase().startsWith(filter.toLowerCase())).toList();
	}

	@ConverterFor(Hologram.class)
	Hologram convertToHologram(String value) {
		Hologram hologram;
		if (value.equalsIgnoreCase("nearest"))
			hologram = HologramsAPI.getHolograms(world()).stream().sorted(Comparator.comparing(holo -> holo.getLocation().distanceSquared(location()))).findFirst().orElse(null);
		else
			hologram = HologramsAPI.getHolograms(world()).stream().filter(holo -> holo.getId().equalsIgnoreCase(value)).findFirst().orElse(null);
		if (hologram == null)
			throw new InvalidInputException("Cannot find hologram for id: " + value);
		return hologram;
	}

	private enum EditActions {
		GUI {
			@Override
			public void execute(Hologram hologram, Player player, String data, boolean nogui) {
				if (data != null) {
					String[] args = data.split(" ");
					try {
						Page page = Page.valueOf(args[0].toUpperCase().trim());
						Integer line = args.length > 1 ? Integer.parseInt(args[1]) : null;
						page.open(player, hologram, line);
						return;
					} catch (Exception ex) {
						throw new InvalidInputException("Could not parse data for: " + data);
					}
				}
				Page.MAIN.open(player, hologram, 0);
			}
		},
		GLOBAL {
			@Override
			public void execute(Hologram hologram, Player player, String data, boolean nogui) {
				if (data == null)
					Page.GLOBAL.open(player, hologram, 0);
				else {
					String[] args = data.split(" ");
					data = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "";
					GlobalSetting setting;
					try {
						setting = GlobalSetting.valueOf(args[0].toUpperCase());
					} catch (Exception ignore) { throw new InvalidInputException("Invalid setting"); }
					setting.process(player, hologram, data).thenAccept(obj -> {
						setting.apply(hologram, obj);
						hologram.save();
					});
				}
			}
		},
		ADD {
			@Override
			public void execute(Hologram hologram, Player player, String data, boolean nogui) {
				if (data == null)
					Page.ADD.open(player, hologram, 0);
				else {
					String[] args = data.split(" ");
					data = args.length > 1 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "";
					getObject(args[0], data, player).thenAccept(obj -> {
						hologram.addLine(obj);
						hologram.save();
						if (!nogui)
							Page.MAIN.open(player, hologram, 0);
					});
				}
			}
		},
		SET {
			@Override
			public void execute(Hologram hologram, Player player, String data, boolean nogui) {
				String[] args = data.split(" ");
				Integer line = null;
				try {
					line = Integer.parseInt(args[0]);
				} catch (Exception ignore) { }
				if (line == null)
					throw new InvalidInputException("You must specify a line index");
				data = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "";
				if (data.isEmpty() || data.isBlank()) {
					String type = "";
					if (hologram.getLines().get(0) instanceof TextLineImpl)
						type = "text ";
					if (hologram.getLines().get(0) instanceof ItemLineImpl)
						type = "item ";
					if (hologram.getLines().get(0) instanceof BlockLineImpl)
						type = "block ";
					if (hologram.getLines().get(0) instanceof Offset)
						type = "offset ";
					player.sendMessage(new JsonBuilder(StringUtils.getPrefix("Hologram") + "&e&lClick Here &3to edit the line")
						                   .hover("&eEdit Line " + line)
						                   .suggest("/hologram edit " + hologram.getId() + " set " + line + " " + type));
					return;
				}
				Integer finalLine = line;
				getObject(args[1], data, player).thenAccept(obj -> {
					hologram.setLine(finalLine, obj);
					hologram.save();
					if (!nogui)
						Page.LINE.open(player, hologram, finalLine);
				});
			}
		},
		INSERT {
			@Override
			public void execute(Hologram hologram, Player player, String data, boolean nogui) {
				String[] args = data.split(" ");
				Integer line = null;
				try {
					line = Integer.parseInt(args[0]);
				} catch (Exception ignore) { }
				if (line == null)
					throw new InvalidInputException("You must specify a line index");
				data = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "";
				Integer finalLine = line;
				getObject(args[1], data, player).thenAccept(obj -> {
					hologram.addLine(finalLine, obj);
					hologram.save();
					if (!nogui)
						Page.LINE.open(player, hologram, finalLine);
				});
			}
		},
		REMOVE {
			@Override
			public void execute(Hologram hologram, Player player, String data, boolean nogui) {
				Integer line = null;
				try {
					line = Integer.parseInt(data);
				} catch (Exception ignore) { }
				if (line == null)
					throw new InvalidInputException("You must specify a line index");
				hologram.removeLine(line);
				hologram.save();
				if (!nogui)
					Page.MAIN.open(player, hologram, 0);
			}
		},
		LINE {
			@Override
			public void execute(Hologram hologram, Player player, String data, boolean nogui) {
				String[] args = data.split(" ");
				data = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "";
				Integer line = null;
				try {
					line = Integer.parseInt(args[0]);
				} catch (Exception ignore) { }
				if (line == null)
					throw new InvalidInputException("You must specify a line index");
				if (data.isEmpty() || data.isBlank())
					Page.LINE.open(player, hologram, line);
				else {
					LineSetting setting;
					try {
						setting = GlobalSetting.valueOf(args[1].toUpperCase());
					} catch (Exception ignore) {
						try {
							setting = TextSetting.valueOf(args[1].toUpperCase());
						} catch (Exception ignore2) { throw new InvalidInputException("Invalid setting"); }
					}
					Integer finalLine = line;
					LineSetting finalSetting = setting;
					setting.process(player, hologram, data, line).thenAccept(obj -> {
						finalSetting.apply(hologram.getLines().get(finalLine), obj);
						hologram.save();
					});
				}
			}
		},
		MOVEUP {
			@Override
			public void execute(Hologram hologram, Player player, String data, boolean nogui) {
				Integer line = null;
				try {
					line = Integer.parseInt(data);
				} catch (Exception ignore) { }
				if (line == null)
					throw new InvalidInputException("You must specify a line index");
				List<HologramLine> lines = hologram.getLines();
				HologramLine tempLine = lines.get(line - 1);
				lines.set(line - 1, lines.get(line));
				lines.set(line, tempLine);
				HologramImpl impl = (HologramImpl) hologram;
				impl.setLinesRaw(lines);
				hologram.update();
				hologram.save();
				if (!nogui)
					Page.MAIN.open(player, hologram, 0);
			}
		},
		MOVEDOWN {
			@Override
			public void execute(Hologram hologram, Player player, String data, boolean nogui) {
				Integer line = null;
				try {
					line = Integer.parseInt(data);
				} catch (Exception ignore) { }
				if (line == null)
					throw new InvalidInputException("You must specify a line index");
				List<HologramLine> lines = hologram.getLines();
				HologramLine tempLine = lines.get(line + 1);
				lines.set(line + 1, lines.get(line));
				lines.set(line, tempLine);
				HologramImpl impl = (HologramImpl) hologram;
				impl.setLinesRaw(lines);
				hologram.update();
				hologram.save();
				if (!nogui)
					Page.MAIN.open(player, hologram, 0);
			}
		};
		
		public CompletableFuture<Object> getObject(String type, String data, Player player) {
			CompletableFuture<Object> completable = new CompletableFuture<>();
			switch (type.toLowerCase()) {
				case "text" -> completable.complete(data);
				case "block" -> {
					if (data.isEmpty())
						completable.complete(Material.STONE.createBlockData());
					try {
						completable.complete(Bukkit.createBlockData(data));
					} catch (Exception ex) {
						throw new InvalidInputException("Unable to parse '" + data + "' as block data");
					}
				}
				case "item" -> {
					if (data.isEmpty())
						completable.complete(new ItemStack(Material.GRASS_BLOCK));
					try {
						String mat = data.split(" ")[0].replace("minecraft:", "").toUpperCase();
						Material material = Material.valueOf(mat);
						ItemStack item = new ItemStack(material);
						NBTItem nbt = new NBTItem(item);
						if (data.contains("{"))
							nbt.mergeCompound(new NBTContainer(data.substring(data.indexOf("{"))));
						completable.complete(nbt.getItem());
					} catch (Exception ex) {
						ex.printStackTrace();
						throw new InvalidInputException("There was an error while parsing your item");
					}
				}
				case "offset" -> {
					if (data.isEmpty()) {
						SignInputGUI.of("", "▲▲▲▲▲▲▲", "Input Value", "Offset Amount")
							.onFinish((p, lines) -> {
								try {
									completable.complete(Offset.of(Float.parseFloat(lines[0])));
								} catch (Exception ignore) {
									completable.complete(Offset.of(1f));
								}
							})
							.open(player);
					}
					else {
						try {
							float offset = Float.parseFloat(data);
							completable.complete(Offset.of(offset));
						} catch (Exception ex) {
							throw new InvalidInputException("Unable to parse '" + data + "' as a float");
						}
					}
				}
				default -> completable.complete(null);
			}
			return completable;
		}

		public abstract void execute(Hologram hologram, Player player, String data, boolean nogui);

	}

	public static class HologramData {
		String data;
	}

	public static class HologramExtra {
		String data;
	}

	public static class HologramExtra2 {
		String data;
	}

	@TabCompleterFor(HologramData.class)
	List<String> tabCompleterHologramData(String filter, EditActions context) {
		List<String> list = new ArrayList<>();
		switch (context) {
			case REMOVE, SET, LINE -> IntStream.range(0, convertToHologram(arg(2)).getLines().size()).mapToObj(x -> "" + x).filter(x -> x.startsWith(filter)).forEach(list::add);
			case MOVEUP -> IntStream.range(1, convertToHologram(arg(2)).getLines().size()).mapToObj(x -> "" + x).filter(x -> x.startsWith(filter)).forEach(list::add);
			case MOVEDOWN, INSERT -> IntStream.range(0, convertToHologram(arg(2)).getLines().size() - 1).mapToObj(x -> "" + x).filter(x -> x.startsWith(filter)).forEach(list::add);
			case ADD -> Arrays.asList("text", "item", "block", "offset").stream().filter(x -> x.startsWith(filter)).forEach(list::add);
			case GLOBAL -> Arrays.stream(GlobalSetting.values()).map(setting -> setting.name().toLowerCase()).filter(x -> x.startsWith(filter)).forEach(list::add);
		}
		return list;
	}

	@TabCompleterFor(HologramExtra.class)
	List<String> hologramExtraTabCompleter(String filter, String context) {
		List<String> list = new ArrayList<>();
		switch (context.toLowerCase()) {
			case "item" -> Arrays.stream(Material.values()).filter(Material::isItem).map(_enum -> _enum.name().toLowerCase()).filter(x -> x.startsWith(filter)).forEach(list::add);
			case "offset" -> list.addAll(Arrays.asList(".1", ".5", "1"));
			case "billboard" -> list.addAll(tabCompleteEnum(filter, Billboard.class));
			case "text_alignment" -> list.addAll(tabCompleteEnum(filter, TextAligment.class));
			case "item_transform" -> list.addAll(tabCompleteEnum(filter, ItemDisplayTransform.class));
			case "mirror" -> list.addAll(Arrays.asList("true", "false"));
		}
		if (Arrays.asList("insert", "set").contains(arg(3).toLowerCase()))
			Arrays.asList("text", "item", "block", "offset").stream().filter(x -> x.startsWith(filter)).forEach(list::add);
		if (arg(3).equalsIgnoreCase("line")) {
			try {
				Integer line = Integer.parseInt(arg(4));
				HologramLine hologramLine = convertToHologram(arg(2)).getLines().get(line);
				if (hologramLine instanceof TextLineImpl) {
					list.addAll(tabCompleteEnum(filter, TextSetting.class));
					if ("text_alignment".startsWith(filter))
						list.add("text_alignment");

				}
				if (hologramLine instanceof ItemLineImpl)
					if ("item_transform".startsWith(filter))
						list.add("item_transform");
				if (hologramLine instanceof BlockLineImpl) {
					// TODO
				}
				Arrays.stream(GlobalSetting.values())
					.filter(setting -> setting != GlobalSetting.TEXT_ALIGNMENT && setting != GlobalSetting.ITEM_TRANSFORM)
					.map(_enum -> _enum.name().toLowerCase())
					.filter(x -> x.startsWith(filter)).forEach(list::add);
			} catch (Exception ignore) { }
		}
		return list;
	}

	@TabCompleterFor(HologramExtra2.class)
	List<String> hologramExtra2TabCompleter(String filter, String context) {
		List<String> list = new ArrayList<>();
		switch (context.toLowerCase()) {
			case "item" -> Arrays.stream(Material.values()).filter(Material::isItem).map(_enum -> _enum.name().toLowerCase()).filter(x -> x.startsWith(filter)).forEach(list::add);
			case "offset" -> list.addAll(Arrays.asList(".1", ".5", "1"));
			case "billboard" -> list.addAll(tabCompleteEnum(filter, Billboard.class));
			case "text_alignment" -> list.addAll(tabCompleteEnum(filter, TextAligment.class));
			case "item_transform" -> list.addAll(tabCompleteEnum(filter, ItemDisplayTransform.class));
			case "mirror" -> list.addAll(Arrays.asList("true", "false"));
		}
		return list;
	}


}
