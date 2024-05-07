package me.elsiff.morefish.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.replace;

public interface FishItemStackConverter {

    @NotNull
    static ItemStack createItemStack(@NotNull Fish fish, @NotNull Player catcher) {
        return createItemStack(fish, fish.length(), catcher);
    }

    @NotNull
    static ItemStack createItemStack(@NotNull Fish fish, double length, @NotNull Player catcher) {
        ItemStack itemStack = fish.type().icon().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!fish.type().hasNotFishItemFormat()) {
            Map<String, Object> replacement = getFormatReplacementMap(fish, length, catcher);
            MiniMessage mm = MiniMessage.miniMessage();
            itemMeta.displayName(replace(getFormatConfig().map(cs -> cs.get("display-name").getAsString()).orElse("null"), replacement, catcher));
            List<Component> lore = replace(getFormatConfig().map(json -> json.getAsJsonArray("lore").asList().stream().map(JsonElement::getAsString).collect(Collectors.toList())).orElse(new ArrayList<>()), replacement, catcher);
            List<Component> oldLore = itemMeta.lore();
            if (oldLore != null) {
                lore.addAll(oldLore.stream().map(c -> replace(mm.serialize(c), replacement, catcher)).toList());
            }

            itemMeta.lore(lore);
        }

        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        data.set(fishTypeKey(), PersistentDataType.STRING, fish.type().name());
        data.set(fishLengthKey(), PersistentDataType.DOUBLE, fish.length());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @NotNull
    static Fish fish(@NotNull ItemStack itemStack) {
        return read(itemStack.getItemMeta());
    }

    static NamespacedKey fishLengthKey() {
        return new NamespacedKey(getPlugin(), "fishLength");
    }

    static NamespacedKey fishTypeKey() {
        return new NamespacedKey(getPlugin(), "fishType");
    }

    private static Optional<JsonObject> getFormatConfig() {
        return getPlugin().getFishTypeTable().getItemFormat();
    }

    private static Map<String, Object> getFormatReplacementMap(Fish fish, double length, Player catcher) {
        return Map.of("%player%", catcher.getName(), "%rarity%", fish.type().rarity().name().toUpperCase(), "%rarity_color%", fish.type().rarity().color(), "%length%", length, "%fish%", fish.type().displayName());
    }

    static boolean isFish(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }

        PersistentDataContainer tags = itemMeta.getPersistentDataContainer();
        return tags.has(fishTypeKey(), PersistentDataType.STRING) && tags.has(fishLengthKey(), PersistentDataType.DOUBLE);
    }

    @NotNull
    private static Fish read(@NotNull ItemMeta itemMeta) {
        PersistentDataContainer tags = itemMeta.getPersistentDataContainer();
        if (!tags.has(fishTypeKey(), PersistentDataType.STRING)) {
            throw new IllegalArgumentException("Item meta must have fish type tag");
        }

        if (!tags.has(fishLengthKey(), PersistentDataType.DOUBLE)) {
            throw new IllegalArgumentException("Item meta must have fish length tag");
        }

        String typeName = tags.get(fishTypeKey(), PersistentDataType.STRING);
        FishType type = getPlugin().getFishTypeTable().getTypes().stream().filter(it -> it.name().equals(typeName)).findFirst().orElseThrow(() -> new IllegalStateException("Fish type doesn't exist"));
        Double length = tags.get(fishLengthKey(), PersistentDataType.DOUBLE);
        return new Fish(type, length == null ? 0 : length);
    }
}
