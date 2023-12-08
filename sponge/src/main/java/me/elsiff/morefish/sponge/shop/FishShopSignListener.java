package me.elsiff.morefish.sponge.shop;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.entity.Sign;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.entity.ChangeSignEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.tag.BlockTypeTags;
import org.spongepowered.configurate.ConfigurationNode;

import static me.elsiff.morefish.common.configuration.Lang.SHOP_DISABLED;
import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.PREFIX;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static net.kyori.adventure.text.Component.text;

public record FishShopSignListener() {

    private ConfigurationNode getConfig() {
        return getPlugin().getConfig();
    }

    private Component getShopSignCreation() {
        return Component.text(getConfig().node("fish-shop.sign.creation").getString("[FishShop]"));
    }

    private Component getShopSignTitle() {
        return LegacyComponentSerializer.legacy('&').deserialize(getConfig().node("fish-shop.sign.title").getString("&b&l[FishShop]"));
    }

    @Listener
    public void onOpenSign(@NotNull InteractBlockEvent.Secondary event, @First ServerPlayer player) {
        BlockSnapshot block = event.block();
        if (Sponge.server().registry(RegistryTypes.BLOCK_TYPE).taggedValues(BlockTypeTags.ALL_SIGNS).contains(block.state().type())) {
            if (block.location().flatMap(location -> location.blockEntity().filter(Sign.class::isInstance).map(Sign.class::cast)).flatMap(sign -> sign.get(Keys.SIGN_LINES)).filter(lines -> lines.get(0).equals(getShopSignTitle())).isPresent()) {
                if (getConfig().node("fish-shop.enable").getBoolean()) {
                    new FishShopGui(player, 1);
                    return;
                }

                player.sendMessage(SHOP_DISABLED);
            }
        }
    }

    @Listener
    public void onSignChange(@NotNull ChangeSignEvent event, @First ServerPlayer player) {
        List<Component> lines = event.originalText().get();
        if (lines.get(0).equals(getShopSignCreation()) || lines.get(0).equals(getShopSignTitle())) {
            if (player.hasPermission("morefish.admin")) {
                event.text().set(0, getShopSignTitle());
                player.sendMessage(join(PREFIX, text("You've created the Fish Shop!")));
                return;
            }

            player.sendMessage(join(PREFIX, text("You don't have the permission.")));
        }
    }
}
