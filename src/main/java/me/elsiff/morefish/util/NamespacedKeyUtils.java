package me.elsiff.morefish.util;

import javax.annotation.Nonnull;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffectType;

public final class NamespacedKeyUtils {

    private NamespacedKeyUtils() {

    }

    @Nonnull
    public static NamespacedKey fromMinecraft(@Nonnull String id) {
        if (id.contains(":")) {
            return fromMinecraft(id.split(":")[1]);
        }

        return NamespacedKey.minecraft(id);
    }

    @Nonnull
    public static Material material(@Nonnull String id) {
        return material(fromMinecraft(id));
    }

    @Nonnull
    public static Material material(@Nonnull NamespacedKey namespacedKey) {
        Material material = Material.matchMaterial(namespacedKey.getKey());
        if (material != null) {
            return material;
        }
        else {
            throw new IllegalStateException("There's no material whose id is '" + namespacedKey + '\'');
        }
    }

    @Nonnull
    public static PotionEffectType potionEffectType(@Nonnull String id) {
        return potionEffectType(fromMinecraft(id));
    }

    @Nonnull
    public static PotionEffectType potionEffectType(@Nonnull NamespacedKey namespacedKey) {
        PotionEffectType potionEffectType = PotionEffectType.getByName(namespacedKey.getKey());
        if (potionEffectType != null) {
            return potionEffectType;
        }
        else {
            throw new IllegalStateException("There's no potion effect type whose id is '" + namespacedKey + '\'');
        }
    }
}
