package me.elsiff.morefish.editor.rarity;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.gui.MusiDialog;
import me.elsiff.morefish.lang.ArgumentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class ColorDialog extends MusiDialog {

    private final FishRarityDialog rarityDialog;

    public ColorDialog(FishRarityDialog rarityDialog) {
        super(Component.translatable("morefish.editor.rarity.selected.color.label"));
        this.rarityDialog = rarityDialog;
    }

    @Override
    public Dialog build() {
        return build(rarityDialog.fishAbstract().color());
    }

    private Dialog build(TextColor color) {
        return Dialog.create(b -> {
            DialogInput red = colorSlider("red", color.red());
            DialogInput green = colorSlider("green", color.green());
            DialogInput blue = colorSlider("blue", color.blue());
            String testPhrase = lang().rawString("morefish.editor.rarity.selected.color.test-phrase");
            DialogBody body = DialogBody.plainMessage(Component.text(testPhrase, color));
            DialogBase base = DialogBase.builder(label).externalTitle(label).inputs(List.of(red, green, blue)).body(List.of(body)).build();
            b.empty().base(base).type(type());
        });
    }

    private DialogInput colorSlider(String colorType, float value) {
        Component label = Component.translatable("morefish.editor.rarity.selector.color" + colorType);
        return DialogInput.numberRange(colorType, label, 0, 255).initial(value).step(1F).build();
    }

    @Override
    protected DialogType type() {
        List<ActionButton> buttons = new ArrayList<>();
        NamedTextColor.NAMES.values().stream().sorted().forEach(color -> buttons.add(namedColorButton(color)));
        buttons.add(saveButton((view, audience) -> {
            float red = getFloat(view, "red");
            float green = getFloat(view, "green");
            float blue = getFloat(view, "blue");
            TextColor color = TextColor.color(red, green, blue);
            FishRarity rarity = rarityDialog.fishAbstract();
            if (rarityDialog.attemptSave(audience, color, rarity.color(), rarity::color)) {
                audience.showDialog(build());
            }
        }));
        buttons.add(actionButton(Component.translatable("morefish.editor.test"), (view, audience) -> {
            float red = getFloat(view, "red");
            float green = getFloat(view, "green");
            float blue = getFloat(view, "blue");
            TextColor color = TextColor.color(red, green, blue);
            audience.showDialog(build(color));
        }));
        ActionButton discard = discardButton(showDialog(rarityDialog));
        return DialogType.multiAction(buttons, discard, 2);
    }

    private ActionButton namedColorButton(NamedTextColor color) {
        Component label = Component.translatable("morefish.editor.rarity.selected.color.preset-color", ArgumentUtil.namedTextColor(color));
        return actionButton(label, (v, a) -> a.showDialog(build(color)));
    }

    private float getFloat(DialogResponseView view, String key) {
        Float value = view.getFloat(key);
        return (value == null ? 0 : value) / 0xFF;
    }
}
