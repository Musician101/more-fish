package me.elsiff.morefish.shop;

import io.musician101.musicianlibrary.java.minecraft.spigot.gui.chest.SpigotChestGUI;
import io.musician101.musicianlibrary.java.minecraft.spigot.gui.chest.SpigotIconBuilder;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AbstractFishShopGUI extends SpigotChestGUI<MoreFish> {

    @Nonnull
    protected final FishShop shop = MoreFish.instance().getFishShop();

    protected AbstractFishShopGUI(@Nonnull String title, @Nonnull Player user) {
        super(user, title, 54, MoreFish.instance(), false);
    }

    protected void glassPaneButton(int slot) {
        setButton(slot, SpigotIconBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE, " "));
    }
}
