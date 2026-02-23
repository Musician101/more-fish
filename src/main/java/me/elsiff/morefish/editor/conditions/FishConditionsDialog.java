package me.elsiff.morefish.editor.conditions;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.set.RegistrySet;
import me.elsiff.morefish.editor.FishAbstractDialog;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class FishConditionsDialog extends MusiDialog {

    final FishAbstractDialog<?> fishAbstractDialog;

    public FishConditionsDialog(FishAbstractDialog<?> fishAbstractDialog) {
        super(Component.translatable("morefish.editor.shared.conditions.label"));
        this.fishAbstractDialog = fishAbstractDialog;
    }

    @Override
    protected DialogType type() {
        List<Dialog> dialogs = new ArrayList<>();
        dialogs.add(new BiomesConditionDialog(this).build());
        dialogs.add(new EnchantmentsConditionDialog(this).build());
        dialogs.add(new LocationYConditionDialog(this).build());
        if (getPlugin().getMcmmo().hasHooked()) {
            dialogs.add(new McmmoSkillsConditionDialog(this).build());
        }

        dialogs.add(new PotionEffectsConditionDialog(this).build());
        dialogs.add(new RainingConditionDialog(this).build());
        dialogs.add(new ThunderingConditionDialog(this).build());
        dialogs.add(new TimeConditionDialog(this).build());
        dialogs.add(new XpLevelConditionDialog(this).build());
        return DialogType.dialogList(RegistrySet.valueSet(RegistryKey.DIALOG, dialogs)).exitAction(backButton(showDialog(fishAbstractDialog))).build();
    }
}
