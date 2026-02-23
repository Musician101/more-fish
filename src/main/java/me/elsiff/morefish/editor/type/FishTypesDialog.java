package me.elsiff.morefish.editor.type;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class FishTypesDialog extends MusiDialog {

    public FishTypesDialog() {
        super(Component.translatable("morefish.editor.type.selector.label"));
    }

    @Override
    protected DialogType type() {
        List<ActionButton> buttons = new ArrayList<>();
        getPlugin().types().stream().sorted(FishType::compareTo).forEach(t -> buttons.add(button(t)));
        buttons.add(actionButton(Component.translatable("morefish.editor.type.selector.label"), showDialog(new NewFishTypeDialog())));
        return DialogType.multiAction(buttons, backButton(), 2);
    }

    private ActionButton button(FishType type) {
        return dialogButton(new FishTypeDialog(type));
    }
}
