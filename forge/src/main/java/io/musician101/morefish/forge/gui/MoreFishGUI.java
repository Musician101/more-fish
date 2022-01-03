package io.musician101.morefish.forge.gui;

import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.musicianlibrary.java.minecraft.Forge.gui.ForgeChestGUI;
import io.musician101.musicianlibrary.java.minecraft.Forge.gui.ForgeIconBuilder;
import javax.annotation.Nonnull;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

public abstract class MoreFishGUI extends ForgeChestGUI<ForgeMoreFish> implements Listener {

    protected static final ItemStack BACK = ForgeIconBuilder.of(Material.BARRIER, "Back");
    protected static final ItemStack NEXT = ForgeIconBuilder.of(Material.ARROW, "Next Page");
    protected static final ItemStack PREVIOUS = ForgeIconBuilder.of(Material.ARROW, "Previous Page");

    protected MoreFishGUI(@Nonnull String title, @Nonnull ServerPlayerEntity user) {
        super(user, title, 54, ForgeMoreFish.getInstance(), false);
    }

    protected void glassPaneButton(int slot) {
        setButton(slot, ForgeIconBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE, " "));
    }
}
