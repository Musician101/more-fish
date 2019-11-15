package me.elsiff.morefish.gui;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public final class GUIButton {

    @Nullable
    private final Consumer<Player> action;
    @Nonnull
    private final ClickType clickType;
    @Nonnull
    private final ItemStack itemStack;
    private final int slot;

    public GUIButton(int slot, @Nonnull ClickType clickType, @Nonnull ItemStack itemStack, @Nullable Consumer<Player> action) {
        this.slot = slot;
        this.clickType = clickType;
        this.itemStack = itemStack;
        this.action = action;
    }

    @Nonnull
    public Optional<Consumer<Player>> getAction() {
        return Optional.ofNullable(action);
    }

    @Nonnull
    public final ClickType getClickType() {
        return clickType;
    }

    @Nonnull
    public final ItemStack getItemStack() {
        return itemStack;
    }

    public final int getSlot() {
        return slot;
    }
}
