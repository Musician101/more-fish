package me.elsiff.morefish.editor;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.editor.rarity.FishRarityDialog;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.LuckOfTheSeaModifier;
import me.elsiff.morefish.fish.LuckOfTheSeaModifier.Type;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.codehaus.plexus.util.StringUtils;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.List;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class LuckOfTheSeaModifierDialog extends MusiDialog {

    private static final String TYPE = "type";
    private static final String AMOUNT = "amount";
    private final FishRarityDialog fishRarityDialog;

    public LuckOfTheSeaModifierDialog(FishRarityDialog fishRarityDialog) {
        super(Component.translatable("morefish.editor.rarity.selected.luck-of-the-sea-modifier.label"));
        this.fishRarityDialog = fishRarityDialog;
    }

    @Override
    protected List<DialogInput> inputs() {
        LuckOfTheSeaModifier modifier = fishRarityDialog.fishAbstract.luckOfTheSeaModifier();
        List<OptionEntry> typeEntries = Arrays.stream(LuckOfTheSeaModifier.Type.values()).map(type -> {
            String typeString = StringUtils.capitalise(type.toString().toLowerCase());
            ComponentLike argument = Argument.string("modifier-type", typeString);
            Component label = Component.translatable("morefish.editor.rarity.selected.luck-of-the-sea-modifier.modifier-type", argument);
            return OptionEntry.create(type.toString(), label, type == modifier.type());
        }).toList();
        DialogInput type = singleOptionInput(TYPE, Component.translatable("morefish.editor.rarity.selected.luck-of-the-sea-modifier.type"), typeEntries);
        DialogInput amount = textInput(AMOUNT, Component.translatable("morefish.editor.rarity.selected.luck-of-the-sea-modifier.amount"), modifier.amount());
        return List.of(type, amount);
    }

    @Override
    protected DialogType type() {
        ActionButton yes = saveButton((view, audience) -> {
            Type type = Arrays.stream(Type.values()).filter(t -> t.toString().equalsIgnoreCase(view.getText(TYPE))).findFirst().orElse(Type.FLAT);
            Float amount = parseNumber(view.getText(AMOUNT), Float::parseFloat, f -> f >= 0);
            if (amount == null) {
                Component errorMessage = Component.translatable("morefish.editor.rarity.selected.luck-of-the-sea-modifier.error");
                audience.showDialog(new ErrorDialog(errorMessage, this).build());
                return;
            }

            FishRarity rarity = fishRarityDialog.fishAbstract;
            if (fishRarityDialog.attemptSave(audience, new LuckOfTheSeaModifier(type, amount), rarity.luckOfTheSeaModifier(), rarity::luckOfTheSeaModifier)) {
                audience.showDialog(fishRarityDialog.build());
            }
        });
        ActionButton no = discardButton(showDialog(fishRarityDialog));
        return DialogType.confirmation(yes, no);
    }
}
