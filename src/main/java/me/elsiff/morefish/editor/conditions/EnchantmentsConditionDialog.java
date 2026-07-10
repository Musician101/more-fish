package me.elsiff.morefish.editor.conditions;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.fish.condition.EnchantmentsCondition;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.enchantments.Enchantment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class EnchantmentsConditionDialog extends FishConditionDialog<EnchantmentsCondition> {

    public EnchantmentsConditionDialog(FishConditionsDialog fishConditionsDialog, Locale locale) {
        super("enchantments", fishConditionsDialog, locale);
    }

    @Override
    protected List<DialogInput> inputs() {
        List<DialogInput> list = new ArrayList<>();
        registryValues(RegistryKey.ENCHANTMENT).sorted(this::sort).forEach(enchantment -> {
            ComponentLike argument = Argument.component("enchantment", translate(enchantment));
            Component label = translate(conditionPath + "enchantment", argument);
            int level = condition().value().getOrDefault(enchantment, 0);
            list.add(textInput(dialogKey(enchantment), label, level));
        });
        return list;
    }

    private int sort(Enchantment e1, Enchantment e2) {
        PlainTextComponentSerializer ptcs = PlainTextComponentSerializer.plainText();
        String s1 = ptcs.serialize(Component.translatable(e1));
        String s2 = ptcs.serialize(Component.translatable(e2));
        return s1.compareTo(s2);
    }

    @Override
    protected void callback(DialogResponseView view, Audience audience) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        for (Enchantment enchantment : registryValues(RegistryKey.ENCHANTMENT).toList()) {
            Integer level = parseNumber(view.getText(dialogKey(enchantment)), Integer::parseInt, i -> i >= 0);
            if (level == null) {
                Component errorMessage = translate(conditionPath + "error");
                audience.showDialog(new ErrorDialog(errorMessage, this, locale).build());
                return;
            }

            enchantments.put(enchantment, level);
        }

        attemptSave(audience, new EnchantmentsCondition(enchantments));
    }

    @Override
    protected EnchantmentsCondition condition() {
        return fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().enchantments().orElse(new EnchantmentsCondition(Map.of()));
    }

    @Override
    protected void condition(@Nullable EnchantmentsCondition condition) {
        fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().enchantments(condition);
    }
}
