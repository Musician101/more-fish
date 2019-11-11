package me.elsiff.morefish.configuration.loader;

import java.util.UUID;
import javax.annotation.Nonnull;
import me.elsiff.morefish.hooker.PluginHooker.Companion;
import me.elsiff.morefish.hooker.ProtocolLibHooker;
import me.elsiff.morefish.util.NamespacedKeyUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class CustomItemStackLoader implements CustomLoader<ItemStack> {

    private final EnchantmentMapLoader enchantmentMapLoader;
    private ProtocolLibHooker protocolLib;

    public CustomItemStackLoader(@Nonnull EnchantmentMapLoader enchantmentMapLoader) {
        this.enchantmentMapLoader = enchantmentMapLoader;
    }

    @Nonnull
    public ItemStack loadFrom(@Nonnull ConfigurationSection section, @Nonnull String path) {
        ConfigurationSection cs = section.getConfigurationSection(path);
        Material material = NamespacedKeyUtils.material(cs.getString("id"));
        int amount = cs.getInt("amount", 1);
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(cs.getStringList("lore"));
        enchantmentMapLoader.loadFrom(cs, "enchantments").forEach((enchantment, level) -> itemMeta.addEnchant(enchantment, level, true));
        itemMeta.setUnbreakable(cs.getBoolean("unbreakable", false));
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(cs.getInt("durability", 0));
        }

        if (cs.contains("skull-uuid") && itemMeta instanceof SkullMeta) {
            ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("skull-uuid")));
        }

        itemStack.setItemMeta(itemMeta);
        if (cs.contains("skull-texture")) {
            Companion.checkHooked(protocolLib);
            itemStack = protocolLib.skullNbtHandler.writeTexture(itemStack, cs.getString("skull-texture"));
        }

        return itemStack;
    }

    public final void setProtocolLib(@Nonnull ProtocolLibHooker protocolLibHooker) {
        this.protocolLib = protocolLibHooker;
    }
}
