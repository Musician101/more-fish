package me.elsiff.morefish.editor.conditions;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import me.elsiff.morefish.fish.condition.RainingCondition;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class RainingConditionDialog extends FishConditionDialog<RainingCondition> {

    private static final String RAINING = "raining";

    public RainingConditionDialog(FishConditionsDialog fishConditionsDialog, Locale locale) {
        super("raining", fishConditionsDialog, locale);
    }

    @Override
    protected void callback(DialogResponseView view, Audience audience) {
        boolean b = Objects.requireNonNullElse(view.getBoolean(RAINING), false);
        attemptSave(audience, new RainingCondition(b));
    }

    @Override
    protected List<DialogInput> inputs() {
        Component label = translate("morefish.editor.enabled");
        return List.of(boolInput(RAINING, label, condition().value()));
    }

    @Override
    protected RainingCondition condition() {
        return fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().raining().orElse(new RainingCondition(false));
    }

    @Override
    protected void condition(@Nullable RainingCondition condition) {
        fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().raining(condition);
    }
}
