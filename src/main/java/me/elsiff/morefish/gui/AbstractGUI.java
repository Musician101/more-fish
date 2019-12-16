package me.elsiff.morefish.gui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.util.ItemUtil;
import me.elsiff.morefish.util.OneTickScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public abstract class AbstractGUI implements Listener {

    private static final String SERVER_VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static Field activeContainer;
    private static Field defaultContainer;
    private static Method getHandle;
    private static Method handleInventoryCloseEvent;

    static {
        try {
            final Class<?> entityHuman = Class.forName("net.minecraft.server." + SERVER_VERSION + ".EntityHuman");
            handleInventoryCloseEvent = Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + ".event.CraftEventFactory").getDeclaredMethod("handleInventoryCloseEvent", entityHuman);
            getHandle = Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + ".entity.CraftPlayer").getDeclaredMethod("getHandle");
            defaultContainer = entityHuman.getDeclaredField("defaultContainer");
            activeContainer = entityHuman.getDeclaredField("activeContainer");
        }
        catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    protected final Inventory inventory;
    @Nonnull
    protected final String title;
    @Nonnull
    protected final Player user;
    @Nonnull
    protected final OneTickScheduler oneTickScheduler;
    @Nonnull
    private final List<GUIButton> buttons = new ArrayList<>();
    @Nonnull
    protected Consumer<InventoryClickEvent> clickExtraHandler = e -> {
    };
    @Nonnull
    protected Consumer<InventoryCloseEvent> closeExtraHandler = e -> {
    };
    @Nonnull
    protected Consumer<InventoryDragEvent> dragExtraHandler = e -> {
    };

    protected AbstractGUI(@Nonnull String title, @Nonnull OneTickScheduler oneTickScheduler, @Nonnull Player user) {
        this.user = user;
        this.title = title;
        this.inventory = parseInventory(user, this.title);
        this.oneTickScheduler = oneTickScheduler;
        Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), () -> {
            try {
                inventory.clear();
                buttons.forEach(button -> inventory.setItem(button.getSlot(), button.getItemStack()));

                final Object entityHuman = getHandle.invoke(user);
                handleInventoryCloseEvent.invoke(null, entityHuman);
                activeContainer.set(entityHuman, defaultContainer.get(entityHuman));

                user.openInventory(inventory);
                Bukkit.getPluginManager().registerEvents(this, MoreFish.instance());
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    private static Inventory parseInventory(Player player, String name) {
        return name == null ? Bukkit.createInventory(player, 54) : Bukkit.createInventory(player, 54, name);
    }

    protected void glassPaneButton(int slot) {
        setButton(new GUIButton(slot, ClickType.LEFT, ItemUtil.named(Material.LIGHT_BLUE_STAINED_GLASS_PANE, " "), null));
    }

    @EventHandler
    public final void handleClick(InventoryClickEvent event) {
        if (isCorrectInventory(event.getView())) {
            if (buttons.stream().map(GUIButton::getSlot).anyMatch(i -> i == event.getRawSlot())) {
                event.setCancelled(true);
                buttons.stream().filter(button -> button.getSlot() == event.getRawSlot() && button.getClickType() == event.getClick()).findFirst().flatMap(GUIButton::getAction).ifPresent(action -> action.accept((Player) event.getWhoClicked()));
            }

            clickExtraHandler.accept(event);
        }
    }

    @EventHandler
    public void handleClose(InventoryCloseEvent event) {
        if (isCorrectInventory(event.getView())) {
            oneTickScheduler.cancelAllOf(this);
            closeExtraHandler.accept(event);
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void handleDrag(InventoryDragEvent event) {
        if (isCorrectInventory(event.getView())) {
            dragExtraHandler.accept(event);
        }
    }

    private boolean isCorrectInventory(InventoryView inventoryView) {
        return inventoryView.getTitle().equals(title) && inventoryView.getPlayer().getUniqueId().equals(user.getUniqueId());
    }

    protected void setButton(GUIButton guiButton) {
        buttons.removeIf(g -> g.getSlot() == guiButton.getSlot() && g.getClickType() == guiButton.getClickType());
        buttons.add(guiButton);
        inventory.setItem(guiButton.getSlot(), guiButton.getItemStack());
    }
}
