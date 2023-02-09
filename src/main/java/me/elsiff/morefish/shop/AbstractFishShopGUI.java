package me.elsiff.morefish.shop;

import io.musician101.musigui.spigot.chest.SpigotChestGUI;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static io.musician101.musigui.spigot.chest.SpigotIconUtil.customName;

public class AbstractFishShopGUI extends SpigotChestGUI<MoreFish> {

    @Nonnull
    protected final FishShop shop = MoreFish.instance().getFishShop();

    protected AbstractFishShopGUI(@Nonnull String title, @Nonnull Player user) {
        super(user, title, 54, MoreFish.instance(), false);
    }

    protected void glassPaneButton(int slot) {
        setButton(slot, customName(new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE), " "));
    }
}
