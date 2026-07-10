package me.elsiff.morefish.editor.rarity;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.gui.MusiDialog;
import me.elsiff.morefish.lang.ArgumentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class NewFishRarityDialog extends MusiDialog {

    private static final String DISPLAY_NAME = "display_name";
    private static final String ID = "rarity_id";

    public NewFishRarityDialog(Locale locale) {
        super(Component.translatable("morefish.editor.rarity.selector.new.label"), locale);
    }

    @Override
    protected List<DialogInput> inputs() {
        DialogInput id = textInput(ID, translate("morefish.editor.rarity.new.id.label"));
        DialogInput displayName = textInput(DISPLAY_NAME, translate("morefish.editor.rarity.new.display-name.label"));
        return List.of(id, displayName);
    }

    @Override
    protected DialogType type() {
        FishRaritiesDialog fishRaritiesDialog = new FishRaritiesDialog(locale);
        ActionButton confirm = confirmButton((view, audience) -> {
            String keyString = view.getText(ID);
            Component errorMessage = translate("morefish.editor.rarity.new.id.error.format");
            ErrorDialog errorDialog = new ErrorDialog(errorMessage, this, locale);
            if (keyString == null) {
                audience.showDialog(errorDialog.build());
                return;
            }

            NamespacedKey rarityKey = NamespacedKey.fromString(keyString);
            if (rarityKey == null) {
                audience.showDialog(errorDialog.build());
                return;
            }

            if (getPlugin().rarities().get(rarityKey).isPresent()) {
                Component message = translate("morefish.editor.rarity.new.id.error.already-exists", Argument.string("id", keyString));
                audience.showDialog(new ErrorDialog(message, this, locale).build());
                return;
            }

            String displayName = view.getText(DISPLAY_NAME);
            if (displayName == null || displayName.isBlank()) {
                Component displayNameError = translate("morefish.editor.rarity.new.display-name.error");
                audience.showDialog(new ErrorDialog(displayNameError, this, locale).build());
                return;
            }

            FishRarity newRarity = new FishRarity(rarityKey, displayName);
            try {
                getPlugin().rarities().save(newRarity);
                audience.showDialog(fishRaritiesDialog.build());
            }
            catch (IOException e) {
                Component message = translate("morefish.editor.rarity.selector.save-failed", newRarity, ArgumentUtil.error(e.getMessage()));
                audience.showDialog(new ErrorDialog(message, fishRaritiesDialog, locale).build());
                getPlugin().getComponentLogger().error(message, e);
            }
        });
        return DialogType.confirmation(confirm, backButton(showDialog(fishRaritiesDialog)));
    }
}
