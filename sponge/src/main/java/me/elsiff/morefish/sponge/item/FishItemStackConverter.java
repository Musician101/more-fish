package me.elsiff.morefish.sponge.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import me.elsiff.morefish.sponge.fishing.SpongeFish;
import me.elsiff.morefish.sponge.fishing.SpongeFishType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.ItemStack;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.lang;

public interface FishItemStackConverter {

    @NotNull
    static ItemStack createItemStack(@NotNull SpongeFish fish, @NotNull ServerPlayer catcher) {
        return createItemStack(fish, fish.length(), catcher);
    }

    @NotNull
    static ItemStack createItemStack(@NotNull SpongeFish fish, double length, @NotNull ServerPlayer catcher) {
        ItemStack itemStack = fish.type().icon().copy();
        if (!fish.type().hasNotFishItemFormat()) {
            Map<String, Object> replacement = getFormatReplacementMap(fish, length, catcher);
            GsonComponentSerializer gson = GsonComponentSerializer.gson();
            itemStack.offer(Keys.DISPLAY_NAME, lang().replace(gson.deserialize(getFormatConfig().map(cs -> cs.get("display-name").toString()).orElse("null")), replacement, catcher.uniqueId()));
            List<Component> lore = lang().replace(getFormatConfig().map(json -> json.getAsJsonArray("lore").asList().stream().map(JsonElement::toString).map(gson::deserialize).toList()).orElse(new ArrayList<>()), replacement, catcher.uniqueId());
            Optional<List<Component>> oldLore = itemStack.get(Keys.LORE);
            oldLore.ifPresent(components -> lore.addAll(components.stream().map(component -> {
                for (Entry<String, Object> entry : replacement.entrySet()) {
                    component = component.replaceText(builder -> {
                        builder.matchLiteral(entry.getKey());
                        builder.replacement(entry.getValue().toString());
                    });
                }

                return component;
            }).toList()));

            itemStack.offer(Keys.LORE, lang().replace(lore, replacement, catcher.uniqueId()));
        }

        itemStack.offer(fishLengthKey(), fish.length());
        itemStack.offer(fishTypeKey(), fish.type());
        return itemStack;
    }

    @NotNull
    static SpongeFish fish(@NotNull ItemStack itemStack) {
        SpongeFishType type = itemStack.get(fishTypeKey()).orElseThrow(() -> new IllegalArgumentException("Item meta must have valid fish type tag"));
        double length = itemStack.get(fishLengthKey()).orElseThrow(() -> new IllegalArgumentException("Item meta must have valid fish length tag"));
        return new SpongeFish(type, length);
    }

    static Key<Value<Double>> fishLengthKey() {
        return Key.from(fishLengthResourceKey(), Double.class);
    }

    static DataQuery fishLengthQuery() {
        return DataQuery.of(fishTypeResourceKey().asString());
    }

    static ResourceKey fishLengthResourceKey() {
        return ResourceKey.of(getPlugin().getPluginContainer(), "fishLength");
    }

    static Key<Value<SpongeFishType>> fishTypeKey() {
        return Key.from(fishLengthResourceKey(), SpongeFishType.class);
    }

    static DataQuery fishTypeQuery() {
        return DataQuery.of(fishTypeResourceKey().asString());
    }

    static ResourceKey fishTypeResourceKey() {
        return ResourceKey.of(getPlugin().getPluginContainer(), "fishType");
    }

    private static Optional<JsonObject> getFormatConfig() {
        return getPlugin().getFishTypeTable().getItemFormat();
    }

    private static Map<String, Object> getFormatReplacementMap(SpongeFish fish, double length, ServerPlayer catcher) {
        return Map.of("%player%", catcher.name(), "%rarity%", fish.type().rarity().name().toUpperCase(), "%rarity_color%", fish.type().rarity().color().toString(), "%length%", length, "%fish%", fish.type().displayName());
    }

    static boolean isFish(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        return itemStack.get(fishTypeKey()).isPresent() && itemStack.get(fishLengthKey()).isPresent();
    }
}
