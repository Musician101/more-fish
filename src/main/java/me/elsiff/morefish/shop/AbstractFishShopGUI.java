package me.elsiff.morefish.shop;

import io.musician101.musigui.paper.chest.PaperChestGUI;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static io.musician101.musigui.paper.chest.PaperIconUtil.customName;
import static net.kyori.adventure.text.Component.text;

public class AbstractFishShopGUI extends PaperChestGUI<MoreFish> {

    @Nonnull
    protected final FishShop shop = MoreFish.instance().getFishShop();

    protected AbstractFishShopGUI(@Nonnull Component title, @Nonnull Player user) {
        super(user, title, 54, MoreFish.instance(), false);
    }

    protected void glassPaneButton(int slot) {
        setButton(slot, customName(new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE), text(" ")));
    }
}
