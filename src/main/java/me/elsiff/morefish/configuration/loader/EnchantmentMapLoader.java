package me.elsiff.morefish.configuration.loader;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

public final class EnchantmentMapLoader implements CustomLoader {

    private static final String DELIMITER = "|";

    @Nonnull
    public Map<Enchantment, Integer> loadFrom(@Nonnull ConfigurationSection section, @Nonnull String path) {
        Map<Enchantment, Integer> map = new HashMap<>();
        if (section.contains(path)) {
            for (String string : section.getStringList(path)) {
                String[] tokens = string.split(DELIMITER);
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(tokens[0]));
                int level = Integer.parseInt(tokens[1]);
                map.put(enchantment, level);
            }
        }

        return map;
    }
}
