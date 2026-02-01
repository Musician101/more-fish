package me.elsiff.morefish.editor.type;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.editor.AnnouncementDialog;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.editor.FishAbstractDialog;
import me.elsiff.morefish.editor.conditions.FishConditionsDialog;
import me.elsiff.morefish.editor.rarity.FishRaritiesDialog;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.fish.FishTypeTable;
import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
@SuppressWarnings("UnstableAPIUsage")
public class FishTypeDialog extends FishAbstractDialog<FishType> {

    private static final String MAX_LENGTH = "max_length";
    private static final String MIN_LENGTH = "min_length";
    private static final String RARITY = "rarity";

    public FishTypeDialog(FishType type) {
        super(label(type), type);
    }

    private static Component label(FishType type) {
        TagResolver resolver = TagResolver.resolver(type, type.rarity());
        return lang().getComponent(path().plus(NodePath.path("label", "internal")), resolver);
    }

    private static NodePath path() {
        return NodePath.path("editor", "type", "selected");
    }

    @Override
    protected DialogBase base() {
        Component externalLabel = lang().getComponent(path().plus(NodePath.path("label", "external")), fishAbstract);
        return DialogBase.builder(label).externalTitle(externalLabel).inputs(inputs()).body(body()).build();
    }

    @Override
    protected List<DialogInput> inputs() {
        return List.of(displayName(), noDisplay(), firework(), skipItemFormat(), doNotSell(), priceMultiplier(), minLength(), maxLength(), rarity(), commands());
    }

    private DialogInput minLength() {
        Component label = lang().getComponent(path().plus(NodePath.path("min-length", "label")));
        return textInput(MIN_LENGTH, label, fishAbstract.minLength());
    }

    private DialogInput maxLength() {
        Component label = lang().getComponent(path().plus(NodePath.path("max-length", "label")));
        return textInput(MAX_LENGTH, label, fishAbstract.maxLength());
    }

    private DialogInput rarity() {
        NodePath rarityPath = path().withAppendedChild("rarity");
        List<OptionEntry> entries = new ArrayList<>();
        getPlugin().getFishTypeTable().getRarities().stream().sorted(Comparator.reverseOrder()).forEach(r -> {
            FishRarity rarity = fishAbstract.rarity();
            Component label = lang().getComponent(rarityPath.withAppendedChild("rarity"), rarity);
            entries.add(OptionEntry.create(r.name(), label, rarity.equals(r)));
        });
        Component label = lang().getComponent(rarityPath.withAppendedChild("label"));
        return singleOptionInput(RARITY, label, entries);
    }

    @Override
    protected DialogType type() {
        List<ActionButton> buttons = new ArrayList<>();
        buttons.add(dialogButton(new AnnouncementDialog(this)));
        buttons.add(dialogButton(new FishConditionsDialog(this)));
        buttons.add(dialogButton(new FishIconDialog(this)));
        buttons.add(saveButton());
        buttons.add(deleteButton());
        return DialogType.multiAction(buttons, discardButton(), 2);
    }

    private ActionButton saveButton() {
        return saveButton((view, audience) -> {
            if (!saveInternal(view, audience)) {
                return;
            }

            Double maxLength = parseNumber(view.getText(MAX_LENGTH), Double::parseDouble, d -> d > 0);
            if (maxLength == null) {
                Component errorMessage = lang().getComponent(path().plus(NodePath.path("max-length", "error")));
                audience.showDialog(new ErrorDialog(errorMessage, this).build());
                return;
            }

            Double minLength = parseNumber(view.getText(MIN_LENGTH), Double::parseDouble, d -> d > 0 && d < maxLength);
            if (minLength == null) {
                Component errorMessage = lang().getComponent(path().plus(NodePath.path("min-length", "error")));
                audience.showDialog(new ErrorDialog(errorMessage, this).build());
                return;
            }

            String rarityString = view.getText(RARITY);
            FishTypeTable ftt = getPlugin().getFishTypeTable();
            Optional<FishRarity> rarity = ftt.getRarities().stream().filter(r -> r.name().equals(rarityString)).findFirst();
            if (rarity.isEmpty()) {
                Component errorMessage = lang().getComponent(path().withAppendedChild("rarity-error"));
                audience.showDialog(new ErrorDialog(errorMessage, this).build());
                return;
            }

            FishRarity oldRarity = fishAbstract.rarity();
            fishAbstract.rarity(rarity.get());
            try {
                ftt.saveType(fishAbstract, oldRarity);
                audience.showDialog(new FishTypesDialog().build());
            }
            catch (IOException e) {
                TagResolver tagResolver = TagResolver.resolver(fishAbstract, TagResolverUtil.error(e.getMessage()));
                Component message = lang().getComponent(path().withAppendedChild("save-failed"), tagResolver);
                getPlugin().getComponentLogger().error(message, e);
                audience.showDialog(new ErrorDialog(message, this).build());
            }
        });
    }

    private ActionButton deleteButton() {
        return deleteButton((view, audience) -> {
            try {
                getPlugin().getFishTypeTable().deleteType(fishAbstract);
                audience.showDialog(new FishTypesDialog().build());
            }
            catch (IOException e) {
                TagResolver tagResolver = TagResolver.resolver(fishAbstract, TagResolverUtil.error(e.getMessage()));
                Component message = lang().getComponent(path().withAppendedChild("delete-failed"), tagResolver);
                getPlugin().getComponentLogger().error(message, e);
                audience.showDialog(new ErrorDialog(message, this).build());
            }
        });
    }

    private ActionButton discardButton() {
        return discardButton(showDialog(new FishRaritiesDialog()));
    }

    @Override
    protected void save() throws IOException {
        getPlugin().getFishTypeTable().saveType(fishAbstract);
    }

    @Override
    protected Component generalErrorMessage(Throwable throwable) {
        TagResolver tagResolver = TagResolver.resolver(fishAbstract, TagResolverUtil.error(throwable.getMessage()));
        return lang().getComponent(path().withAppendedChild("save-failed"), tagResolver);
    }
}
