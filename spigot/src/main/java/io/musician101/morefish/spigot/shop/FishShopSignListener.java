package io.musician101.morefish.spigot.shop;

import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import javax.annotation.Nonnull;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class FishShopSignListener implements Listener {

    private Config<SpigotTextFormat, SpigotTextListFormat, String> getConfig() {
        return SpigotMoreFish.getInstance().getPluginConfig();
    }

    private FishShopConfig getFishShopConfig() {
        return getConfig().getFishShopConfig();
    }

    private LangConfig<SpigotTextFormat, SpigotTextListFormat, String> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private String getShopSignTitle() {
        return getFishShopConfig().getSignTitle().getString();
    }

    @EventHandler
    public void onPlayerInteract(@Nonnull PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && block.getState() instanceof Sign sign) {
            if (sign.getLines()[0].equals(getShopSignTitle())) {
                if (getFishShopConfig().isEnabled()) {
                    new FishShopGui(event.getPlayer());
                    return;
                }

                event.getPlayer().sendMessage(getLangConfig().text("shop-disabled"));
            }
        }
    }

    @EventHandler
    public void onSignChange(@Nonnull SignChangeEvent event) {
        String[] lines = event.getLines();
        if (lines[0].equals(getFishShopConfig().getSignCreation().getString()) || lines[0].equals(getShopSignTitle())) {
            Player player = event.getPlayer();
            if (event.getPlayer().hasPermission("morefish.admin")) {
                event.setLine(0, getShopSignTitle());
                player.sendMessage(getLangConfig().text("created-sign-shop"));
                return;
            }

            player.sendMessage(getLangConfig().text("no-permission"));
        }
    }
}
