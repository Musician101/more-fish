package me.elsiff.morefish.editor.type;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.editor.AnnouncementDialog;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.editor.FishAbstractDialog;
import me.elsiff.morefish.editor.conditions.FishConditionsDialog;
import me.elsiff.morefish.editor.rarity.FishRaritiesDialog;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.lang.ArgumentUtil;
import net.kyori.adventure.text.Component;
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
public class FishTypeDialog extends FishAbstractDialog<FishType> {

    private static final String MAX_LENGTH = "max_length";
    private static final String MIN_LENGTH = "min_length";
    private static final String RARITY = "rarity";

    public FishTypeDialog(FishType type, Locale locale) {
        super(Component.translatable("morefish.editor.type.selected.label.internal", type, type.rarity()), type, locale);
    }

    @Override
    protected DialogBase base() {
        Component externalLabel = translate("morefish.editor.type.selected.label.external", fishAbstract);
        return DialogBase.builder(label).externalTitle(externalLabel).inputs(inputs()).body(body()).build();
    }

    @Override
    protected List<DialogInput> inputs() {
        return List.of(displayName(), noDisplay(), firework(), skipItemFormat(), doNotSell(), priceMultiplier(), minLength(), maxLength(), rarity(), commands());
    }

    private DialogInput minLength() {
        Component label = translate("morefish.editor.type.selected.min-length.label");
        return textInput(MIN_LENGTH, label, fishAbstract.minLength());
    }

    private DialogInput maxLength() {
        Component label = translate("morefish.editor.type.selected.max-length.label");
        return textInput(MAX_LENGTH, label, fishAbstract.maxLength());
    }

    private DialogInput rarity() {
        List<OptionEntry> entries = new ArrayList<>();
        getPlugin().rarities().stream().sorted(Comparator.reverseOrder()).forEach(r -> {
            FishRarity rarity = fishAbstract.rarity();
            Component label = translate("morefish.editor.type.selected.rarity.rarity", rarity);
            entries.add(OptionEntry.create(r.getKey().asString(), label, rarity.equals(r)));
        });
        Component label = translate("morefish.editor.type.selected.rarity.label");
        return singleOptionInput(RARITY, label, entries);
    }

    @Override
    protected DialogType type() {
        List<ActionButton> buttons = new ArrayList<>();
        buttons.add(dialogButton(new AnnouncementDialog(this, locale)));
        buttons.add(dialogButton(new FishConditionsDialog(this, locale)));
        buttons.add(dialogButton(new FishIconDialog(this, locale)));
        buttons.add(saveButton());
        buttons.add(deleteButton());
        return DialogType.multiAction(buttons, discardButton(), 2);
    }

    private ActionButton saveButton() {
        return saveButton((view, audience) -> {
            if (!saveInternal(view, audience)) {
                return;
            }

            Double maxLength = parseNumber(view.getText(MAX_LENGTH), Double::parseDouble, d -> d > 0);
            if (maxLength == null) {
                Component errorMessage = translate("morefish.editor.type.selected.max-length.error");
                audience.showDialog(new ErrorDialog(errorMessage, this, locale).build());
                return;
            }

            Double minLength = parseNumber(view.getText(MIN_LENGTH), Double::parseDouble, d -> d > 0 && d < maxLength);
            if (minLength == null) {
                Component errorMessage = translate("morefish.editor.type.selected.min-length.error");
                audience.showDialog(new ErrorDialog(errorMessage, this, locale).build());
                return;
            }

            String rarityString = view.getText(RARITY);
            NamespacedKey rarityKey = null;
            if (rarityString != null) {
                rarityKey = NamespacedKey.fromString(rarityString);
            }

            Optional<FishRarity> rarity = rarityKey == null ? Optional.empty() : getPlugin().rarities().get(rarityKey);
            if (rarity.isEmpty()) {
                Component errorMessage = translate("morefish.editor.type.selected.rarity.error");
                audience.showDialog(new ErrorDialog(errorMessage, this, locale).build());
                return;
            }

            FishRarity oldRarity = fishAbstract.rarity();
            double oldMinLength = fishAbstract.minLength();
            double oldMaxLength = fishAbstract.maxLength();
            fishAbstract.rarity(rarity.get());
            fishAbstract.minLength(minLength);
            fishAbstract.maxLength(maxLength);
            try {
                getPlugin().types().save(fishAbstract);
                audience.showDialog(new FishTypesDialog(locale).build());
            }
            catch (IOException e) {
                fishAbstract.rarity(oldRarity);
                fishAbstract.minLength(oldMinLength);
                fishAbstract.maxLength(oldMaxLength);
                Component message = translate("morefish.editor.type.selected.save-failed", ArgumentUtil.error(e.getMessage()), fishAbstract);
                getPlugin().getComponentLogger().error(message, e);
                audience.showDialog(new ErrorDialog(message, this, locale).build());
            }
        });
    }

    private ActionButton deleteButton() {
        return deleteButton((view, audience) -> {
            try {
                getPlugin().types().delete(fishAbstract);
                audience.showDialog(new FishTypesDialog(locale).build());
            }
            catch (IOException e) {
                Component message = translate("morefish.editor.rarity.selected.delete-failed", ArgumentUtil.error(e.getMessage()), fishAbstract);
                getPlugin().getComponentLogger().error(message, e);
                audience.showDialog(new ErrorDialog(message, this, locale).build());
            }
        });
    }

    private ActionButton discardButton() {
        return discardButton(showDialog(new FishRaritiesDialog(locale)));
    }

    @Override
    protected void save() throws IOException {
        getPlugin().types().save(fishAbstract);
    }

    @Override
    protected Component generalErrorMessage(Throwable throwable) {
        return translate("morefish.editor.type.selected.save-failed", ArgumentUtil.error(throwable.getMessage()), fishAbstract);
    }
}
