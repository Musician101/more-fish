package io.musician101.morefish.spigot.item;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.config.ItemStackSerializer;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.configurate.ConfigurateException;

@SuppressWarnings("ConstantConditions")
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
    public ItemStack createItemStack(@Nonnull Fish fish, @Nonnull UUID catcher) {
        ItemStack itemStack;
        try {
            itemStack = new ItemStackSerializer().deserialize(ItemStack.class, fish.getType().getIcon());
        }
        catch (ConfigurateException e) {
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }

        if (!fish.getType().hasNotFishItemFormat()) {
            Map<String, Object> replacement = getFormatReplacementMap(fish, catcher);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(new SpigotTextFormat(getFishConfig().getDisplayNameFormat()).replace(replacement).output(catcher));
                List<String> lore = itemMeta.getLore();
                if (lore != null) {
                    itemMeta.setLore(Stream.concat(lore.stream(), new SpigotTextListFormat(getFishConfig().getLoreFormat()).replace(replacement).output(catcher).stream()).collect(Collectors.toList()));
                }

                write(itemMeta, fish);
                itemStack.setItemMeta(itemMeta);
            }
        }

        return itemStack;
    }

    @Nonnull
    public Fish fish(@Nonnull ItemStack itemStack) {
        return read(itemStack.getItemMeta());
    }

    private FishConfig getFishConfig() {
        return SpigotMoreFish.getInstance().getPluginConfig().getFishConfig();
    }

    private Map<String, Object> getFormatReplacementMap(Fish fish, UUID catcher) {
        return ImmutableMap.of("%player%", Bukkit.getPlayer(catcher).getName(), "%rarity%", fish.getType().getRarity().getName().toUpperCase(), "%rarity_color%", ChatColor.valueOf(fish.getType().getRarity().getColor().toUpperCase()), "%length%", fish.getLength(), "%fish%", fish.getType().getDisplayName());
    }

    public boolean isFish(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        return canRead(itemStack.getItemMeta());
    }

    @Nonnull
    private Fish read(@Nullable ItemMeta itemMeta) {
        if (itemMeta == null) {
            return new Fish(getFishConfig().getTypes().stream().findFirst().orElseThrow(() -> new IllegalStateException("Fish type doesn't exist")), 0);
        }
        PersistentDataContainer tags = itemMeta.getPersistentDataContainer();
        if (!tags.has(fishTypeKey, PersistentDataType.STRING)) {
            throw new IllegalArgumentException("Item meta must have fish type tag");
        }

        if (!tags.has(fishLengthKey, PersistentDataType.DOUBLE)) {
            throw new IllegalArgumentException("Item meta must have fish length tag");
        }

        String typeName = tags.get(fishTypeKey, PersistentDataType.STRING);
        FishType type = getFishConfig().getTypes().stream().filter(it -> it.getName().equals(typeName)).findFirst().orElseThrow(() -> new IllegalStateException("Fish type doesn't exist"));
        double length = tags.get(fishLengthKey, PersistentDataType.DOUBLE);
        return new Fish(type, length);
    }

    private void write(@Nonnull ItemMeta itemMeta, @Nonnull Fish fish) {
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        data.set(this.fishTypeKey, PersistentDataType.STRING.STRING, fish.getType().getName());
        data.set(this.fishLengthKey, PersistentDataType.DOUBLE, fish.getLength());
    }
}
