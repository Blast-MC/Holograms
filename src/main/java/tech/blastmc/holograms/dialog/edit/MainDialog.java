package tech.blastmc.holograms.dialog.edit;

import gg.projecteden.commands.util.JsonBuilder;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import lombok.NonNull;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.api.models.line.HologramLine;
import tech.blastmc.holograms.api.models.line.Offset;
import tech.blastmc.holograms.api.models.line.TextLine;
import tech.blastmc.holograms.models.HologramImpl;
import tech.blastmc.holograms.utils.DialogUtils.MultiActionDialogBuilder;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class MainDialog extends EditDialog {

	public MainDialog(@NonNull Hologram hologram) {
		super(hologram);
	}

	@Override
	protected JsonBuilder getTitle() {
		return new JsonBuilder("Edit Hologram: " + hologram.getId());
	}

	@Override
	protected void render(MultiActionDialogBuilder dialog) {
		dialog.exitButton("Save");

		AtomicInteger i = new AtomicInteger(0);
		for (HologramLine line : hologram.getLines()) {
			dialog.button("&c\uD83D\uDDD1", "&cRemove Line", 20, response -> {
				hologram.removeLine(i.get());
				hologram.save();
				Page.MAIN.open(response.getPlayer(), this.hologram, 0);
			});

			dialog.button("✐", "Edit", 20, response -> {
				// TODO
			});

			String type = "";
			String display = "";
			if (line instanceof Offset offset) {
				type = "Offset";
				display = "&e" + offset.getValue();
			}
			if (line instanceof TextLine textLine) {
				type = "Text";
				display = textLine.getText();
			}

			dialog.button(type, display, 100, response -> {
				// TODO
			});

			if (i.get() == 0)
				dialog.button("&7▲", 20, response -> {});
			else {
				int j = i.get();
				dialog.button("▲", "Move up", 20, response -> {
					List<HologramLine> lines = hologram.getLines();
					HologramLine tempLine = lines.get(j - 1);
					lines.set(j - 1, lines.get(j));
					lines.set(j, tempLine);
					HologramImpl impl = (HologramImpl) hologram;
					impl.setLinesRaw(lines);
					hologram.update();
					hologram.save();
					Page.MAIN.open(response.getPlayer(), hologram, -1);
				});
			}

			if (i.get() == hologram.getLines().size() - 1)
				dialog.button("&7▼", 20, response -> {});
			else {
				int j = i.get();
				dialog.button("▼", "Move down", 20, response -> {
					List<HologramLine> lines = hologram.getLines();
					HologramLine tempLine = lines.get(j + 1);
					lines.set(j + 1, lines.get(j));
					lines.set(j, tempLine);
					HologramImpl impl = (HologramImpl) hologram;
					impl.setLinesRaw(lines);
					hologram.update();
					hologram.save();
					Page.MAIN.open(response.getPlayer(), hologram, -1);
				});
			}

			i.getAndIncrement();
		}

		dialog.button("", 20);
		dialog.button("", 20);
		dialog.button("&eGlobal Settings", "Modify settings for the entire hologram", 100, response -> {
			// TODO
		});
		dialog.button("", 20);
		dialog.button("", 20);

		dialog.button("", 20);
		dialog.button("", 20);
		dialog.button("&aAdd Line", "Add a line to the hologram", 100, response -> {
			Page.ADD.open(response.getPlayer(), hologram, -1);
		});
		dialog.button("", 20);
		dialog.button("", 20);

		dialog.columns(5);
	}

}
