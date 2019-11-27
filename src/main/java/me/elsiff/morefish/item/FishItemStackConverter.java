package me.elsiff.morefish.item;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.format.TextFormat;
import me.elsiff.morefish.configuration.format.TextListFormat;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishTypeTable;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public final class FishItemStackConverter {

    private final FishItemTagReader fishReader;
    private final FishItemTagWriter fishWriter;

    public FishItemStackConverter(@Nonnull Plugin plugin, @Nonnull FishTypeTable fishTypeTable) {
        super();
        NamespacedKey fishTypeKey = new NamespacedKey(plugin, "fishType");
        NamespacedKey fishLengthKey = new NamespacedKey(plugin, "fishLength");
        this.fishReader = new FishItemTagReader(fishTypeTable, fishTypeKey, fishLengthKey);
        this.fishWriter = new FishItemTagWriter(fishTypeKey, fishLengthKey);
    }

    @Nonnull
    public final ItemStack createItemStack(@Nonnull Fish fish, @Nonnull Player catcher) {
        ItemStack itemStack = fish.getType().getIcon().clone();
        if (!fish.getType().getHasNotFishItemFormat()) {
            Map<String, Object> replacement = getFormatReplacementMap(fish, catcher);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(new TextFormat(getFormatConfig().getString("display-name")).replace(replacement).output(catcher));
            List<String> lore = getFormatConfig().getStringList("lore");
            if (itemMeta.getLore() != null) {
                lore.addAll(itemMeta.getLore());
            }
            itemMeta.setLore(new TextListFormat(lore).replace(replacement).output(catcher));
            fishWriter.write(itemMeta, fish);
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    @Nonnull
    public final Fish fish(@Nonnull ItemStack itemStack) {
        return fishReader.read(itemStack.getItemMeta());
    }

    private ConfigurationSection getFormatConfig() {
        return Config.INSTANCE.getFish().getConfigurationSection("item-format");
    }

    private Map<String, Object> getFormatReplacementMap(Fish fish, Player catcher) {
        return ImmutableMap.of("%player%", catcher.getName(), "%rarity%", fish.getType().getRarity().getName().toUpperCase(), "%rarity_color%", fish.getType().getRarity().getColor().toString(), "%length%", fish.getLength(), "%fish%", fish.getType().getDisplayName());
    }

    public final boolean isFish(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        return fishReader.canRead(itemStack.getItemMeta());
    }
}
