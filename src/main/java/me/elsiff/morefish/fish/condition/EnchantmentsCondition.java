package me.elsiff.morefish.fish.condition;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public class EnchantmentsCondition extends FishCondition<Map<Enchantment, Integer>> {

	public EnchantmentsCondition(Map<Enchantment, Integer> value) {
		super(value);
	}

    public boolean check(Item caught, Player fisher) {
        ItemStack fishingRod = fisher.getInventory().getItemInMainHand();
        if (fishingRod.getType() == Material.AIR) {
            return false;
        }

        return value.entrySet().stream().allMatch(e -> fishingRod.containsEnchantment(e.getKey()) && fishingRod.getEnchantmentLevel(e.getKey()) >= e.getValue());
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            return TagResolverUtil.fromMap(value, arguments, ctx, s -> {
                Key key = Key.key(s);
                return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key);
            }, i -> Tag.preProcessParsed(i + ""));
        }

        return null;
    }

    @Override
    public boolean has(String name) {
        return name.equals("enchantments");
    }
}
