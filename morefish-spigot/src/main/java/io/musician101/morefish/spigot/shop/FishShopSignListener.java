package io.musician101.morefish.spigot.shop;

import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchHandler;
import io.musician101.morefish.spigot.fishing.competition.SpigotPrize;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class FishShopSignListener implements Listener {

    private Config<FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>>, LangConfig<SpigotTextFormat, SpigotTextListFormat, String>, MessagesConfig<SpigotPlayerAnnouncement, BarColor>, FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String>, SpigotPrize> getConfig() {
        return SpigotMoreFish.getInstance().getPluginConfig();
    }

    private FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String> getFishShopConfig() {
        return getConfig().getFishShopConfig();
    }

    private LangConfig<SpigotTextFormat, SpigotTextListFormat, String> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private String getShopSignTitle() {
        return getFishShopConfig().getSignTitle();
    }

    @EventHandler
    public final void onPlayerInteract(@Nonnull PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            if (sign.getLines()[0].equals(getShopSignTitle())) {
                if (getFishShopConfig().isEnabled()) {
                    SpigotMoreFish.getInstance().getPluginConfig().getFishShopConfig().openGuiTo(event.getPlayer());
                    return;
                }

                event.getPlayer().sendMessage(getLangConfig().text("shop-disabled"));
            }
        }
    }

    @EventHandler
    public final void onSignChange(@Nonnull SignChangeEvent event) {
        String[] lines = event.getLines();
        if (lines[0].equals(getFishShopConfig().getSignCreation()) || lines[0].equals(getShopSignTitle())) {
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
