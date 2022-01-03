package io.musician101.morefish.sponge.shop;

import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import java.io.BufferedWriter;
import java.io.StringWriter;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.entity.Sign;
import org.spongepowered.api.data.value.ListValue;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent.Secondary;
import org.spongepowered.api.event.block.entity.ChangeSignEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

public final class FishShopSignListener {

    private Config<SpongeTextFormat, SpongeTextListFormat, Component> getConfig() {
        return SpongeMoreFish.getInstance().getConfig();
    }

    private FishShopConfig getFishShopConfig() {
        return getConfig().getFishShopConfig();
    }

    private LangConfig<SpongeTextFormat, SpongeTextListFormat, Component> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private Component getShopSignTitle() {
        StringWriter sw = new StringWriter();
        try {
            GsonConfigurationLoader.builder().sink(() -> new BufferedWriter(sw)).build().save(getFishShopConfig().getSignTitle());
        }
        catch (ConfigurateException e) {
            e.printStackTrace();
            return Component.text().build();
        }

        return GsonComponentSerializer.gson().deserialize(sw.toString());
    }

    private Component getSignCreation() {
        StringWriter sw = new StringWriter();
        try {
            GsonConfigurationLoader.builder().sink(() -> new BufferedWriter(sw)).build().save(getFishShopConfig().getSignCreation());
        }
        catch (ConfigurateException e) {
            e.printStackTrace();
            return Component.text().build();
        }

        return GsonComponentSerializer.gson().deserialize(sw.toString());
    }

    @Listener
    public final void onPlayerInteract(@Nonnull Secondary event, @First ServerPlayer player, @Getter("block") BlockSnapshot block) {
        block.location().flatMap(ServerLocation::blockEntity).filter(Sign.class::isInstance).map(Sign.class::cast).map(Sign::lines).filter(l -> !l.isEmpty() && getShopSignTitle().equals(l.get(0))).ifPresent(l -> {
            if (getFishShopConfig().isEnabled()) {
                new FishShopGui(player);
                return;
            }

            player.sendMessage(getLangConfig().text("shop-disabled"));
        });
    }

    @Listener
    public final void onSignChange(@Nonnull ChangeSignEvent event, @First ServerPlayer player, @Getter("sign") Sign sign, @Getter("text") ListValue.Mutable<Component> lines) {
        if (lines.get(0).equals(getSignCreation()) || lines.get(0).equals(getShopSignTitle())) {
            if (player.hasPermission("morefish.admin")) {
                lines.set(0, getShopSignTitle());
                sign.offer(lines);
                player.sendMessage(getLangConfig().text("created-sign-shop"));
                return;
            }

            player.sendMessage(getLangConfig().text("no-permission"));
        }
    }
}
