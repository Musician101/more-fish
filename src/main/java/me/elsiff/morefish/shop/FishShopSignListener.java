package me.elsiff.morefish.shop;

import io.papermc.paper.event.player.PlayerOpenSignEvent;
import io.papermc.paper.event.player.PlayerOpenSignEvent.Cause;
import java.util.List;
import me.elsiff.morefish.configuration.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;

public record FishShopSignListener() implements Listener {

    private FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    private Component getShopSignCreation() {
        return Component.text(getConfig().getString("fish-shop.sign.creation", "[FishShop]"));
    }

    private Component getShopSignTitle() {
        return LegacyComponentSerializer.legacy('&').deserialize(getConfig().getString("fish-shop.sign.title", "&b&l[FishShop]"));
    }

    @EventHandler
    public void onOpenSign(@NotNull PlayerOpenSignEvent event) {
        if (event.getCause() != Cause.INTERACT) {
            return;
        }

        if (event.getSign().getSide(event.getSide()).line(0).equals(getShopSignTitle())) {
            if (getConfig().getBoolean("fish-shop.enable")) {
                new FishShopGui(event.getPlayer(), 1);
                return;
            }

            event.getPlayer().sendMessage(Lang.SHOP_DISABLED);
        }
    }

    @EventHandler
    public void onSignChange(@NotNull SignChangeEvent event) {
        List<Component> lines = event.lines();
        if (lines.get(0).equals(getShopSignCreation()) || lines.get(0).equals(getShopSignTitle())) {
            Player player = event.getPlayer();
            if (event.getPlayer().hasPermission("morefish.admin")) {
                event.line(0, getShopSignTitle());
                player.sendMessage(join(PREFIX, text("You've created the Fish Shop!")));
                return;
            }

            player.sendMessage(join(PREFIX, text("You don't have the permission.")));
        }
    }
}
