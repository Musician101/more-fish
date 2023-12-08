package me.elsiff.morefish.paper.fishing.catchhandler;

import me.elsiff.morefish.common.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.paper.fishing.PaperFish;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

public final class CatchFireworkSpawner implements CatchHandler<PaperFish, Player> {

    private final FireworkEffect effect = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.AQUA).withFade(Color.BLUE).withTrail().withFlicker().build();

    public void handle(@NotNull Player catcher, @NotNull PaperFish fish) {
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
