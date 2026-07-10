package me.elsiff.morefish.editor.rarity;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.editor.AnnouncementDialog;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.editor.FishAbstractDialog;
import me.elsiff.morefish.editor.LuckOfTheSeaModifierDialog;
import me.elsiff.morefish.editor.conditions.FishConditionsDialog;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.lang.ArgumentUtil;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class FishRarityDialog extends FishAbstractDialog<FishRarity> {

    private static final String FILTER_DEFAULT = "filter_default";
    private static final String WEIGHT = "weight";

    public FishRarityDialog(FishRarity rarity, Locale locale) {
        super(Component.translatable("morefish.editor.rarity.selected.label.internal", rarity), rarity, locale);
    }

    @Override
    protected DialogBase base() {
        Component externalLabel = translate("morefish.editor.rarity.selected.label.external", fishAbstract);
        return DialogBase.builder(label).externalTitle(externalLabel).inputs(inputs()).body(body()).build();
    }

    @Override
    public void save() throws IOException {
        getPlugin().rarities().save(fishAbstract);
    }

    @Override
    protected Component generalErrorMessage(Throwable throwable) {
        return translate("morefish.editor.rarity.selected.save-failed", ArgumentUtil.error(throwable.getMessage()), fishAbstract);
    }

    @Override
    protected List<DialogInput> inputs() {
        return List.of(displayName(), noDisplay(), filterDefault(), firework(), skipItemFormat(), weight(), doNotSell(), priceMultiplier(), commands());
    }

    private DialogInput filterDefault() {
        Component label = translate("morefish.editor.rarity.selected.filter-default");
        return boolInput(FILTER_DEFAULT, label, fishAbstract.filterDefaultEnabled());
    }

    private DialogInput weight() {
        Component label = translate("morefish.editor.rarity.selected.weight.label");
        return textInput(WEIGHT, label, fishAbstract.weight());
    }

    @Override
    protected DialogType type() {
        List<ActionButton> buttons = new ArrayList<>();
        buttons.add(dialogButton(new AnnouncementDialog(this, locale)));
        buttons.add(dialogButton(new ColorDialog(this, locale)));
        buttons.add(dialogButton(new FishConditionsDialog(this, locale)));
        buttons.add(dialogButton(new LuckOfTheSeaModifierDialog(this, locale)));
        buttons.add(saveButton());
        buttons.add(deleteButton());
        ActionButton discardButton = discardButton(showDialog(new FishRaritiesDialog(locale)));
        return DialogType.multiAction(buttons, discardButton, 2);
    }

    private ActionButton saveButton() {
        return saveButton((view, audience) -> {
            if (!saveInternal(view, audience)) {
                return;
            }

            Integer weight = parseNumber(view.getText(WEIGHT), Integer::parseInt, i -> i > 0);
            if (weight == null) {
                Component errorMessage = translate("morefish.editor.rarity.selected.weight.error");
                audience.showDialog(new ErrorDialog(errorMessage, this, locale).build());
                return;
            }

            setValue(view.getBoolean(FILTER_DEFAULT), fishAbstract::filterDefaultEnabled);
            setValue(weight, fishAbstract::weight);
            try {
                save();
                audience.showDialog(new FishRaritiesDialog(locale).build());
            }
            catch (IOException e) {
                Component message = translate("morefish.editor.rarity.selected.save-failed", ArgumentUtil.error(e.getMessage()), fishAbstract);
                getPlugin().getComponentLogger().error(message, e);
                audience.showDialog(new ErrorDialog(message, this, locale).build());
            }
        });
    }

    private ActionButton deleteButton() {
        return deleteButton((view, audience) -> {
            try {
                getPlugin().rarities().delete(fishAbstract);
                audience.showDialog(new FishRaritiesDialog(locale).build());
            }
            catch (IOException e) {
                Component message = translate("morefish.editor.rarity.selected.delete-failed", ArgumentUtil.error(e.getMessage()), fishAbstract);
                getPlugin().getComponentLogger().error(message, e);
                audience.showDialog(new ErrorDialog(message, this, locale).build());
            }
        });
    }
}
