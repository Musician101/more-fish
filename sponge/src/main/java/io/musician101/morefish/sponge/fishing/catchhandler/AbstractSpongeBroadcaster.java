package io.musician101.morefish.sponge.fishing.catchhandler;

import io.musician101.morefish.common.fishing.catchhandler.CatchBroadcaster;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;

public abstract class AbstractSpongeBroadcaster extends CatchBroadcaster<SpongeTextFormat, Component> {

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static ServerPlayer getPlayer(UUID uuid) {
        return Sponge.server().player(uuid).get();
    }

    @Nonnull
    @Override
    protected final String getCatcherName(@Nonnull UUID uuid) {
        return getPlayer(uuid).name();
    }

    @Override
    protected final boolean hasFishingRod(@Nonnull UUID uuid) {
        return getPlayer(uuid).itemInHand(HandTypes.MAIN_HAND).type() != ItemTypes.FISHING_ROD.get();
    }

    @Override
    protected final boolean onlyAnnounceFishingRod() {
        return SpongeMoreFish.getInstance().getConfig().getMessagesConfig().onlyAnnounceFishingRod();
    }

    @Override
    protected final void sendMessage(@Nonnull UUID uuid, @Nonnull Component msg) {
        getPlayer(uuid).sendMessage(msg);
    }
}
