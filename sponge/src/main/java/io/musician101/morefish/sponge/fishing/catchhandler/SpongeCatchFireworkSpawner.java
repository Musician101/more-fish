package io.musician101.morefish.sponge.fishing.catchhandler;

import io.musician101.morefish.common.fishing.catchhandler.CatchFireworkSpawner;
import java.util.Collections;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.server.ServerWorld;

public final class SpongeCatchFireworkSpawner extends CatchFireworkSpawner {

    private final FireworkEffect effect = FireworkEffect.builder().shape(FireworkShapes.LARGE_BALL).color(Color.CYAN).fade(Color.BLUE).trail(true).flicker(true).build();

    @Override
    protected void spawnFirework(@Nonnull UUID uuid) {
        Sponge.server().player(uuid).ifPresent(player -> {
            ServerWorld world = player.world();
            Entity fw = world.createEntity(EntityTypes.FIREWORK_ROCKET, player.position());
            fw.offer(Keys.FIREWORK_EFFECTS, Collections.singletonList(effect));
            fw.offer(Keys.FIREWORK_FLIGHT_MODIFIER, Ticks.single());
            world.spawnEntity(fw);
        });
    }
}
