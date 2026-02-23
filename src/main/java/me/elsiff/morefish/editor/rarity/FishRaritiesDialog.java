package me.elsiff.morefish.editor.rarity;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class FishRaritiesDialog extends MusiDialog {

    public FishRaritiesDialog() {
        super(Component.translatable("morefish.editor.rarity.selector.label"));
    }

    @Override
    protected DialogType type() {
        List<ActionButton> buttons = new ArrayList<>();
        getPlugin().rarities().stream().sorted(Comparator.reverseOrder()).forEach(r -> buttons.add(button(r)));
        buttons.add(actionButton(Component.translatable("morefish.editor.rarity.selector.new"), showDialog(new NewFishRarityDialog())));
        return DialogType.multiAction(buttons, backButton(), 2);
    }

    private ActionButton button(FishRarity rarity) {
        return dialogButton(new FishRarityDialog(rarity));
    }
}
