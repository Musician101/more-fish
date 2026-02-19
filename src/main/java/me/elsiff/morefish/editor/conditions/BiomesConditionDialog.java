package me.elsiff.morefish.editor.conditions;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import me.elsiff.morefish.fish.condition.BiomesCondition;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.block.Biome;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class BiomesConditionDialog extends FishConditionDialog<BiomesCondition> {

    public BiomesConditionDialog(FishConditionsDialog fishConditionsDialog) {
        super("biome", fishConditionsDialog);
    }

    @Override
    protected void callback(DialogResponseView view, Audience audience) {
        List<Biome> biomes = registryValues(RegistryKey.BIOME).filter(biome -> {
            Boolean bool = view.getBoolean(dialogKey(biome));
            return Objects.requireNonNullElse(bool, false);
        }).toList();
        attemptSave(audience, new BiomesCondition(biomes));
    }

    @Override
    protected @Nullable BiomesCondition condition() {
        return fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().biomes().orElse(null);
    }

    @Override
    protected void condition(@Nullable BiomesCondition condition) {
        fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().biomes(condition);
    }

    @Override
    protected List<DialogInput> inputs() {
        List<DialogInput> list = new ArrayList<>();
        registryValues(RegistryKey.BIOME).sorted(this::sort).forEach(biome -> {
            ComponentLike argument = Argument.component("biome", Component.translatable(biome.translationKey()));
            Component label = Component.translatable(conditionPath + "biome", argument);
            BiomesCondition condition = condition();
            boolean initial = condition != null && condition.value().contains(biome);
            list.add(boolInput(dialogKey(biome), label, initial));
        });
        return list;
    }

    private int sort(Biome b1, Biome b2) {
        PlainTextComponentSerializer ptcs = PlainTextComponentSerializer.plainText();
        String s1 = ptcs.serialize(Component.translatable(b1));
        String s2 = ptcs.serialize(Component.translatable(b2));
        return s1.compareTo(s2);
    }
}
