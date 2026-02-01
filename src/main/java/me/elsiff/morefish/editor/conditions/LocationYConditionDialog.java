package me.elsiff.morefish.editor.conditions;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.fish.condition.LocationYCondition;
import me.elsiff.morefish.util.Range;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.NodePath;

import java.util.List;
import java.util.function.Predicate;

import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class LocationYConditionDialog extends FishConditionDialog<LocationYCondition> {

    private static final String MAX = "max";
    private static final String MIN = "min";

    public LocationYConditionDialog(FishConditionsDialog fishConditionsDialog) {
        super("location-y", fishConditionsDialog);
    }

    @Override
    protected void callback(DialogResponseView view, Audience audience) {
        Double min = parse(MIN, view, audience, d -> true);
        if (min == null) {
            return;
        }

        Double max = parse(MAX, view, audience, d -> min <= d);
        if (max == null) {
            return;
        }

        attemptSave(audience, new LocationYCondition(new Range<>(min, max)));
    }

    @Nullable
    private Double parse(String key, DialogResponseView view, Audience audience, Predicate<Double> validator) {
        Double min = parseNumber(view.getText(key), Double::parseDouble, validator);
        if (min == null) {
            Component errorMessage = lang().getComponent(conditionPath.plus(NodePath.path(key, "error")));
            audience.showDialog(new ErrorDialog(errorMessage, this).build());
        }

        return min;
    }

    @Override
    protected LocationYCondition condition() {
        return fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().locationY().orElse(new LocationYCondition(new Range<>(-64D, 320D)));
    }

    @Override
    protected void condition(@Nullable LocationYCondition condition) {
        fishConditionsDialog.fishAbstractDialog.fishAbstract().conditions().locationY(new LocationYCondition(new Range<>(-64D, 320D)));
    }

    @Override
    protected List<DialogInput> inputs() {
        DialogInput min = input(MIN, condition().value().min());
        DialogInput max = input(MAX, condition().value().max());
        return List.of(min, max);
    }

    private DialogInput input(String key, double initial) {
        Component label = lang().getComponent(conditionPath.plus(NodePath.path(key, "label")));
        return textInput(key, label, initial);
    }

    @Override
    protected List<DialogBody> body() {
        return List.of(DialogBody.plainMessage(lang().getComponent(conditionPath.withAppendedChild("warning"))));
    }
}
