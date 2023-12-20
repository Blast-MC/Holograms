package tech.blastmc.holograms.commands.edit;

import gg.projecteden.commands.util.JsonBuilder;
import lombok.NonNull;
import net.kyori.adventure.inventory.Book.Builder;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.line.HologramLine;
import tech.blastmc.holograms.api.models.line.Offset;
import tech.blastmc.holograms.models.line.HologramLineImpl;

public class MainPage extends EditPage {

	public MainPage(@NonNull Hologram hologram) {
		super(hologram);
	}

	@Override
	protected Builder render(Builder book) {
		JsonBuilder json = new JsonBuilder()
					.next("    &6&lEdit Hologram\n")
					.hover("&e" + hologram.getId())
					.group()
					.next("\n")
					.next(" &3&l[ Global Settings ]\n")
					.hover("&eClick to edit settings for all lines")
					.command("hologram edit " + hologram.getId() + " global")
					.group()
					.next("\n")
					.group()
					.next("     &3&l[ Add Line ]\n")
					.hover("&eClick to add a line")
					.command("hologram edit " + hologram.getId() + " add")
					.group()
					.next("\n")
					.group();

		int index = 0;
		for (HologramLine line : hologram.getLines()) {
			json.next("•")
				.next(" ")
				.next( "&c✗")
				.command("hologram edit " + hologram.getId() + " remove " + index)
				.hover("&cRemove Line")
				.group()
				.next(" ")
				.next("&0✐")
				.command("hologram edit " + hologram.getId() + " line " + index)
				.hover("&eClick to Edit")
				.group()
				.next(" ");

			if (index == 0)
				json.next("&7▲");
			else
				json.next("&3▲")
				.command("hologram edit " + hologram.getId() + " moveup " + index)
				.hover("&eMove up")
				.group();

			json.next(" ");

			if (index == hologram.getLines().size() - 1)
				json.next("&7▼");
			else
				json.next("&3▼")
				.hover("&eMove Down")
				.command("hologram edit " + hologram.getId() + " movedown " + index)
				.group();

			json.next(" &0");

			if (line instanceof Offset offset)
				json.next("Offset").hover("&e" + offset.getValue());
			else
				json.next(((HologramLineImpl) line).renderHover("&0", -1));

			json.command("hologram edit " + hologram.getId() + " line " + index);
			json.group().next("\n").group();

			if (index == 10) {
				book.addPage(json.build());
				json = new JsonBuilder();
			}

			index++;
		}

		return book.addPage(json.build());
	}
}
