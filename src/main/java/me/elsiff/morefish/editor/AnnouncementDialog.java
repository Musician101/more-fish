package me.elsiff.morefish.editor;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.fish.FishAbstract;
import me.elsiff.morefish.fish.PlayerAnnouncement;
import me.elsiff.morefish.fish.PlayerAnnouncement.Type;
import me.elsiff.morefish.gui.MusiDialog;
import me.elsiff.morefish.util.EnumUtils;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.util.Arrays;
import java.util.List;

import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class AnnouncementDialog extends MusiDialog {

    private static final String TYPE = "type";
    private static final String RADIUS = "radius";
    private final FishAbstractDialog<?> fishAbstractDialog;

    public AnnouncementDialog(FishAbstractDialog<?> fishAbstractDialog) {
        super(lang().getComponent(path().withAppendedChild("label")));
        this.fishAbstractDialog = fishAbstractDialog;
    }

    private static NodePath path() {
        return NodePath.path("editor", "shared", "announcement");
    }

    @Override
    protected List<DialogInput> inputs() {
        return List.of(announcementType(), radius());
    }

    @Override
    protected DialogType type() {
        ActionButton yes = saveButton((view, audience) -> {
            Double radius = parseNumber(view.getText(RADIUS), Double::parseDouble, d -> d > 0);
            if (radius == null) {
                Component errorMessage = lang().getComponent(path().plus(NodePath.path(RADIUS, "error")));
                audience.showDialog(new ErrorDialog(errorMessage, this).build());
                return;
            }

            Type type = EnumUtils.get(view.getText(TYPE), Type.class, Type.SERVER);
            PlayerAnnouncement newAnnouncement = new PlayerAnnouncement(type, radius);
            FishAbstract<?> fishAbstract = fishAbstractDialog.fishAbstract;
            if (fishAbstractDialog.attemptSave(audience, newAnnouncement, fishAbstract.announcement(), fishAbstract::announcement)) {
                audience.showDialog(fishAbstractDialog.build());
            }
        });
        ActionButton no = discardButton(showDialog(fishAbstractDialog));
        return DialogType.confirmation(yes, no);
    }

    private DialogInput announcementType() {
        FishAbstract<?> fishAbstract = fishAbstractDialog.fishAbstract;
        List<OptionEntry> optionEntries = Arrays.stream(Type.values()).map(t -> {
            PlayerAnnouncement announcement = fishAbstract.announcement();
            Component label = lang().getComponent(path().withAppendedChild(t.toString().toLowerCase()), announcement);
            return OptionEntry.create(t.toString(), label, announcement.type() == t);
        }).toList();
        Component label = lang().getComponent(path().withAppendedChild(TYPE));
        return singleOptionInput(TYPE, label, optionEntries);
    }

    private DialogInput radius() {
        FishAbstract<?> fishAbstract = fishAbstractDialog.fishAbstract;
        Component label = lang().getComponent(path().plus(NodePath.path(RADIUS, "label")));
        return textInput(RADIUS, label, fishAbstract.announcement().radius());
    }
}
