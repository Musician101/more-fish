package me.elsiff.morefish.fishing.catchhandler;

import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.Fish;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public final class CatchFireworkSpawner implements CatchHandler {

    private final FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.AQUA).withFade(Color.BLUE).withTrail().withFlicker().build();

    public void handle(@Nonnull Player catcher, @Nonnull Fish fish) {
        if (fish.type().hasCatchFirework()) {
            catcher.getWorld().spawn(catcher.getLocation(), Firework.class, firework -> {
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(effect);
                meta.setPower(1);
                firework.setFireworkMeta(meta);
            });
        }
    }
}
