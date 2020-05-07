package io.musician101.morefish.spigot.item;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchHandler;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class FishItemStackConverter {

    private final NamespacedKey fishLengthKey = new NamespacedKey(SpigotMoreFish.getInstance(), "fishLength");
    private final NamespacedKey fishTypeKey = new NamespacedKey(SpigotMoreFish.getInstance(), "fishType");

    private boolean canRead(@Nullable ItemMeta itemMeta) {
        if (itemMeta == null) {
            return false;
        }

        PersistentDataContainer tags = itemMeta.getPersistentDataContainer();
        return tags.has(fishTypeKey, PersistentDataType.STRING) && tags.has(fishLengthKey, PersistentDataType.DOUBLE);
    }

    @Nonnull
    public final ItemStack createItemStack(@Nonnull Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish, @Nonnull Player catcher) {
        ItemStack itemStack = fish.getType().getIcon().clone();
        if (!fish.getType().hasNotFishItemFormat()) {
            Map<String, Object> replacement = getFormatReplacementMap(fish, catcher);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(new SpigotTextFormat(getFishConfig().getDisplayNameFormat()).replace(replacement).output(catcher));
            itemMeta.setLore(Stream.concat(itemMeta.getLore().stream(), new SpigotTextListFormat(getFishConfig().getLoreFormat()).replace(replacement).output(catcher).stream()).collect(Collectors.toList()));
            write(itemMeta, fish);
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    @Nonnull
    public final Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish(@Nonnull ItemStack itemStack) {
        return read(itemStack.getItemMeta());
    }

    private FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>> getFishConfig() {
        return SpigotMoreFish.getInstance().getPluginConfig().getFishConfig();
    }

    private Map<String, Object> getFormatReplacementMap(Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish, Player catcher) {
        return ImmutableMap.of("%player%", catcher.getName(), "%rarity%", fish.getType().getRarity().getName().toUpperCase(), "%rarity_color%", fish.getType().getRarity().getColor().toString(), "%length%", fish.getLength(), "%fish%", fish.getType().getDisplayName());
    }

    public final boolean isFish(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        return canRead(itemStack.getItemMeta());
    }

    @Nonnull
    private Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> read(@Nonnull ItemMeta itemMeta) {
        PersistentDataContainer tags = itemMeta.getPersistentDataContainer();
        if (!tags.has(fishTypeKey, PersistentDataType.STRING)) {
            throw new IllegalArgumentException("Item meta must have fish type tag");
        }

        if (!tags.has(fishLengthKey, PersistentDataType.DOUBLE)) {
            throw new IllegalArgumentException("Item meta must have fish length tag");
        }

        String typeName = tags.get(fishTypeKey, PersistentDataType.STRING);
        FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> type = getFishConfig().getTypes().stream().filter(it -> it.getName().equals(typeName)).findFirst().orElseThrow(() -> new IllegalStateException("Fish type doesn't exist"));
        Double length = tags.get(fishLengthKey, PersistentDataType.DOUBLE);
        return new Fish<>(type, length);
    }

    private void write(@Nonnull ItemMeta itemMeta, @Nonnull Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish) {
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        data.set(this.fishTypeKey, PersistentDataType.STRING.STRING, fish.getType().getName());
        data.set(this.fishLengthKey, PersistentDataType.DOUBLE, fish.getLength());
    }
}
