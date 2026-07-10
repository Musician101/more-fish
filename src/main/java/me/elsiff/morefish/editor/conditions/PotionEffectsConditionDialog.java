package me.elsiff.morefish.editor.conditions;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.fish.condition.PotionEffectsCondition;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class PotionEffectsConditionDialog extends FishConditionDialog<PotionEffectsCondition> {

    public PotionEffectsConditionDialog(FishConditionsDialog fishConditionsDialog, Locale locale) {
        super("potion-effects", fishConditionsDialog, locale);
    }

    @Override
    protected void callback(DialogResponseView view, Audience audience) {
        Map<PotionEffectType, Integer> potionEffects = new HashMap<>();
        for (PotionEffectType p : registryValues(RegistryKey.MOB_EFFECT).toList()) {
            Integer level = parseNumber(dialogKey(p), Integer::parseInt, i -> i >= 0);
            if (level == null) {
                Component errorMessage = translate(conditionPath + "error");
                audience.showDialog(new ErrorDialog(errorMessage, this, locale).build());
                return;
            }

            potionEffects.put(p, level);
        }

        attemptSave(audience, new PotionEffectsCondition(potionEffects));
    }

    @Override
    protected PotionEffectsCondition condition() {
        return fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().potionEffects().orElse(new PotionEffectsCondition(Map.of()));
    }

    @Override
    protected void condition(@Nullable PotionEffectsCondition condition) {
        fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().potionEffects(condition);
    }

    @Override
    protected List<DialogInput> inputs() {
        List<DialogInput> list = new ArrayList<>();
        registryValues(RegistryKey.MOB_EFFECT).sorted(this::sort).forEach(potionEffectType -> {
            int level = condition().value().getOrDefault(potionEffectType, 0);
            ComponentLike argument = Argument.component("potion-effect", translate(potionEffectType));
            Component label = translate(conditionPath + "potion-effect", argument);
            list.add(textInput(dialogKey(potionEffectType), label, level));
        });
        return list;
    }

    private int sort(PotionEffectType p1, PotionEffectType p2) {
        PlainTextComponentSerializer ptcs = PlainTextComponentSerializer.plainText();
        String s1 = ptcs.serialize(Component.translatable(p1));
        String s2 = ptcs.serialize(Component.translatable(p2));
        return s1.compareTo(s2);
    }
}
