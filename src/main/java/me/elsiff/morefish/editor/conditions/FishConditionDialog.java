package me.elsiff.morefish.editor.conditions;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.fish.condition.FishCondition;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Keyed;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.NodePath;

import java.util.List;
import java.util.stream.Stream;

import static me.elsiff.morefish.MoreFish.lang;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public abstract class FishConditionDialog<C extends FishCondition<?>> extends MusiDialog {

    protected final FishConditionsDialog fishConditionsDialog;
    protected final NodePath conditionPath;

    protected FishConditionDialog(String conditionType, FishConditionsDialog fishConditionsDialog) {
        super(lang().getComponent(path(conditionType).withAppendedChild("label")));
        this.fishConditionsDialog = fishConditionsDialog;
        this.conditionPath = path(conditionType);
    }

    private static NodePath path(String conditionType) {
        return NodePath.path("editor", "shared", "conditions", conditionType);
    }

    protected <V extends Keyed> Stream<V> registryValues(RegistryKey<V> registry) {
        return RegistryAccess.registryAccess().getRegistry(registry).stream();
    }

    protected String dialogKey(Keyed keyed) {
        return keyed.key().asString().replace(":", "__");
    }

    protected abstract void callback(DialogResponseView view, Audience audience);

    protected void attemptSave(Audience audience, C newValue) {
        if (fishConditionsDialog.fishAbstractDialog.attemptSave(audience, newValue, condition(), this::condition)) {
            audience.showDialog(fishConditionsDialog.build());
        }
    }

    @Nullable
    protected abstract C condition();

    protected abstract void condition(@Nullable C condition);

    @Override
    protected DialogType type() {
        ActionButton save = saveButton(this::callback);
        ActionButton delete = deleteButton((v, a) -> {
            condition(null);
            a.showDialog(fishConditionsDialog.build());
        });
        ActionButton discard = discardButton(showDialog(fishConditionsDialog));
        return DialogType.multiAction(List.of(save, delete), discard, 2);
    }
}
