package io.musician101.morefish.spigot.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public final class SpigotCatchFireworkSpawner implements SpigotCatchHandler {

    private final FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.AQUA).withFade(Color.BLUE).withTrail().withFlicker().build();

    public void handle(@Nonnull Player catcher, @Nonnull Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish) {
        if (fish.getType().hasCatchFirework()) {
            catcher.getWorld().spawn(catcher.getLocation(), Firework.class, firework -> {
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(effect);
                meta.setPower(1);
                firework.setFireworkMeta(meta);
            });
        }
    }
}
