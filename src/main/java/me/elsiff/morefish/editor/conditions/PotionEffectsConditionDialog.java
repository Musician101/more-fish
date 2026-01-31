package me.elsiff.morefish.editor.conditions;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import me.elsiff.morefish.fish.condition.PotionEffectsCondition;
import me.elsiff.morefish.editor.ErrorDialog;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.elsiff.morefish.MoreFish.lang;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class PotionEffectsConditionDialog extends FishConditionDialog<PotionEffectsCondition> {

    public PotionEffectsConditionDialog(FishConditionsDialog fishConditionsDialog) {
        super("potion-effects", fishConditionsDialog);
    }

    @Override
    protected void callback(DialogResponseView view, Audience audience) {
        Map<PotionEffectType, Integer> potionEffects = new HashMap<>();
        for (PotionEffectType p : registryValues(RegistryKey.MOB_EFFECT).toList()) {
            Integer level = parseNumber(dialogKey(p), Integer::parseInt, i -> i >= 0);
            if (level == null) {
                Component errorMessage = lang().getComponent(conditionPath.withAppendedChild("error"));
                audience.showDialog(new ErrorDialog(errorMessage, this).build());
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
        registryValues(RegistryKey.MOB_EFFECT).sorted(this::sort).forEach(p -> {
            int level = condition().value().getOrDefault(p, 0);
            Component label = lang().getComponent(conditionPath.withAppendedChild("potion-effect"), Placeholder.component("potion-effect", Component.translatable(p)));
            list.add(textInput(dialogKey(p), label, level));
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
