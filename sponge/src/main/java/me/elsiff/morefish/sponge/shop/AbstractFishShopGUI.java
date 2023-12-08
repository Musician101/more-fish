package me.elsiff.morefish.sponge.shop;

import io.musician101.musigui.sponge.chest.SpongeChestGUI;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import static io.musician101.musigui.sponge.chest.SpongeIconUtil.customName;
import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;

public class AbstractFishShopGUI extends SpongeChestGUI {

    @NotNull protected final SpongeFishShop shop = getPlugin().getFishShop();

    protected AbstractFishShopGUI(@NotNull Component title, @NotNull ServerPlayer user) {
        super(user, title, 54, getPlugin().getPluginContainer(), false, false);
    }

    protected void glassPaneButton(int slot) {
        setButton(slot, customName(ItemStack.of(ItemTypes.LIGHT_BLUE_STAINED_GLASS_PANE), text(" ")));
    }
}
