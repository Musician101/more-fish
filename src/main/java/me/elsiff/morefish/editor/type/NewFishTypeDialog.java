package me.elsiff.morefish.editor.type;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.fish.FishTypeTable;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
@SuppressWarnings("UnstableAPIUsage")
public class NewFishTypeDialog extends MusiDialog {

    private static final String NAME = "name";
    private static final String RARITY = "RARITY";

    public NewFishTypeDialog() {
        super(lang().getComponent(path().withAppendedChild("new")));
    }

    private static NodePath path() {
        return NodePath.path("editor", "type", "selector");
    }

    @Override
    protected List<DialogInput> inputs() {
        DialogInput name = textInput(NAME, lang().getComponent(path().withAppendedChild("name")));
        return List.of(name, rarity());
    }

    private DialogInput rarity() {
        List<OptionEntry> entries = new ArrayList<>();
        FishTypeTable ftt = getPlugin().getFishTypeTable();
        List<FishRarity> rarities = ftt.getRarities();
        rarities.sort(Comparator.reverseOrder());
        FishRarity initial = rarities.getFirst();
        rarities.stream().sorted(Comparator.reverseOrder()).forEach(r -> {
            Component label = lang().getComponent(r, "main", "item-format", "display-name");
            entries.add(OptionEntry.create(r.name(), label, initial.equals(r)));
        });
        Component label = lang().getComponent(path().withAppendedChild("rarity"));
        return singleOptionInput(RARITY, label, entries);
    }

    @Override
    protected DialogType type() {
        FishTypesDialog fishTypesDialog = new FishTypesDialog();
        ActionButton confirm = confirmButton((view, audience) -> {
            String name = view.getText(NAME);
            if (name == null) {
                audience.showDialog(fishTypesDialog.build());
                return;
            }

            FishTypeTable ftt = getPlugin().getFishTypeTable();
            if (ftt.getTypes().stream().anyMatch(t -> t.name().equals(name))) {
                Component message = lang().getComponent(path().withAppendedChild("error"));
                audience.showDialog(new ErrorDialog(message, this).build());
                return;
            }

            String rarityString = view.getText(RARITY);
            Optional<FishRarity> rarity = ftt.getRarities().stream().filter(r -> r.name().equals(rarityString)).findFirst();
            if (rarity.isEmpty()) {
                Component errorMessage = lang().getComponent(path().withAppendedChild("rarity-error"));
                audience.showDialog(new ErrorDialog(errorMessage, this).build());
                return;
            }

            try {
                FishType fishType = new FishType(name, rarity.get());
                ftt.saveType(fishType);
                audience.showDialog(fishTypesDialog.build());
            }
            catch (IOException e) {
                Component message = lang().getComponent(path().withAppendedChild("save-failed"));
                audience.showDialog(new ErrorDialog(message, fishTypesDialog).build());
            }
        });
        return DialogType.confirmation(confirm, backButton((v, a) -> a.showDialog(fishTypesDialog.build())));
    }
}
