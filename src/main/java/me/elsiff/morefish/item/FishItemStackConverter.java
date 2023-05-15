package me.elsiff.morefish.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public interface FishItemStackConverter {

    @Nonnull
    static ItemStack createItemStack(@Nonnull Fish fish, @Nonnull Player catcher) {
        ItemStack itemStack = fish.type().icon().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!fish.type().hasNotFishItemFormat()) {
            Map<String, Object> replacement = getFormatReplacementMap(fish, catcher);
            Optional<JsonObject> jsonObject = getPlugin().getFishTypeTable().getItemFormat();
            GsonComponentSerializer gson = GsonComponentSerializer.gson();
            Component displayName = gson.deserialize(Lang.replace(jsonObject.map(json -> json.get("display-name").getAsString()).orElse("null"), replacement, catcher));
            itemMeta.displayName(displayName);
            List<Component> lore = Lang.replace(jsonObject.map(json -> json.getAsJsonArray("lore").asList().stream().map(JsonElement::toString).toList()).orElse(List.of()), replacement, catcher).stream().map(gson::deserialize).collect(Collectors.toList());
            List<Component> oldLore = itemMeta.lore();
            if (oldLore != null) {
                lore.addAll(oldLore.stream().map(component -> {
                    for (Entry<String, Object> entry : replacement.entrySet()) {
                        component = component.replaceText(builder -> {
                            builder.matchLiteral(entry.getKey());
                            builder.replacement(entry.getValue().toString());
                        });
                    }

                    return component;
                }).toList());
            }

            itemMeta.lore(Lang.replaceComponents(lore, replacement, catcher));
        }

        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        data.set(fishTypeKey(), PersistentDataType.STRING.STRING, fish.type().name());
        data.set(fishLengthKey(), PersistentDataType.DOUBLE, fish.length());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Nonnull
    static Fish fish(@Nonnull ItemStack itemStack) {
        return read(itemStack.getItemMeta());
    }

    private static NamespacedKey fishLengthKey() {
        return new NamespacedKey(getPlugin(), "fishLength");
    }

    private static NamespacedKey fishTypeKey() {
        return new NamespacedKey(getPlugin(), "fishType");
    }

    private static Map<String, Object> getFormatReplacementMap(Fish fish, Player catcher) {
        return Map.of("%player%", catcher.getName(), "%rarity%", fish.type().rarity().name().toUpperCase(), "%rarity_color%", fish.type().rarity().color().toString(), "%length%", fish.length(), "%fish%", fish.type().displayName());
    }

    private static MoreFish getPlugin() {
        return MoreFish.instance();
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

    @Nonnull
    private static Fish read(@Nonnull ItemMeta itemMeta) {
        PersistentDataContainer tags = itemMeta.getPersistentDataContainer();
        if (!tags.has(fishTypeKey(), PersistentDataType.STRING)) {
            throw new IllegalArgumentException("Item meta must have fish type tag");
        }

        if (!tags.has(fishLengthKey(), PersistentDataType.DOUBLE)) {
            throw new IllegalArgumentException("Item meta must have fish length tag");
        }

        String typeName = tags.get(fishTypeKey(), PersistentDataType.STRING);
        FishType type = MoreFish.instance().getFishTypeTable().getTypes().stream().filter(it -> it.name().equals(typeName)).findFirst().orElseThrow(() -> new IllegalStateException("Fish type doesn't exist"));
        Double length = tags.get(fishLengthKey(), PersistentDataType.DOUBLE);
        return new Fish(type, length == null ? 0 : length);
    }
}
