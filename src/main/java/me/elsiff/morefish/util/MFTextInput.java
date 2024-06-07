package me.elsiff.morefish.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;

public abstract class MFTextInput implements Listener {

    @NotNull
    protected final Player player;

    public MFTextInput(@NotNull Player player) {
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    protected void close(boolean cancelled) {
        HandlerList.unregisterAll(this);
        if (cancelled) {
            onCancelled();
            return;
        }

        onProcessed();
    }

    protected void onProcessed() {

    }

    protected void onCancelled() {

    }

    protected abstract void process(String message);

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().equals(player) && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            close(true);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.equals(this.player)) {
            process(event.getMessage());
            event.setCancelled(true);
            close(false);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer().equals(player)) {
            close(true);
        }
    }

    @EventHandler
    public void openInventory(InventoryOpenEvent event) {
        if (event.getPlayer().equals(player)) {
            close(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getPlayer().equals(player)) {
            close(true);
        }
    }
}
