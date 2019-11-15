package me.elsiff.morefish.shop;

import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class FishShopSignListener implements Listener {

    private final FishShop fishShop;

    public FishShopSignListener(@Nonnull FishShop fishShop) {
        super();
        this.fishShop = fishShop;
    }

    private String getShopSignCreation() {
        return Config.INSTANCE.getStandard().getString("fish-shop.sign.creation");
    }

    private String getShopSignTitle() {
        return Config.INSTANCE.getStandard().getString("fish-shop.sign.title");
    }

    @EventHandler
    public final void onPlayerInteract(@Nonnull PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && block.getState() instanceof Sign) {
            Sign sign = (Sign) block;
            if (sign.getLines()[0].equals(getShopSignTitle())) {
                if (Config.INSTANCE.getStandard().getBoolean("fish-shop.enable")) {
                    fishShop.openGuiTo(event.getPlayer());
                    return;
                }

                event.getPlayer().sendMessage(Lang.INSTANCE.text("shop-disabled"));
            }
        }

    }

    @EventHandler
    public final void onSignChange(@Nonnull SignChangeEvent event) {
        String[] lines = event.getLines();
        if (lines[0].equals(getShopSignCreation()) || lines[0].equals(getShopSignTitle())) {
            Player player = event.getPlayer();
            if (event.getPlayer().hasPermission("morefish.admin")) {
                event.setLine(0, getShopSignTitle());
                player.sendMessage(Lang.INSTANCE.text("created-sign-shop"));
                return;
            }

            player.sendMessage(Lang.INSTANCE.text("no-permission"));
        }
    }
}
