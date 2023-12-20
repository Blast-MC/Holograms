package tech.blastmc.holograms.commands.edit;

import gg.projecteden.commands.util.JsonBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.inventory.Book;
import org.bukkit.entity.Player;
import tech.blastmc.holograms.api.models.Hologram;

@RequiredArgsConstructor
public abstract class EditPage {

	@NonNull
	public Hologram hologram;
	private Book book;

	protected void addBackButton(JsonBuilder json, Page page, Integer index) {
		json.next("&c<-- Back")
			.hover("&eGo to the previous page")
			.command("hologram edit " + hologram.getId() + " gui " + page.name().toLowerCase() + " " + index)
			.group().next("\n");
	}

	protected abstract Book.Builder render(Book.Builder book);

	protected void open(Player player) {
		book = render(Book.builder()).build();
		player.openBook(book);
	}

	public enum Page {
		MAIN {
			@Override
			public void open(Player player, Hologram hologram, Integer index) {
				new MainPage(hologram).open(player);
			}
		},
		GLOBAL {
			@Override
			public void open(Player player, Hologram hologram, Integer index) {
				new GlobalPage(hologram).open(player);
			}
		},
		ADD {
			@Override
			public void open(Player player, Hologram hologram, Integer index) {
				new AddPage(hologram).open(player);
			}
		},
		LOCATION {
			@Override
			public void open(Player player, Hologram hologram, Integer index) {
				new LocationPage(hologram).open(player);
			}
		},
		LINE {
			@Override
			public void open(Player player, Hologram hologram, Integer index) {
				new LinePage(hologram, index).open(player);
			}
		};

		public abstract void open(Player player, Hologram hologram, Integer line);

	}

}
