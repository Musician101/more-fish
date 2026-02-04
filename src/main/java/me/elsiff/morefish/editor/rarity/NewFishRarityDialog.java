package me.elsiff.morefish.editor.rarity;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.registry.FishTypeTable;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.io.IOException;
import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class NewFishRarityDialog extends MusiDialog {

    private static final String DISPLAY_NAME = "display_name";
    private static final String ID = "rarity_id";

    public NewFishRarityDialog() {
        super(lang().getComponent(path().withAppendedChild("new")));
    }

    private static NodePath path() {
        return NodePath.path("editor", "rarity", "selector");
    }

    @Override
    protected List<DialogInput> inputs() {
        DialogInput id = textInput(ID, lang().getComponent(path().plus(NodePath.path("id", "label"))));
        DialogInput displayName = textInput(DISPLAY_NAME, lang().getComponent(path().plus(NodePath.path("display-name", "label"))));
        return List.of(id, displayName);
    }

    @Override
    protected DialogType type() {
        FishRaritiesDialog fishRaritiesDialog = new FishRaritiesDialog();
        ActionButton confirm = confirmButton((view, audience) -> {
            String keyString = view.getText(ID);
            Component errorMessage = lang().getComponent(path().plus(NodePath.path("id", "error", "format")));
            ErrorDialog errorDialog = new ErrorDialog(errorMessage, this);
            if (keyString == null) {
                audience.showDialog(errorDialog.build());
                return;
            }

            NamespacedKey rarityKey = NamespacedKey.fromString(keyString);
            if (rarityKey == null) {
                audience.showDialog(errorDialog.build());
                return;
            }

            FishTypeTable ftt = getPlugin().getFishTypeTable();
            if (ftt.getRarities().stream().anyMatch(r -> r.getKey().equals(rarityKey))) {
                Component message = lang().getComponent(path().withAppendedChild("error"));
                audience.showDialog(new ErrorDialog(message, this).build());
                return;
            }

            String displayName = view.getText(DISPLAY_NAME);
            if (displayName == null || displayName.isBlank()) {
                Component displayNameError = lang().getComponent(path().plus(NodePath.path("display-name", "error")));
                audience.showDialog(new ErrorDialog(displayNameError, this).build());
                return;
            }

            try {
                getPlugin().getFishTypeTable().saveRarity(new FishRarity(rarityKey, displayName));
                audience.showDialog(fishRaritiesDialog.build());
            }
            catch (IOException e) {
                Component message = lang().getComponent(path().withAppendedChild("save-failed"));
                audience.showDialog(new ErrorDialog(message, fishRaritiesDialog).build());
            }
        });
        return DialogType.confirmation(confirm, backButton(showDialog(fishRaritiesDialog)));
    }
}
