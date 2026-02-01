package me.elsiff.morefish.editor.rarity;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.editor.AnnouncementDialog;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.editor.FishAbstractDialog;
import me.elsiff.morefish.editor.LuckOfTheSeaModifierDialog;
import me.elsiff.morefish.editor.conditions.FishConditionsDialog;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
@SuppressWarnings("UnstableAPIUsage")
public class FishRarityDialog extends FishAbstractDialog<FishRarity> {

    private static final String FILTER_DEFAULT = "filter_default";
    private static final String WEIGHT = "weight";

    public FishRarityDialog(FishRarity rarity) {
        super(label(rarity), rarity);
    }

    private static NodePath path() {
        return NodePath.path("editor", "rarity", "selected");
    }

    private static Component label(FishRarity rarity) {
        return lang().getComponent(path().plus(NodePath.path("label", "internal")), rarity);
    }

    @Override
    protected DialogBase base() {
        Component externalLabel = lang().getComponent(path().plus(NodePath.path("label", "external")), fishAbstract);
        return DialogBase.builder(label).externalTitle(externalLabel).inputs(inputs()).body(body()).build();
    }

    @Override
    public void save() throws IOException {
        getPlugin().getFishTypeTable().saveRarity(fishAbstract);
    }

    @Override
    protected Component generalErrorMessage(Throwable throwable) {
        TagResolver tagResolver = TagResolver.resolver(fishAbstract, TagResolverUtil.error(throwable.getMessage()));
        return lang().getComponent(path().withAppendedChild("save-failed"), tagResolver);
    }

    @Override
    protected List<DialogInput> inputs() {
        return List.of(displayName(), noDisplay(), filterDefault(), firework(), skipItemFormat(), weight(), doNotSell(), priceMultiplier(), commands());
    }

    private DialogInput filterDefault() {
        Component label = lang().getComponent(path().withAppendedChild("filter-default"));
        return boolInput(FILTER_DEFAULT, label, fishAbstract.filterDefaultEnabled());
    }

    private DialogInput weight() {
        Component label = lang().getComponent(path().plus(NodePath.path(WEIGHT, "label")));
        return textInput(WEIGHT, label, fishAbstract.weight());
    }

    @Override
    protected DialogType type() {
        List<ActionButton> buttons = new ArrayList<>();
        buttons.add(dialogButton(new AnnouncementDialog(this)));
        buttons.add(dialogButton(new ColorDialog(this)));
        buttons.add(dialogButton(new FishConditionsDialog(this)));
        buttons.add(dialogButton(new LuckOfTheSeaModifierDialog(this)));
        buttons.add(saveButton());
        buttons.add(deleteButton());
        ActionButton discardButton = discardButton(showDialog(new FishRaritiesDialog()));
        return DialogType.multiAction(buttons, discardButton, 2);
    }

    private ActionButton saveButton() {
        return saveButton((view, audience) -> {
            if (!saveInternal(view, audience)) {
                return;
            }

            Integer weight = parseNumber(view.getText(WEIGHT), Integer::parseInt, i -> i > 0);
            if (weight == null) {
                Component errorMessage = lang().getComponent(path().plus(NodePath.path(WEIGHT, "error")));
                audience.showDialog(new ErrorDialog(errorMessage, this).build());
                return;
            }

            setValue(view.getBoolean(FILTER_DEFAULT), fishAbstract::filterDefaultEnabled);
            setValue(weight, fishAbstract::weight);
            try {
                save();
                audience.showDialog(new FishRaritiesDialog().build());
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
                getPlugin().getFishTypeTable().deleteRarity(fishAbstract);
                audience.showDialog(new FishRaritiesDialog().build());
            }
            catch (IOException e) {
                TagResolver tagResolver = TagResolver.resolver(fishAbstract, TagResolverUtil.error(e.getMessage()));
                Component message = lang().getComponent(path().withAppendedChild("delete-failed"), tagResolver);
                getPlugin().getComponentLogger().error(message, e);
                audience.showDialog(new ErrorDialog(message, this).build());
            }
        });
    }
}
