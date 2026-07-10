package me.elsiff.morefish.editor.type;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput.Builder;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class DataComponentDialog extends MusiDialog {

    private static final String COMPONENT = "component";
    private final FishIconDialog fishIconDialog;
    private final String componentKey;
    @Nullable
    private final String dataComponent;

    public DataComponentDialog(Component label, FishIconDialog fishIconDialog, String componentKey, @Nullable String dataComponent, Locale locale) {
        super(label, locale);
        this.fishIconDialog = fishIconDialog;
        this.componentKey = componentKey;
        this.dataComponent = dataComponent;
    }

    @Override
    protected List<DialogInput> inputs() {
        Builder builder = DialogInput.text(COMPONENT, Component.empty()).labelVisible(false);
        if (dataComponent != null) {
            builder.initial(dataComponent);
        }

        return List.of(builder.build());
    }

    @Override
    protected DialogType type() {
        ActionButton applyButton = actionButton(translate("morefish.editor.apply"), (view, audience) -> {
            String component = view.getText(COMPONENT);
            if (component == null) {
                fishIconDialog.dataComponents.remove(componentKey);
            }
            else {
                fishIconDialog.dataComponents.put(componentKey, component);
            }

            audience.showDialog(fishIconDialog.build());
        });
        ActionButton discardButton = discardButton(showDialog(fishIconDialog));
        return DialogType.confirmation(applyButton, discardButton);
    }
}
