package io.musician101.morefish.sponge.item;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.config.ItemStackSerializer;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.configurate.serialize.SerializationException;

public final class FishItemStackConverter {

    public static final Key<Value<Double>> FISH_LENGTH = Key.of(SpongeMoreFish.getInstance().getPluginContainer(), "length", TypeTokens.DOUBLE_VALUE_TOKEN);
    public static final Key<Value<String>> FISH_TYPE = Key.of(SpongeMoreFish.getInstance().getPluginContainer(), "type", TypeTokens.STRING_VALUE_TOKEN);

    @Nonnull
    public final ItemStack createItemStack(@Nonnull Fish fish, @Nonnull UUID catcher) {
        try {
            ItemStack itemStack = new ItemStackSerializer().deserialize(ItemStack.class, fish.getType().getIcon());
            if (!fish.getType().hasNotFishItemFormat()) {
                Map<String, Object> replacement = getFormatReplacementMap(fish, catcher);
                itemStack.offer(Keys.DISPLAY_NAME, new SpongeTextFormat(getFishConfig().getDisplayNameFormat()).replace(replacement).output(catcher));
                itemStack.offer(Keys.LORE, Stream.concat(itemStack.get(Keys.LORE).orElse(Collections.emptyList()).stream(), new SpongeTextListFormat(getFishConfig().getLoreFormat()).replace(replacement).output(catcher).stream()).collect(Collectors.toList()));
                itemStack.offer(FISH_TYPE, fish.getType().getName());
                itemStack.offer(FISH_LENGTH, fish.getLength());
            }

            return itemStack;
        }
        catch (SerializationException e) {
            throw new IllegalArgumentException("Failed to parse item", e);
        }
    }

    @Nonnull
    public final Fish fish(@Nonnull ItemStack itemStack) {
        return itemStack.get(FISH_TYPE).flatMap(fishType -> itemStack.get(FISH_LENGTH).map(fishLength -> new Fish(getFishConfig().getTypes().stream().filter(f -> f.getName().equals(fishType)).findFirst().orElseThrow(() -> new IllegalArgumentException(fishType + " does not exist")), fishLength))).orElseThrow(() -> new IllegalArgumentException("Fish Data missing from item"));
    }

    private FishConfig getFishConfig() {
        return SpongeMoreFish.getInstance().getConfig().getFishConfig();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private Map<String, Object> getFormatReplacementMap(Fish fish, UUID catcher) {
        return ImmutableMap.of("%player%", Sponge.server().player(catcher).get().name(), "%rarity%", fish.getType().getRarity().getName().toUpperCase(), "%rarity_color%", fish.getType().getRarity().getColor(), "%length%", fish.getLength(), "%fish%", fish.getType().getDisplayName());
    }

    public final boolean isFish(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return false;
        }

        return itemStack.get(FISH_LENGTH).isPresent() && itemStack.get(FISH_TYPE).isPresent();
    }
}
