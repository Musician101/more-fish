package io.musician101.morefish.sponge.gui;

import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.SpongeChestGUI;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.SpongeIconBuilder;
import javax.annotation.Nonnull;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public abstract class MoreFishGUI extends SpongeChestGUI {

    protected static final ItemStack BACK = SpongeIconBuilder.of(ItemTypes.BARRIER, Text.of("Back"));
    protected static final ItemStack NEXT = SpongeIconBuilder.of(ItemTypes.ARROW, Text.of("Next Page"));
    protected static final ItemStack PREVIOUS = SpongeIconBuilder.of(ItemTypes.ARROW, Text.of("Previous Page"));

    protected MoreFishGUI(@Nonnull Text title, @Nonnull Player user) {
        super(user, title, 54, SpongeMoreFish.getInstance().getPluginContainer(), false);
    }

    protected void glassPaneButton(int slot) {
        setButton(slot, SpongeIconBuilder.builder(ItemTypes.STAINED_GLASS_PANE).name(Text.of(" ")).offer(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).build());
    }
}
