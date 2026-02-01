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
import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.util.ArrayList;
import java.util.List;

import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class ColorDialog extends MusiDialog {

    private final FishRarityDialog rarityDialog;

    public ColorDialog(FishRarityDialog rarityDialog) {
        super(lang().getComponent(path().withAppendedChild("label")));
        this.rarityDialog = rarityDialog;
    }

    private static NodePath path() {
        return NodePath.path("editor", "rarity", "selected", "color");
    }

    @Override
    public Dialog build() {
        return build(rarityDialog.fishAbstract().color());
    }

    private Dialog build(TextColor color) {
        return Dialog.create(b -> {
            Component label = lang().getComponent(path().withAppendedChild("label"));
            DialogInput red = colorSlider(path(), "red", color.red());
            DialogInput green = colorSlider(path(), "green", color.green());
            DialogInput blue = colorSlider(path(), "blue", color.blue());
            String testPhrase = lang().rawString(path().withAppendedChild("test-phrase"));
            DialogBody body = DialogBody.plainMessage(Component.text(testPhrase, color));
            DialogBase base = DialogBase.builder(label).externalTitle(label).inputs(List.of(red, green, blue)).body(List.of(body)).build();
            b.empty().base(base).type(type());
        });
    }

    private DialogInput colorSlider(NodePath colorPath, String colorType, float value) {
        Component label = lang().getComponent(colorPath.withAppendedChild(colorType));
        return numberRangeInput(colorType, label, 0, 255, value, 1f);
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
        buttons.add(actionButton(lang().getComponent("editor", "test"), (view, audience) -> {
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
        Component label = lang().getComponent(path().withAppendedChild("preset-color"), TagResolverUtil.namedTextColor(color));
        return actionButton(label, (v, a) -> a.showDialog(build(color)));
    }

    private float getFloat(DialogResponseView view, String key) {
        Float value = view.getFloat(key);
        return (value == null ? 0 : value) / 0xFF;
    }
}
