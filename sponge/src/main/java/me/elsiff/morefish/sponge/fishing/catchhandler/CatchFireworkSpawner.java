package me.elsiff.morefish.sponge.fishing.catchhandler;

import java.util.List;
import me.elsiff.morefish.common.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.sponge.fishing.SpongeFish;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.projectile.explosive.FireworkRocket;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.util.Color;

public final class CatchFireworkSpawner implements CatchHandler<SpongeFish, ServerPlayer> {

    private final FireworkEffect effect = FireworkEffect.builder().shape(FireworkShapes.LARGE_BALL).color(Color.CYAN).fade(Color.BLUE).trail(true).flicker(true).build();

    public void handle(@NotNull ServerPlayer catcher, @NotNull SpongeFish fish) {
        if (fish.type().hasCatchFirework()) {
            FireworkRocket fwr = catcher.world().createEntity(EntityTypes.FIREWORK_ROCKET, catcher.position());
            fwr.offer(Keys.FIREWORK_EFFECTS, List.of(effect));
            catcher.world().spawnEntity(fwr);
        }
    }
}
