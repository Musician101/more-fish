package me.elsiff.morefish.shop;

import javax.annotation.Nonnull;
import me.elsiff.morefish.gui.AbstractGUI;
import me.elsiff.morefish.item.FishItemStackConverter;
import me.elsiff.morefish.util.OneTickScheduler;
import org.bukkit.entity.Player;

public class AbstractFishShopGUI extends AbstractGUI {

    @Nonnull
    protected final FishItemStackConverter converter;
    @Nonnull
    protected final FishShop shop;

    protected AbstractFishShopGUI(@Nonnull String title, @Nonnull FishShop shop, @Nonnull FishItemStackConverter converter, @Nonnull OneTickScheduler oneTickScheduler, @Nonnull Player user) {
        super(title, oneTickScheduler, user);
        this.shop = shop;
        this.converter = converter;
    }
}
