package me.elsiff.morefish.util;

import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ItemUtil {

    @Nonnull
    public static final ItemStack EMPTY = new ItemStack(Material.AIR);

    private ItemUtil() {

    }

    public static ItemStack named(@Nonnull Material material, @Nonnull String name) {
        return setDisplayName(new ItemStack(material), name);
    }

    public static ItemStack setDisplayName(@Nonnull ItemStack itemStack, @Nonnull String name) {
        if (itemStack.getType() == Material.AIR) {
            return itemStack;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack addHiddenGlow(@Nonnull ItemStack itemStack) {
        if (itemStack.getType() == Material.AIR) {
            return itemStack;
        }

        if (itemStack.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            meta.addStoredEnchant(Enchantment.DURABILITY, 1, true);
            itemStack.setItemMeta(meta);
        }
        else {
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }

        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack setLore(@Nonnull ItemStack itemStack, @Nonnull List<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack setPotion(@Nonnull ItemStack itemStack, @Nonnull PotionType potionType) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) itemMeta;
            potionMeta.setBasePotionData(new PotionData(potionType));
            potionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            itemStack.setItemMeta(potionMeta);
        }

        return itemStack;
    }

    public static ItemStack setPotionColor(@Nonnull ItemStack itemStack, @Nonnull Color color) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof PotionMeta) {
            ItemStack editItemStack = setPotion(itemStack, PotionType.AWKWARD).clone();
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            meta.setColor(color);
            editItemStack.setItemMeta(meta);
            return editItemStack;
        }

        return itemStack;
    }
}
