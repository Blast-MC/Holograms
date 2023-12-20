package tech.blastmc.holograms.commands.edit;

import com.google.common.base.Strings;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import gg.projecteden.commands.util.JsonBuilder;
import gg.projecteden.commands.util.SerializationUtils;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.inventory.Book.Builder;
import org.bukkit.Color;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.line.HologramLine;
import tech.blastmc.holograms.api.models.line.Offset;
import tech.blastmc.holograms.commands.edit.GlobalPage.GlobalSetting;
import tech.blastmc.holograms.commands.edit.GlobalPage.LineSetting;
import tech.blastmc.holograms.models.line.BlockLineImpl;
import tech.blastmc.holograms.models.line.HologramLineImpl;
import tech.blastmc.holograms.models.line.ItemLineImpl;
import tech.blastmc.holograms.models.line.TextLineImpl;
import tech.blastmc.holograms.utils.StringUtils;

import java.util.Arrays;

/**
 * TODO
 *  - blockdata?
 */
public class LinePage extends EditPage {

	int index;

	public LinePage(@NonNull Hologram hologram, int index) {
		super(hologram);
		this.index = index;
	}

	@Override
	protected Builder render(Builder book) {
		JsonBuilder json = new JsonBuilder();
		addBackButton(json, Page.MAIN, 0);

		json.next("       ");

		if (index < 10)
			json.next(" ");

		json.group()
			.next("&6&lLine " + (index + 1))
			.group()
			.group().next("\n")
			.group().next("\n");

		HologramLine line = hologram.getLines().get(index);

		if (line instanceof Offset offset) {
			json.next("          Offset")
				.hover("&3Current Value: &e" + offset.getValue())
				.command("hologram edit " + hologram.getId() + " set " + index);
			book.addPage(json.build());
			return book;
		}

		if (line instanceof BlockLineImpl)
			json.next("         ");
		if (line instanceof ItemLineImpl || line instanceof TextLineImpl)
			json.next("          ");

		json.next(((HologramLineImpl) line).renderHover("&3", index))
			.command("hologram edit " + hologram.getId() + " set " + index) // Suggest doesn't work, this will prompt // TODO
			.group();

		json.group().next("\n")
			.group().next("\n")
			.next("       ")
			.next("&6&lSettings")
			.group().next("\n");

		if (line instanceof TextLineImpl) {
			for (TextSetting setting : TextSetting.values()) {
				json.group().next("\n")
					.next("&f" + Strings.repeat(" ", setting.spacing))
					.next("&3" + StringUtils.camelCase(setting.name()));
				if (setting.type.isEnum())
					json.hover("&3Current Value: &e" + StringUtils.camelCase(((Enum) setting.get(line)).name()));
				else
					json.hover("&3Current Value: &e" + setting.get(line));
				String command = "hologram edit " + hologram.getId() + " line " + index + " " + setting.name().toLowerCase();
				if (setting.type.isEnum())
					command += " next";
				json.command(command)
					.group();
			}
			GlobalSetting alignment = GlobalSetting.TEXT_ALIGNMENT;
			json.group().next("\n").group()
				.next("&f" + Strings.repeat(" ", alignment.spacing))
				.next("&3" + StringUtils.camelCase(alignment.name()))
				.hover("&3Current Value: &e" + StringUtils.camelCase(((Enum) alignment.get(line)).name()))
				.command("hologram edit " + hologram.getId() + " line " + index + " " + alignment.name().toLowerCase() + " next")
				.group();

		}
		if (line instanceof ItemLineImpl) {
			GlobalSetting setting = GlobalSetting.ITEM_TRANSFORM;
			json.group().next("\n").group()
				.next("&f" + Strings.repeat(" ", setting.spacing))
				.next("&3" + StringUtils.camelCase(setting.name()))
				.hover("&3Current Value: &e" + StringUtils.camelCase(((Enum) setting.get(line)).name()))
				.command("hologram edit " + hologram.getId() + " line " + index + " " + setting.name().toLowerCase() + " next")
				.group();
		}

		book.addPage(json.build());
		json = new JsonBuilder();

		json.group().next("\n")
			.next("      &6&lOverrides")
			.group().next("\n");

		for (GlobalSetting setting : GlobalSetting.values()) {
			if (Arrays.asList(GlobalSetting.TEXT_ALIGNMENT, GlobalSetting.ITEM_TRANSFORM, GlobalSetting.MIRROR, GlobalSetting.BLOCK_LIGHT, GlobalSetting.SKY_LIGHT).contains(setting)) continue;
			json.group().next("\n")
				.next("&f" + Strings.repeat(" ", setting.spacing))
				.next("&3" + StringUtils.camelCase(setting.name()));
			if (setting.type.isEnum())
				json.hover("&3Current Value: &e" + StringUtils.camelCase(((Enum) setting.get(hologram)).name()));
			else
				json.hover("&3Current Value: &e" + setting.get(hologram));
			String command = "hologram edit " + hologram.getId() + " line " + index + " " + setting.name().toLowerCase();
			if (setting.type.isEnum())
				command += " next";
			json.command(command)
				.group();
		}

		book.addPage(json.build());
		return book;
	}

	public enum TextSetting implements LineSetting {
		LINE_WIDTH(7, Integer.class) {
			@Override
			public Object get(HologramLine line) {
				if (line instanceof TextLineImpl text)
					return text.getLineWidth();
				return null;
			}

			@Override
			public void apply(HologramLine line, Object data) {
				if (line instanceof TextLineImpl text)
					text.setLineWidth((Integer) data);
			}
		},
		BACKGROUND(6, Color.class) {
			@Override
			public Object get(HologramLine line) {
				if (line instanceof TextLineImpl text)
					return text.getBackground();
				return null;
			}

			@Override
			public void apply(HologramLine line, Object data) {
				if (line instanceof TextLineImpl text)
					text.setBackground((Color) data);
			}
		},
		OPACITY(9, Byte.class) {
			@Override
			public Object get(HologramLine line) {
				if (line instanceof TextLineImpl text)
					return text.getOpacity();
				return null;
			}

			@Override
			public void apply(HologramLine line, Object data) {
				if (line instanceof TextLineImpl text)
					text.setOpacity((Byte) data);
			}
		},
		SHADOWED(7, Boolean.class) {
			@Override
			public Object get(HologramLine line) {
				if (line instanceof TextLineImpl text)
					return text.getShadowed();
				return null;
			}

			@Override
			public void apply(HologramLine line, Object data) {
				if (line instanceof TextLineImpl text)
					text.setShadowed((Boolean) data);
			}
		},
		SEE_THROUGH(5, Boolean.class) {
			@Override
			public Object get(HologramLine line) {
				if (line instanceof TextLineImpl text)
					return text.getSeeThrough();
				return null;
			}

			@Override
			public void apply(HologramLine line, Object data) {
				if (line instanceof TextLineImpl text)
					text.setSeeThrough((Boolean) data);
			}
		};

		@Getter
		int spacing;
		@Getter
		Class<?> type;

		TextSetting(int spacing, Class<?> type) {
			this.spacing = spacing;
			this.type = type;
		}

		public String getName() {
			return StringUtils.camelCase(name());
		}
	}

}
