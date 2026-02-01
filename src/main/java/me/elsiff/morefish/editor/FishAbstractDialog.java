package me.elsiff.morefish.editor;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput.MultilineOptions;
import me.elsiff.morefish.fish.FishAbstract;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.NodePath;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
@SuppressWarnings("UnstableAPIUsage")
public abstract class FishAbstractDialog<F extends FishAbstract<F>> extends MusiDialog {

    protected static final String COMMANDS = "commands";
    protected static final String DISPLAY_NAME = "display_name";
    protected static final String DO_NOT_SELL = "do_not_sell";
    protected static final String FIREWORK = "firework";
    protected static final String NO_DISPLAY_NAME = "no_display_name";
    protected static final String PRICE_MULTIPLIER = "price_multiplier";
    protected static final String SKIP_ITEM_FORMAT = "skip_item_format";
    protected final F fishAbstract;
    protected final NodePath sharedPath = NodePath.path("editor", "shared");

    public FishAbstractDialog(Component label, F fishAbstract) {
        super(label);
        this.fishAbstract = fishAbstract;
    }

    public F fishAbstract() {
        return fishAbstract;
    }

    protected DialogInput commands() {
        Component label = lang().getComponent(sharedPath.withAppendedChild(COMMANDS));
        String initial = String.join("\n", fishAbstract.commands());
        return DialogInput.text(COMMANDS, label).initial(initial).maxLength(Integer.MAX_VALUE).multiline(MultilineOptions.create(Integer.MAX_VALUE, 100)).build();
    }

    protected DialogInput displayName() {
        Component label = lang().getComponent(sharedPath.withAppendedChild("display-name"));
        return textInput(DISPLAY_NAME, label, fishAbstract.displayName());
    }

    protected DialogInput doNotSell() {
        Component label = lang().getComponent(sharedPath.withAppendedChild("do-not-sell"));
        return boolInput(DO_NOT_SELL, label, fishAbstract.doNotSell());
    }

    protected DialogInput firework() {
        Component label = lang().getComponent(sharedPath.withAppendedChild(FIREWORK));
        return boolInput(FIREWORK, label, fishAbstract.noDisplay());
    }

    protected DialogInput noDisplay() {
        Component label = lang().getComponent(sharedPath.withAppendedChild("no-display"));
        return boolInput(NO_DISPLAY_NAME, label, fishAbstract.noDisplay());
    }

    protected DialogInput priceMultiplier() {
        Component label = lang().getComponent(sharedPath.plus(NodePath.path("price-multiplier", "label")));
        return textInput(PRICE_MULTIPLIER, label, fishAbstract.priceMultiplier());
    }

    protected DialogInput skipItemFormat() {
        Component label = lang().getComponent(sharedPath.withAppendedChild("skip-item-format"));
        return boolInput(SKIP_ITEM_FORMAT, label, fishAbstract.noDisplay());
    }

    protected boolean saveInternal(DialogResponseView view, Audience audience) {
        Float priceMultiplier = parseNumber(view.getText(PRICE_MULTIPLIER), Float::parseFloat, f -> f > 0);
        if (priceMultiplier == null) {
            Component errorMessage = lang().getComponent(sharedPath.plus(NodePath.path("price-multiplier", "error")));
            audience.showDialog(new ErrorDialog(errorMessage, this).build());
            return false;
        }

        setValue(view.getText(DISPLAY_NAME), fishAbstract::displayName);
        setValue(view.getText(COMMANDS), c -> fishAbstract.commands(Arrays.asList(c.split("\\n"))));
        setValue(view.getBoolean(FIREWORK), fishAbstract::firework);
        setValue(view.getBoolean(NO_DISPLAY_NAME), fishAbstract::noDisplay);
        setValue(priceMultiplier, fishAbstract::priceMultiplier);
        setValue(view.getBoolean(SKIP_ITEM_FORMAT), fishAbstract::skipItemFormat);
        return true;
    }

    protected <V> void setValue(@Nullable V value, Consumer<V> applier) {
        if (value == null) {
            return;
        }

        applier.accept(value);
    }

    protected abstract void save() throws IOException;

    public <V> boolean attemptSave(Audience audience, @Nullable V newValue, @Nullable V oldValue, Consumer<@Nullable V> applier) {
        applier.accept(newValue);
        try {
            save();
            return true;
        }
        catch (IOException e) {
            applier.accept(oldValue);
            Component message = generalErrorMessage(e);
            getPlugin().getComponentLogger().error(message, e);
            audience.showDialog(new ErrorDialog(message, this).build());
            return false;
        }
    }

    protected abstract Component generalErrorMessage(Throwable throwable);
}
