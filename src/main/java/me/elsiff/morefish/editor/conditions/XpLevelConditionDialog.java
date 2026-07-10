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
import java.util.Locale;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class XpLevelConditionDialog extends FishConditionDialog<XpLevelCondition> {

    private static final String XP_LEVEL = "xp_level";

    public XpLevelConditionDialog(FishConditionsDialog fishConditionsDialog, Locale locale) {
        super("xp-level", fishConditionsDialog, locale);
    }

    @Override
    protected void callback(DialogResponseView view, Audience audience) {
        Integer level = parseNumber(view.getText(XP_LEVEL), Integer::parseInt, i -> i >= 0);
        if (level == null) {
            Component errorMessage = translate(conditionPath + "error");
            audience.showDialog(new ErrorDialog(errorMessage, this, locale).build());
            return;
        }

        attemptSave(audience, new XpLevelCondition(level));
    }

    @Override
    protected List<DialogInput> inputs() {
        Component label = translate(conditionPath + "level");
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
