package me.elsiff.morefish.gui;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translatable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public abstract class MusiDialog {

    public static final ClickCallback.Options DEFAULT_CALLBACK_OPTIONS = ClickCallback.Options.builder().uses(1).lifetime(ClickCallback.DEFAULT_LIFETIME).build();

    protected final Component label;
    protected final Locale locale;

    public MusiDialog(Component label, Locale locale) {
        this.locale = locale;
        this.label = label instanceof TranslatableComponent ? translate((TranslatableComponent) label) : label;
    }

    public Component label() {
        return label;
    }

    public Dialog build() {
        return Dialog.create(b -> b.empty().type(type()).base(base()));
    }

    protected Component translate(Translatable translatable) {
        return translate(Component.translatable(translatable));
    }

    protected Component translate(String translationKey, ComponentLike... arguments) {
        return translate(Component.translatable(translationKey, arguments));
    }

    protected Component translate(TranslatableComponent component) {
        return GlobalTranslator.render(component, locale);
    }

    protected DialogAction customClick(DialogActionCallback callback) {
        return DialogAction.customClick(callback, DEFAULT_CALLBACK_OPTIONS);
    }

    protected ActionButton backButton() {
        return backButton((view, audience) -> {

        });
    }

    protected ActionButton backButton(DialogActionCallback callback) {
        return actionButton(translate("morefish.gui.back"), callback);
    }

    protected ActionButton confirmButton(DialogActionCallback callback) {
        return actionButton(translate("morefish.editor.confirm"), callback);
    }

    protected ActionButton dialogButton(MusiDialog dialog) {
        return actionButton(dialog.label(), showDialog(dialog));
    }

    protected ActionButton deleteButton(DialogActionCallback callback) {
        return actionButton(translate("morefish.editor.delete"), callback);
    }

    protected ActionButton discardButton(DialogActionCallback callback) {
        return actionButton(translate("morefish.editor.discard"), callback);
    }

    protected ActionButton saveButton(DialogActionCallback callback) {
        return actionButton(translate("morefish.editor.save"), callback);
    }

    protected ActionButton actionButton(Component label, DialogActionCallback callback) {
        return ActionButton.builder(label).action(customClick(callback)).build();
    }

    protected DialogActionCallback showDialog(MusiDialog musiDialog) {
        return (view, audience) -> audience.showDialog(musiDialog.build());
    }

    protected DialogBase base() {
        return DialogBase.builder(label).externalTitle(label).inputs(inputs()).body(body()).build();
    }

    protected DialogInput boolInput(String key, Component label, boolean initial) {
        return DialogInput.bool(key, label).initial(initial).build();
    }

    protected DialogInput singleOptionInput(String key, Component label, List<OptionEntry> entries) {
        return DialogInput.singleOption(key, label, entries).build();
    }

    protected DialogInput textInput(String key, Component label) {
        return DialogInput.text(key, label).build();
    }

    protected DialogInput textInput(String key, Component label, Object initial) {
        return DialogInput.text(key, label).initial(initial.toString()).build();
    }

    protected List<DialogInput> inputs() {
        return List.of();
    }

    protected abstract DialogType type();

    protected List<DialogBody> body() {
        return List.of();
    }

    @Nullable
    protected <N extends Number> N parseNumber(@Nullable String string, Function<String, N> parser, Predicate<N> validator) {
        if (string == null) {
            return null;
        }

        try {
            N number = parser.apply(string);
            if (validator.test(number)) {
                return number;
            }
        }
        catch (NumberFormatException ignored) {

        }

        return null;
    }
}
