package me.elsiff.morefish.editor.conditions;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.fish.condition.McmmoSkillsCondition;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class McmmoSkillsConditionDialog extends FishConditionDialog<McmmoSkillsCondition> {

    public McmmoSkillsConditionDialog(FishConditionsDialog fishConditionsDialog) {
        super("mcmmo-skills", fishConditionsDialog);
    }

    @Override
    protected List<DialogInput> inputs() {
        List<DialogInput> list = new ArrayList<>();
        Arrays.stream(PrimarySkillType.values()).sorted(Comparator.comparing(PrimarySkillType::toString)).forEach(s -> {
            int level = condition().value().getOrDefault(s, 0);
            String skillName = s.toString().toLowerCase();
            ComponentLike argument = Argument.string("mcmmo-skill", s.toString());
            Component label = Component.translatable(conditionPath + "mcmmo-skill", argument);
            list.add(textInput(skillName, label, level));
        });
        return list;
    }

    @Override
    protected void callback(DialogResponseView view, Audience audience) {
        Map<PrimarySkillType, Integer> skills = new HashMap<>();
        for (PrimarySkillType s : PrimarySkillType.values()) {
            Integer level = parseNumber(s.toString(), Integer::parseInt, i -> i >= 0);
            if (level == null) {
                Component errorMessage = Component.translatable(conditionPath + "error");
                audience.showDialog(new ErrorDialog(errorMessage, this).build());
                return;
            }

            skills.put(s, level);
        }

        attemptSave(audience, new McmmoSkillsCondition(skills));
    }

    @Override
    protected McmmoSkillsCondition condition() {
        return fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().mcmmoSkills().orElse(new McmmoSkillsCondition(Map.of()));
    }

    @Override
    protected void condition(@Nullable McmmoSkillsCondition condition) {
        fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().mcmmoSkills(condition);
    }
}
