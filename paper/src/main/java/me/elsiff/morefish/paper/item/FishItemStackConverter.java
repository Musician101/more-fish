package me.elsiff.morefish.paper.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import me.elsiff.morefish.paper.configuration.PaperLang;
import me.elsiff.morefish.paper.fishing.PaperFish;
import me.elsiff.morefish.paper.fishing.PaperFishType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;

public interface FishItemStackConverter {

    @NotNull
    static ItemStack createItemStack(@NotNull PaperFish fish, @NotNull Player catcher) {
        return createItemStack(fish, fish.length(), catcher);
    }

    @NotNull
    static ItemStack createItemStack(@NotNull PaperFish fish, double length, @NotNull Player catcher) {
        ItemStack itemStack = fish.type().icon().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!fish.type().hasNotFishItemFormat()) {
            Map<String, Object> replacement = getFormatReplacementMap(fish, length, catcher);
            GsonComponentSerializer gson = GsonComponentSerializer.gson();
            itemMeta.displayName(PaperLang.lang().replace(gson.deserialize(getFormatConfig().map(cs -> cs.get("display-name").toString()).orElse("null")), replacement, catcher.getUniqueId()));
            List<Component> lore = PaperLang.lang().replace(getFormatConfig().map(json -> json.getAsJsonArray("lore").asList().stream().map(JsonElement::toString).map(gson::deserialize).toList()).orElse(new ArrayList<>()), replacement, catcher.getUniqueId());
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

            itemMeta.lore(PaperLang.lang().replace(lore, replacement, catcher.getUniqueId()));
        }

        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        data.set(fishTypeKey(), PersistentDataType.STRING, fish.type().name());
        data.set(fishLengthKey(), PersistentDataType.DOUBLE, fish.length());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @NotNull
    static PaperFish fish(@NotNull ItemStack itemStack) {
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

    private static Map<String, Object> getFormatReplacementMap(PaperFish fish, double length, Player catcher) {
        return Map.of("%player%", catcher.getName(), "%rarity%", fish.type().rarity().name().toUpperCase(), "%rarity_color%", fish.type().rarity().color().toString(), "%length%", length, "%fish%", fish.type().displayName());
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
    private static PaperFish read(@NotNull ItemMeta itemMeta) {
        PersistentDataContainer tags = itemMeta.getPersistentDataContainer();
        if (!tags.has(fishTypeKey(), PersistentDataType.STRING)) {
            throw new IllegalArgumentException("Item meta must have fish type tag");
        }

        if (!tags.has(fishLengthKey(), PersistentDataType.DOUBLE)) {
            throw new IllegalArgumentException("Item meta must have fish length tag");
        }

        String typeName = tags.get(fishTypeKey(), PersistentDataType.STRING);
        PaperFishType type = getPlugin().getFishTypeTable().getTypes().stream().filter(it -> it.name().equals(typeName)).findFirst().orElseThrow(() -> new IllegalStateException("PaperFish type doesn't exist"));
        Double length = tags.get(fishLengthKey(), PersistentDataType.DOUBLE);
        return new PaperFish(type, length == null ? 0 : length);
    }
}
