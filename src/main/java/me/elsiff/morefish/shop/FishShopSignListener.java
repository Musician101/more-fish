package me.elsiff.morefish.shop;

import java.util.List;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public record FishShopSignListener() implements Listener {

    private FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    private MoreFish getPlugin() {
        return MoreFish.instance();
    }

    private Component getShopSignCreation() {
        return Component.text(getConfig().getString("fish-shop.sign.creation", "[FishShop]"));
    }

    private Component getShopSignTitle() {
        return LegacyComponentSerializer.legacy('&').deserialize(getConfig().getString("fish-shop.sign.title", "&b&l[FishShop]"));
    }

    @EventHandler
    public void onPlayerInteract(@Nonnull PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && block.getState() instanceof Sign sign) {
            if (sign.line(0).equals(getShopSignTitle())) {
                if (getConfig().getBoolean("fish-shop.enable")) {
                    new FishShopGui(event.getPlayer(), 1);
                    return;
                }

                event.getPlayer().sendMessage(Lang.SHOP_DISABLED);
            }
        }

    }

    @EventHandler
    public void onSignChange(@Nonnull SignChangeEvent event) {
        List<Component> lines = event.lines();
        if (lines.get(0).equals(getShopSignCreation()) || lines.get(0).equals(getShopSignTitle())) {
            Player player = event.getPlayer();
            if (event.getPlayer().hasPermission("morefish.admin")) {
                event.line(0, getShopSignTitle());
                player.sendMessage(Lang.CREATED_SIGN_SHOP);
                return;
            }

            player.sendMessage(Lang.NO_PERMISSION);
        }
    }
}
