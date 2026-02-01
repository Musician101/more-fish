package me.elsiff.morefish.editor.conditions;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.fish.condition.XpLevelCondition;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
@SuppressWarnings("UnstableAPIUsage")
public class XpLevelConditionDialog extends FishConditionDialog<XpLevelCondition> {

    private static final String XP_LEVEL = "xp_level";

    public XpLevelConditionDialog(FishConditionsDialog fishConditionsDialog) {
        super("xp-level", fishConditionsDialog);
    }

    @Override
    protected void callback(DialogResponseView view, Audience audience) {
        Integer level = parseNumber(view.getText(XP_LEVEL), Integer::parseInt, i -> i >= 0);
        if (level == null) {
            Component errorMessage = lang().getComponent(conditionPath.withAppendedChild("error"));
            audience.showDialog(new ErrorDialog(errorMessage, this).build());
            return;
        }

        attemptSave(audience, new XpLevelCondition(level));
    }

    @Override
    protected List<DialogInput> inputs() {
        Component label = lang().getComponent(conditionPath.withAppendedChild("level"));
        return List.of(textInput(XP_LEVEL, label, condition().value()));
    }

    @Override
    protected XpLevelCondition condition() {
        return fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().xpLevel().orElse(new XpLevelCondition(0));
    }

    @Override
    protected void condition(@Nullable XpLevelCondition condition) {
        fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().xpLevel(condition);
    }
}
