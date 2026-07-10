package me.elsiff.morefish.editor.conditions;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import me.elsiff.morefish.fish.condition.TimeCondition;
import me.elsiff.morefish.fish.condition.TimeCondition.TimeState;
import me.elsiff.morefish.util.EnumUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class TimeConditionDialog extends FishConditionDialog<TimeCondition> {

    private static final String TIME = "time";

    public TimeConditionDialog(FishConditionsDialog fishConditionsDialog, Locale locale) {
        super("time", fishConditionsDialog, locale);
    }

    @Override
    protected void callback(DialogResponseView view, Audience audience) {
        String time = view.getText(TIME);
        TimeState timeState = EnumUtils.get(time, TimeState.class, TimeState.ANY);
        attemptSave(audience, new TimeCondition(timeState));
    }

    @Override
    protected List<DialogInput> inputs() {
        Component label = translate(conditionPath + "label");
        List<OptionEntry> options = Arrays.stream(TimeState.values()).map(this::option).toList();
        return List.of(singleOptionInput(TIME, label, options));
    }

    private OptionEntry option(TimeState timeState) {
        String s = timeState.toString().toLowerCase();
        return OptionEntry.create(s, translate(conditionPath + s), condition().value() == timeState);
    }

    @Override
    protected TimeCondition condition() {
        return fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().time().orElse(new TimeCondition(TimeState.ANY));
    }

    @Override
    protected void condition(@Nullable TimeCondition condition) {
        fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().time(condition);
    }
}
