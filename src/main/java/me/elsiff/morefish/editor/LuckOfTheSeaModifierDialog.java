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
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.codehaus.plexus.util.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.util.Arrays;
import java.util.List;

import static me.elsiff.morefish.MoreFish.lang;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class LuckOfTheSeaModifierDialog extends MusiDialog {

    private static final String TYPE = "type";
    private static final String AMOUNT = "amount";
    private final FishRarityDialog fishRarityDialog;

    public LuckOfTheSeaModifierDialog(FishRarityDialog fishRarityDialog) {
        super(lang().getComponent(path().withAppendedChild("label")));
        this.fishRarityDialog = fishRarityDialog;
    }

    private static NodePath path() {
        return NodePath.path("editor", "rarity", "selected", "luck-of-the-sea-modifier");
    }

    @Override
    protected List<DialogInput> inputs() {
        LuckOfTheSeaModifier modifier = fishRarityDialog.fishAbstract.luckOfTheSeaModifier();
        List<OptionEntry> typeEntries = Arrays.stream(LuckOfTheSeaModifier.Type.values()).map(type -> {
            String typeString = StringUtils.capitalise(type.toString().toLowerCase());
            TagResolver resolver = TagResolver.resolver("modifier-type", Tag.selfClosingInserting(Component.text(typeString)));
            Component label = lang().getComponent(path().withAppendedChild("modifier-type"), resolver);
            return OptionEntry.create(type.toString(), label, type == modifier.type());
        }).toList();
        DialogInput type = singleOptionInput(TYPE, lang().getComponent(path().withAppendedChild("type")), typeEntries);
        DialogInput amount = textInput(AMOUNT, lang().getComponent("amount"), modifier.amount());
        return List.of(type, amount);
    }

    @Override
    protected DialogType type() {
        ActionButton yes = saveButton((view, audience) -> {
            Type type = Arrays.stream(Type.values()).filter(t -> t.toString().equalsIgnoreCase(view.getText(TYPE))).findFirst().orElse(Type.FLAT);
            Float amount = parseNumber(view.getText(AMOUNT), Float::parseFloat, f -> f >= 0);
            if (amount == null) {
                Component errorMessage = lang().getComponent(path().withAppendedChild("error"));
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
