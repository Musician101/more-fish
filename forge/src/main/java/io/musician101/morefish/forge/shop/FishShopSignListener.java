package io.musician101.morefish.forge.shop;

import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.forge.ForgeMoreFish;
import javax.annotation.Nonnull;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//TODO replace with custom sign?
@Deprecated
public final class FishShopSignListener {

    private Config getConfig() {
        return ForgeMoreFish.getInstance().getPluginConfig();
    }

    private FishShopConfig getFishShopConfig() {
        return getConfig().getFishShopConfig();
    }

    private LangConfig<?, ?, ?> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private ITextComponent getShopSignTitle() {
        return getFishShopConfig().getSignTitle();
    }

    @SubscribeEvent
    public final void onPlayerInteract(@Nonnull RightClickBlock event) {
        TileEntity block = event.getWorld().getTileEntity(event.getPos());
        if (block instanceof SignTileEntity) {
            SignTileEntity sign = (SignTileEntity) block;
            if (sign.signText[0].equals(getShopSignTitle())) {
                if (getFishShopConfig().isEnabled()) {
                    //TODO send packet to open gui
                    //ForgeMoreFish.getInstance().getPluginConfig().getFishShopConfig().openGuiTo(event.getPlayer());
                    return;
                }

                event.getPlayer().sendMessage(getLangConfig().text("shop-disabled"));
            }
        }
    }

    /*@SubscribeEvent
    public final void onSignChange(@Nonnull SignChangeEvent event) {
        String[] lines = event.getLines();
        if (lines[0].equals(getFishShopConfig().getSignCreation()) || lines[0].equals(getShopSignTitle())) {
            ServerPlayerEntity player = event.getPlayer();
            if (event.getPlayer().hasPermission("morefish.admin")) {
                event.setLine(0, getShopSignTitle());
                player.sendMessage(getLangConfig().text("created-sign-shop"));
                return;
            }

            player.sendMessage(getLangConfig().text("no-permission"));
        }
    }*/
}
