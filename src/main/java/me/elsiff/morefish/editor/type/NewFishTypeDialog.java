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
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class NewFishTypeDialog extends MusiDialog {

    private static final String DISPLAY_NAME = "display_name";
    private static final String ID = "type_id";
    private static final String RARITY = "rarity";

    public NewFishTypeDialog(Locale locale) {
        super(Component.translatable("morefish.editor.type.new.label"), locale);
    }

    @Override
    protected List<DialogInput> inputs() {
        DialogInput id = textInput(ID, translate("morefish.editor.type.new.id.label"));
        DialogInput displayName = textInput(DISPLAY_NAME, translate("morefish.editor.type.new.display-name.label"));
        return List.of(id, displayName, rarity());
    }

    private DialogInput rarity() {
        List<OptionEntry> entries = new ArrayList<>();
        List<FishRarity> rarities = getPlugin().rarities().values();
        rarities.sort(Comparator.reverseOrder());
        FishRarity initial = rarities.getFirst();
        rarities.stream().sorted(Comparator.reverseOrder()).forEach(r -> {
            Component label = translate("morefish.editor.type.new.rarity.rarity", r);
            entries.add(OptionEntry.create(r.getKey().asString(), label, initial.equals(r)));
        });
        Component label = translate("morefish.editor.type.new.rarity.label");
        return singleOptionInput(RARITY, label, entries);
    }

    @Override
    protected DialogType type() {
        FishTypesDialog fishTypesDialog = new FishTypesDialog(locale);
        ActionButton confirm = confirmButton((view, audience) -> {
            String keyString = view.getText(ID);
            Component errorMessage = translate("morefish.editor.type.new.id.error.format");
            ErrorDialog errorDialog = new ErrorDialog(errorMessage, this, locale);
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
                Component alreadyExistsMessage = translate("morefish.editor.type.new.id.error.already-exists");
                audience.showDialog(new ErrorDialog(alreadyExistsMessage, this, locale).build());
                return;
            }

            String rarityString = view.getText(RARITY);
            NamespacedKey rarityKey = null;
            if (rarityString != null) {
                rarityKey = NamespacedKey.fromString(rarityString);
            }

            Optional<FishRarity> rarity = rarityKey == null ? Optional.empty() : getPlugin().rarities().get(rarityKey);
            if (rarity.isEmpty()) {
                Component rarityError = translate("morefish.editor.type.new.rarity.error");
                audience.showDialog(new ErrorDialog(rarityError, this, locale).build());
                return;
            }

            String displayName = view.getText(DISPLAY_NAME);
            if (displayName == null || displayName.isBlank()) {
                Component displayNameError = translate("morefish.editor.type.new.display-name.error");
                audience.showDialog(new ErrorDialog(displayNameError, this, locale).build());
                return;
            }

            try {
                FishType fishType = new FishType(typeKey, displayName, rarity.get());
                getPlugin().types().save(fishType);
                audience.showDialog(fishTypesDialog.build());
            }
            catch (IOException e) {
                Component message = translate("morefish.editor.type.new.save-failed", Argument.string("name", keyString));
                audience.showDialog(new ErrorDialog(message, fishTypesDialog, locale).build());
                getPlugin().getComponentLogger().error(message, e);
            }
        });
        return DialogType.confirmation(confirm, backButton((v, a) -> a.showDialog(fishTypesDialog.build())));
    }
}
