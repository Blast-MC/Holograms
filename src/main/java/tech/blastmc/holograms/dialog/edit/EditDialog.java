package tech.blastmc.holograms.dialog.edit;

import gg.projecteden.commands.util.JsonBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import tech.blastmc.holograms.api.models.Hologram;
import tech.blastmc.holograms.utils.DialogUtils.DialogBuilder;
import tech.blastmc.holograms.utils.DialogUtils.MultiActionDialogBuilder;

@RequiredArgsConstructor
public abstract class EditDialog {

	@NonNull
	public Hologram hologram;

	protected abstract JsonBuilder getTitle();

	protected abstract void render(MultiActionDialogBuilder dialog);

	public void open(Player player) {
		var dialog = new DialogBuilder().title(getTitle()).multiAction();
		render(dialog);
		dialog.open(player);
	}

	public enum Page {
		MAIN {
			@Override
			public void open(Player player, Hologram hologram, Integer line) {
				new MainDialog(hologram).open(player);
			}
		},
		ADD {
			@Override
			public void open(Player player, Hologram hologram, Integer line) {
				new AddDialog(hologram).open(player);
			}
		};

		public abstract void open(Player player, Hologram hologram, Integer line);
	}

}
