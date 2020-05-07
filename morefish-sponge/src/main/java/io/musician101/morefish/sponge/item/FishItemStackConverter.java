package io.musician101.morefish.sponge.item;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import io.musician101.morefish.sponge.data.MoreFishData;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchHandler;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColor;

public final class FishItemStackConverter {

    @Nonnull
    public final ItemStack createItemStack(@Nonnull Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish, @Nonnull Player catcher) {
        ItemStack itemStack = fish.getType().getIcon().copy();
        if (!fish.getType().hasNotFishItemFormat()) {
            Map<String, Object> replacement = getFormatReplacementMap(fish, catcher);
            itemStack.offer(Keys.DISPLAY_NAME, new SpongeTextFormat(getFishConfig().getDisplayNameFormat()).replace(replacement).output(catcher));
            itemStack.offer(Keys.ITEM_LORE, Stream.concat(itemStack.get(Keys.ITEM_LORE).orElse(Collections.emptyList()).stream(), new SpongeTextListFormat(getFishConfig().getLoreFormat()).replace(replacement).output(catcher).stream()).collect(Collectors.toList()));
            itemStack.offer(new MoreFishData(fish.getType(), fish.getLength()));
        }

        return itemStack;
    }

    @Nonnull
    public final Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish(@Nonnull ItemStack itemStack) {
        return itemStack.get(MoreFishData.class).map(moreFishData -> new Fish<>(moreFishData.getFishType().get(), moreFishData.getLength().get())).orElseThrow(() -> new IllegalArgumentException("MoreFishData missing from item"));
    }

    private FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>> getFishConfig() {
        return SpongeMoreFish.getInstance().getConfig().getFishConfig();
    }

    private Map<String, Object> getFormatReplacementMap(Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish, Player catcher) {
        return ImmutableMap.of("%player%", catcher.getName(), "%rarity%", fish.getType().getRarity().getName().toUpperCase(), "%rarity_color%", fish.getType().getRarity().getColor().toString(), "%length%", fish.getLength(), "%fish%", fish.getType().getDisplayName());
    }

    public final boolean isFish(@Nullable ItemStack itemStack) {
        if (itemStack == null && itemStack.isEmpty()) {
            return false;
        }

        return itemStack.get(MoreFishData.class).isPresent();
    }
}
