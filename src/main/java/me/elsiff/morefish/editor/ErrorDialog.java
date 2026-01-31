package me.elsiff.morefish.editor;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static me.elsiff.morefish.MoreFish.lang;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class ErrorDialog extends MusiDialog {

    private final Component errorMessage;
    private final MusiDialog previousDialog;

    public ErrorDialog(Component errorMessage, MusiDialog previousDialog) {
        super(lang().getComponent("editor", "error"));
        this.errorMessage = errorMessage;
        this.previousDialog = previousDialog;
    }

    @Override
    protected List<DialogBody> body() {
        return List.of(DialogBody.plainMessage(errorMessage));
    }

    @Override
    protected DialogType type() {
        return DialogType.notice(backButton((view, audience) -> audience.showDialog(previousDialog.build())));
    }
}
