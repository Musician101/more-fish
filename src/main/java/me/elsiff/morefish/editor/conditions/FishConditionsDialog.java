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
import java.util.Locale;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class FishConditionsDialog extends MusiDialog {

    final FishAbstractDialog<?> fishAbstractDialog;

    public FishConditionsDialog(FishAbstractDialog<?> fishAbstractDialog, Locale locale) {
        super(Component.translatable("morefish.editor.shared.conditions.label"), locale);
        this.fishAbstractDialog = fishAbstractDialog;
    }

    @Override
    protected DialogType type() {
        List<Dialog> dialogs = new ArrayList<>();
        dialogs.add(new BiomesConditionDialog(this, locale).build());
        dialogs.add(new EnchantmentsConditionDialog(this, locale).build());
        dialogs.add(new LocationYConditionDialog(this, locale).build());
        dialogs.add(new PotionEffectsConditionDialog(this, locale).build());
        dialogs.add(new RainingConditionDialog(this, locale).build());
        dialogs.add(new ThunderingConditionDialog(this, locale).build());
        dialogs.add(new TimeConditionDialog(this, locale).build());
        dialogs.add(new XpLevelConditionDialog(this, locale).build());
        return DialogType.dialogList(RegistrySet.valueSet(RegistryKey.DIALOG, dialogs)).exitAction(backButton(showDialog(fishAbstractDialog))).build();
    }
}
