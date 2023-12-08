package me.elsiff.morefish.paper.shop;

import io.musician101.musigui.paper.chest.PaperChestGUI;
import me.elsiff.morefish.paper.PaperMoreFish;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.musician101.musigui.paper.chest.PaperIconUtil.customName;
import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;

public class AbstractFishShopGUI extends PaperChestGUI<PaperMoreFish> {

    @NotNull protected final PaperFishShop shop = getPlugin().getFishShop();

    protected AbstractFishShopGUI(@NotNull Component title, @NotNull Player user) {
        super(user, title, 54, getPlugin(), false);
    }

    protected void glassPaneButton(int slot) {
        setButton(slot, customName(new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE), text(" ")));
    }
}
