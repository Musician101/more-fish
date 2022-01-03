package io.musician101.morefish.spigot.gui;

import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.musicianlibrary.java.minecraft.spigot.gui.chest.SpigotChestGUI;
import io.musician101.musicianlibrary.java.minecraft.spigot.gui.chest.SpigotIconBuilder;
import javax.annotation.Nonnull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class MoreFishGUI extends SpigotChestGUI<SpigotMoreFish> implements Listener {

    protected static final ItemStack BACK = SpigotIconBuilder.of(Material.BARRIER, "Back");
    protected static final ItemStack NEXT = SpigotIconBuilder.of(Material.ARROW, "Next Page");
    protected static final ItemStack PREVIOUS = SpigotIconBuilder.of(Material.ARROW, "Previous Page");

    protected MoreFishGUI(@Nonnull String title, @Nonnull Player user) {
        super(user, title, 54, SpigotMoreFish.getInstance(), false);
    }

    protected void glassPaneButton(int slot) {
        setButton(slot, SpigotIconBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE, " "));
    }
}
