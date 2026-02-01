package me.elsiff.morefish.editor.type;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.editor.ErrorDialog;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.codehaus.plexus.util.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
@SuppressWarnings("UnstableAPIUsage")
public class FishIconDialog extends MusiDialog {

    private static final String ITEM_ID = "item_id";
    private static final String AMOUNT = "amount";
    final Map<String, String> dataComponents;
    private final FishTypeDialog fishTypeDialog;

    public FishIconDialog(FishTypeDialog fishTypeDialog) {
        super(lang().getComponent(path().withAppendedChild("label")));
        this.fishTypeDialog = fishTypeDialog;
        this.dataComponents = dataComponents();
    }

    private static NodePath path() {
        return NodePath.path("editor", "type", "selected", "icon");
    }

    private FishType fishType() {
        return fishTypeDialog.fishAbstract();
    }

    private ItemStack itemStack() {
        return fishType().icon().itemStack().clone();
    }

    private Map<String, String> dataComponents() {
        ItemStack itemStack = itemStack();
        Map<String, Object> serializedMap = itemStack.serialize();
        Map<String, String> dataComponents = new HashMap<>();
        Object object = serializedMap.get("components");
        if (object instanceof Map<?, ?> map) {
            map.forEach((k, v) -> dataComponents.put(k.toString(), v.toString()));
        }

        return dataComponents;
    }

    @Override
    protected List<DialogInput> inputs() {
        ItemStack itemStack = itemStack();
        DialogInput itemId = textInput(ITEM_ID, lang().getComponent(path().plus(NodePath.path("id", "label"))), itemStack.getType().key().asString());
        DialogInput amount = textInput(AMOUNT, lang().getComponent(path().plus(NodePath.path(AMOUNT, "label"))), itemStack.getAmount());
        return List.of(itemId, amount);
    }

    @SuppressWarnings("PatternValidation")
    @Override
    protected DialogType type() {
        List<ActionButton> buttons = new ArrayList<>();
        Arrays.stream(DataComponents.values()).forEach(d -> buttons.add(button(d)));
        //Arrays.stream(DataComponents.values()).forEach(d -> buttons.add(d.button(this)));
        buttons.add(saveButton((view, audience) -> {
            String id = view.getText(ITEM_ID);
            ErrorDialog idErrorDialog = new ErrorDialog(lang().getComponent(path().plus(NodePath.path("ID", "error"))), this);
            if (id == null) {
                audience.showDialog(idErrorDialog.build());
                return;
            }

            ItemType itemType = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).get(Key.key(id));
            if (itemType == null) {
                audience.showDialog(idErrorDialog.build());
                return;
            }

            Integer amount = parseNumber(view.getText(AMOUNT), Integer::parseInt, i -> i > 0);
            if (amount == null) {
                Component message = lang().getComponent(path().plus(NodePath.path(AMOUNT, "label")));
                audience.showDialog(new ErrorDialog(message, this).build());
                return;
            }

            Map<String, Object> map = itemStack().serialize();
            map.put("id", itemType.key().asString());
            map.put("count", amount);
            map.put("components", dataComponents);
            if (fishTypeDialog.attemptSave(audience, ItemStack.deserialize(map), itemStack(), fishType().icon()::itemStack)) {
                audience.showDialog(fishTypeDialog.build());
            }
        }));
        ActionButton discardButton = discardButton(showDialog(fishTypeDialog));
        return DialogType.multiAction(buttons, discardButton, 2);
    }

    private ActionButton button(DataComponents dataComponent) {
        String dataComponentString = dataComponent.toString();
        String component = dataComponents.get(dataComponentString);
        String name = StringUtils.capitaliseAllWords(dataComponentString.replaceAll("_", " "));
        TagResolver resolver = TagResolver.resolver("data-component", Tag.preProcessParsed(name));
        Component label = lang().getComponent(path().plus(NodePath.path("data-component", "label")), resolver);
        return dialogButton(new DataComponentDialog(label, this, dataComponentString, component));
    }

    // DataComponent implementation provided by Paper is still experimental and not fully implemented.
    // This enum is just a placeholder until their implementation is more complete, and I'm actually able to iterate all the DataComponents.
    // I also only need the DataComponents that are valid for ItemStack
    private enum DataComponents {
        ATTACK_RANGE,
        ATTRIBUTE_MODIFIERS,
        BANNER_PATTERNS,
        BASE_COLOR,
        BEES,
        BLOCK_ENTITY_DATA,
        BLOCK_STATE,
        BLOCKS_ATTACKS,
        BREAK_SOUND,
        BUCKET_ENTITY_DATA,
        BUNDLE_CONTENTS,
        CAN_BREAK,
        CAN_PLACE_ON,
        CHARGED_PROJECTILES,
        CONSUMABLE,
        CONTAINER,
        CONTAINER_LOOT,
        CUSTOM_DATA,
        CUSTOM_MODEL_DATA,
        CUSTOM_NAME,
        DAMAGE,
        DAMAGE_RESISTANT,
        DAMAGE_TYPE,
        DEATH_PROTECTION,
        DEBUG_STICK_STATE,
        DYED_COLOR,
        ENCHANTABLE,
        ENCHANTMENT_GLINT_OVERRIDE,
        ENCHANTMENTS,
        ENTITY_DATA,
        EQUIPPABLE,
        FIREWORK_EXPLOSION,
        FIREWORKS,
        FOOD,
        GLIDER,
        INSTRUMENT,
        INTANGIBLE_PROJECTILE,
        ITEM_MODEL,
        ITEM_NAME,
        JUKEBOX_PLAYABLE,
        KINETIC_WEAPON,
        LOCK,
        LODESTONE_TRACKER,
        LORE,
        MAP_COLOR,
        MAP_DECORATIONS,
        MAP_ID,
        MAX_DAMAGE,
        MAX_STACK_SIZE,
        MINIMUM_ATTACK_CHARGE,
        NOTE_BLOCK_SOUND,
        OMINOUS_BOTTLE_AMPLIFIER,
        PIERCING_WEAPON,
        POT_DECORATIONS,
        POTION_CONTENTS,
        POTION_DURATION_SCALE,
        PROFILE,
        PROVIDES_BANNER_PATTERNS,
        PROVIDES_TRIM_MATERIAL,
        RARITY,
        RECIPES,
        REPAIR_COST,
        REPAIRABLE,
        STORED_ENCHANTMENTS,
        SUSPICIOUS_STEW_EFFECTS,
        SWING_ANIMATION,
        TOOL,
        TOOLTIP_DISPLAY,
        TRIM,
        UNBREAKABLE,
        USE_COOLDOWN,
        USE_EFFECTS,
        USE_REMAINDER,
        WEAPON,
        WRITABLE_BOOK_CONTENT,
        WRITTEN_BOOK_CONTENT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
}
