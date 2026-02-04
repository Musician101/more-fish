package me.elsiff.morefish.editor.type;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
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
@SuppressWarnings("UnstableApiUsage")
public class NewFishTypeDialog extends MusiDialog {

    private static final String DISPLAY_NAME = "display_name";
    private static final String ID = "type_id";
    private static final String RARITY = "rarity";

    public NewFishTypeDialog() {
        super(lang().getComponent(path().withAppendedChild("label")));
    }

    private static NodePath path() {
        return NodePath.path("editor", "type", "selector", "new");
    }

    @Override
    protected List<DialogInput> inputs() {
        DialogInput id = textInput(ID, lang().getComponent(path().plus(NodePath.path("id", "label"))));
        DialogInput displayName = textInput(DISPLAY_NAME, lang().getComponent(path().plus(NodePath.path("display-name", "label"))));
        return List.of(id, displayName, rarity());
    }

    private DialogInput rarity() {
        List<OptionEntry> entries = new ArrayList<>();
        List<FishRarity> rarities = getPlugin().rarities().values();
        rarities.sort(Comparator.reverseOrder());
        FishRarity initial = rarities.getFirst();
        rarities.stream().sorted(Comparator.reverseOrder()).forEach(r -> {
            Component label = lang().getComponent(r, "main", "item-format", "display-name");
            entries.add(OptionEntry.create(r.getKey().asString(), label, initial.equals(r)));
        });
        Component label = lang().getComponent(path().withAppendedChild("rarity"));
        return singleOptionInput(RARITY, label, entries);
    }

    @Override
    protected DialogType type() {
        FishTypesDialog fishTypesDialog = new FishTypesDialog();
        ActionButton confirm = confirmButton((view, audience) -> {
            String keyString = view.getText(ID);
            Component errorMessage = lang().getComponent(path().plus(NodePath.path("id", "error", "format")));
            ErrorDialog errorDialog = new ErrorDialog(errorMessage, this);
            if (keyString == null) {
                audience.showDialog(errorDialog.build());
                return;
            }

            NamespacedKey typeKey = NamespacedKey.fromString(keyString);
            if (typeKey == null) {
                audience.showDialog(errorDialog.build());
                return;
            }

            if (getPlugin().types().get(typeKey).isPresent()) {
                Component alreadyExistsMessage = lang().getComponent(path().plus(NodePath.path("id", "error", "already-exists")));
                audience.showDialog(new ErrorDialog(alreadyExistsMessage, this).build());
                return;
            }

            String rarityString = view.getText(RARITY);
            NamespacedKey rarityKey = null;
            if (rarityString != null) {
                rarityKey = NamespacedKey.fromString(rarityString);
            }

            Optional<FishRarity> rarity = rarityKey == null ? Optional.empty() : getPlugin().rarities().get(rarityKey);
            if (rarity.isEmpty()) {
                Component rarityError = lang().getComponent(path().plus(NodePath.path("rarity", "error")));
                audience.showDialog(new ErrorDialog(rarityError, this).build());
                return;
            }

            String displayName = view.getText(DISPLAY_NAME);
            if (displayName == null || displayName.isBlank()) {
                Component displayNameError = lang().getComponent(path().plus(NodePath.path("display-name", "error")));
                audience.showDialog(new ErrorDialog(displayNameError, this).build());
                return;
            }

            try {
                FishType fishType = new FishType(typeKey, displayName, rarity.get());
                getPlugin().types().save(fishType);
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
