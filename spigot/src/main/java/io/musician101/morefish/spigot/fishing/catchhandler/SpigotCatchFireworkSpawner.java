package io.musician101.morefish.spigot.fishing.catchhandler;

import io.musician101.morefish.common.fishing.catchhandler.CatchFireworkSpawner;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public final class SpigotCatchFireworkSpawner extends CatchFireworkSpawner {

    private static final FireworkEffect EFFECT = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.AQUA).withFade(Color.BLUE).trail(true).flicker(true).build();

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void spawnFirework(@Nonnull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        player.getWorld().spawn(player.getLocation(), Firework.class, firework -> {
            ItemStack itemStack = new ItemStack(Material.FIREWORK_ROCKET);
            FireworkMeta fireworkMeta = (FireworkMeta) itemStack.getItemMeta();
            fireworkMeta.addEffect(EFFECT);
            fireworkMeta.setPower(1);
            firework.setFireworkMeta(fireworkMeta);
        });
    }
}
