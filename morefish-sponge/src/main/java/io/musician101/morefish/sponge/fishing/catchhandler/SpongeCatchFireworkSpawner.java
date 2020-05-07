package io.musician101.morefish.sponge.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import java.util.Collections;
import javax.annotation.Nonnull;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.World;

public final class SpongeCatchFireworkSpawner implements SpongeCatchHandler {

    private final FireworkEffect effect = FireworkEffect.builder().shape(FireworkShapes.LARGE_BALL).color(Color.CYAN).fade(Color.BLUE).trail(true).flicker(true).build();

    public void handle(@Nonnull Player catcher, @Nonnull Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish) {
        if (fish.getType().hasCatchFirework()) {
            World world = catcher.getWorld();
            Entity fw = world.createEntity(EntityTypes.FIREWORK, catcher.getPosition());
            fw.offer(Keys.FIREWORK_EFFECTS, Collections.singletonList(effect));
            fw.offer(Keys.FIREWORK_FLIGHT_MODIFIER, 1);
            world.spawnEntity(fw);
        }
    }
}
