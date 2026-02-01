package me.elsiff.morefish.editor.rarity;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishTypeTable;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.io.IOException;
import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
@SuppressWarnings("UnstableAPIUsage")
public class NewFishRarityDialog extends MusiDialog {

    private static final String NAME = "name";

    public NewFishRarityDialog() {
        super(lang().getComponent(path().withAppendedChild("new")));
    }

    private static NodePath path() {
        return NodePath.path("editor", "rarity", "selector");
    }

    @Override
    protected List<DialogInput> inputs() {
        Component label = lang().getComponent(path().withAppendedChild("name"));
        return List.of(textInput(NAME, label));
    }

    @Override
    protected DialogType type() {
        FishRaritiesDialog fishRaritiesDialog = new FishRaritiesDialog();
        ActionButton confirm = confirmButton((v, a) -> {
            String name = v.getText(NAME);
            if (name == null) {
                a.showDialog(fishRaritiesDialog.build());
                return;
            }

            FishTypeTable ftt = getPlugin().getFishTypeTable();
            if (ftt.getRarities().stream().anyMatch(r -> r.name().equals(name))) {
                Component message = lang().getComponent(path().withAppendedChild("error"));
                a.showDialog(new ErrorDialog(message, this).build());
                return;
            }

            try {
                getPlugin().getFishTypeTable().saveRarity(new FishRarity(name, name));
                a.showDialog(fishRaritiesDialog.build());
            }
            catch (IOException e) {
                Component message = lang().getComponent(path().withAppendedChild("save-failed"));
                a.showDialog(new ErrorDialog(message, fishRaritiesDialog).build());
            }
        });
        return DialogType.confirmation(confirm, backButton(showDialog(fishRaritiesDialog)));
    }
}
