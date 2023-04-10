package tech.blastmc.holograms.commands.edit;

import gg.projecteden.commands.util.JsonBuilder;
import gg.projecteden.commands.util.StringUtils;
import lombok.NonNull;
import net.kyori.adventure.inventory.Book.Builder;
import tech.blastmc.holograms.api.models.Hologram;

import java.util.Arrays;

public class LocationPage extends EditPage{

	public LocationPage(@NonNull Hologram hologram) {
		super(hologram);
	}

	@Override
	protected Builder render(Builder book) {
		JsonBuilder json = new JsonBuilder();
		addBackButton(json, Page.GLOBAL, 0);

		json.newline(true)
			.next("       &6&lLocation")
			.hover("&e" + StringUtils.getShortLocationString(hologram.getLocation()))
			.newline(true)
			.newline(true);

		for (String coord : Arrays.asList("x", "y", "z")) {
			json.next("     ")
				.next("&3<<")
				.hover("&eMove by -1 full block")
				.command("hologram shift " + hologram.getId() + " --" + coord + "=-1 --gui")
				.group()
				.next("  ")
				.group()
				.next("&3<")
				.hover("&eMove by -.1 blocks")
				.command("hologram shift " + hologram.getId() + " --" + coord + "=-.1 --gui")
				.group()
				.next("  ")
				.group()
				.next("&3" + coord)
				.group()
				.next("  ")
				.group()
				.next("&3>")
				.hover("&eMove by .1 blocks")
				.command("hologram shift " + hologram.getId() + " --" + coord + "=.1 --gui")
				.group()
				.next("  ")
				.next("&3>>")
				.hover("&eMove by 1 full block")
				.command("hologram shift " + hologram.getId() + " --" + coord + "=1 --gui")
				.newline(true)
				.newline(true);
		}

		json.next("    ")
			.next("&3[ Move to You ]")
			.hover("&eMove the hologram to your exact location")
			.command("hologram movehere " + hologram.getId())
			.group();

		book.addPage(json.build());
		return book;
	}
}
