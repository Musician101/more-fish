package io.musician101.morefish.sponge.gui;

import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.chest.SpongeChestGUI;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.chest.SpongeIconBuilder;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

public abstract class MoreFishGUI extends SpongeChestGUI {

    protected static final ItemStack BACK = SpongeIconBuilder.of(ItemTypes.BARRIER, Component.text("Back"));
    protected static final ItemStack NEXT = SpongeIconBuilder.of(ItemTypes.ARROW, Component.text("Next Page"));
    protected static final ItemStack PREVIOUS = SpongeIconBuilder.of(ItemTypes.ARROW, Component.text("Previous Page"));

    protected MoreFishGUI(@Nonnull Component title, @Nonnull ServerPlayer user, boolean readOnly) {
        super(user, title, 54, SpongeMoreFish.getInstance().getPluginContainer(), false, readOnly);
    }

    protected void glassPaneButton(int slot) {
        setButton(slot, SpongeIconBuilder.of(ItemTypes.LIGHT_BLUE_STAINED_GLASS_PANE, Component.text(" ")));
    }
}
