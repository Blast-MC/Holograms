package tech.blastmc.holograms.commands.edit;

import gg.projecteden.commands.util.JsonBuilder;
import lombok.NonNull;
import net.kyori.adventure.inventory.Book.Builder;
import tech.blastmc.holograms.api.models.Hologram;

public class AddPage extends EditPage {

	public AddPage(@NonNull Hologram hologram) {
		super(hologram);
	}

	@Override
	protected Builder render(Builder book) {
		JsonBuilder json = new JsonBuilder();
		addBackButton(json, Page.MAIN, 0);
		json.newline(true)
			.next("       &6&lAdd Line")
			.newline(true)
			.newline(true)
			.next("        &3[ Text ]")
			.hover("&eAdd a text line")
			.command("hologram edit " + hologram.getId() + " add text")
			.newline(true)
			.newline(true)
			.next("        &3[ Block ]")
			.hover("&eAdd a block line")
			.command("hologram edit " + hologram.getId() + " add block")
			.newline(true)
			.newline(true)
			.next("        &3[ Item ]")
			.hover("&eAdd an item line")
			.command("hologram edit " + hologram.getId() + " add item")
			.newline(true)
			.newline(true)
			.next("       &3[ Offset ]")
			.hover("&eAdd an offset line")
			.command("hologram edit " + hologram.getId() + " add offset");
		book.addPage(json.build());
		return book;
	}
}
