package me.elsiff.morefish.editor.conditions;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import me.elsiff.morefish.fish.condition.ThunderingCondition;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static me.elsiff.morefish.MoreFish.lang;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class ThunderingConditionDialog extends FishConditionDialog<ThunderingCondition> {

    private static final String THUNDERING = "thundering";

    public ThunderingConditionDialog(FishConditionsDialog fishConditionsDialog) {
        super("thundering", fishConditionsDialog);
    }

    @Override
    protected void callback(DialogResponseView view, Audience audience) {
        boolean b = Objects.requireNonNullElse(view.getBoolean(THUNDERING), false);
        attemptSave(audience, new ThunderingCondition(b));
    }

    @Override
    protected List<DialogInput> inputs() {
        Component label = lang().getComponent("editor", "enabled");
        return List.of(boolInput(THUNDERING, label, condition().value()));
    }

    @Override
    protected ThunderingCondition condition() {
        return fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().thundering().orElse(new ThunderingCondition(false));
    }

    @Override
    protected void condition(@Nullable ThunderingCondition condition) {
        fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().thundering(condition);
    }
}
