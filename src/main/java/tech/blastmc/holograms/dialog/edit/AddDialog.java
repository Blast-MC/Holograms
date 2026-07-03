package tech.blastmc.holograms.dialog.edit;

import gg.projecteden.commands.util.JsonBuilder;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import lombok.NonNull;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.utils.DialogUtils.MultiActionDialogBuilder;

public class AddDialog extends EditDialog {

	public AddDialog(@NonNull Hologram hologram) {
		super(hologram);
	}

	@Override
	protected JsonBuilder getTitle() {
		return new JsonBuilder("Add line");
	}

	@Override
	protected void render(MultiActionDialogBuilder dialog) {
		dialog.columns(1);

		dialog.exitButton(ActionButton.builder(
			new JsonBuilder("Back").build()
		).build());

		dialog.exitButton("Back", response -> {
			Page.MAIN.open(response.getPlayer(), hologram, -1);
		});

		dialog.button("Text", response -> {});
		dialog.button("Item", response -> {});
		dialog.button("Block", response -> {});
		dialog.button("Offset", response -> {});
	}
}
