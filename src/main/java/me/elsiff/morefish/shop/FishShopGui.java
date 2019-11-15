package me.elsiff.morefish.shop;

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.gui.GUIButton;
import me.elsiff.morefish.item.FishItemStackConverter;
import me.elsiff.morefish.util.ItemUtil;
import me.elsiff.morefish.util.OneTickScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public final class FishShopGui implements Listener {

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
    private final List<GUIButton> buttons = new ArrayList<>();
    private final FishItemStackConverter converter;
    @Nonnull
    private final String name;
    private final OneTickScheduler oneTickScheduler;
    private final FishShop shop;
    private final Player user;

    public FishShopGui(@Nonnull FishShop shop, @Nonnull FishItemStackConverter converter, @Nonnull OneTickScheduler oneTickScheduler, @Nonnull Player user) {
        this.user = user;
        this.name = Lang.INSTANCE.text("shop-gui-title");
        this.inventory = parseInventory(user, name, 54);
        this.shop = shop;
        this.converter = converter;
        this.oneTickScheduler = oneTickScheduler;
        for (int i = 45; i < 54; i++) {
            setButton(new GUIButton(i, ClickType.LEFT, ItemUtil.named(Material.LIGHT_BLUE_STAINED_GLASS_PANE, " "), null));
        }

        updatePriceIcon(0D);
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

    private static Inventory parseInventory(Player player, String name, int size) {
        return name == null ? Bukkit.createInventory(player, size) : Bukkit.createInventory(player, size, name);
    }

    private final List<ItemStack> allFishItemStacks() {
        return IntStream.range(0, 45).mapToObj(inventory::getItem).filter(Objects::nonNull).filter(converter::isFish).filter(itemStack -> shop.priceOf(converter.fish(itemStack)) >= 0).collect(Collectors.toList());
    }

    private final void dropAllFish() {
        IntStream.range(0, 45).mapToObj(inventory::getItem).filter(Objects::nonNull).forEach(itemStack -> user.getWorld().dropItem(user.getLocation(), itemStack.clone()));
    }

    private final double getTotalPrice() {
        return allFishItemStacks().stream().mapToDouble(itemStack -> {
            Fish fish = converter.fish(itemStack);
            return (shop.priceOf(fish) * itemStack.getAmount());
        }).sum();
    }

    @EventHandler
    public final void handleClick(InventoryClickEvent event) {
        if (isCorrectInventory(event.getView())) {
            if (buttons.stream().map(GUIButton::getSlot).anyMatch(i -> i == event.getRawSlot())) {
                event.setCancelled(true);
                buttons.stream().filter(button -> button.getSlot() == event.getRawSlot() && button.getClickType() == event.getClick()).findFirst().flatMap(GUIButton::getAction).ifPresent(action -> action.accept((Player) event.getWhoClicked()));
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), this::updatePriceIcon, 2);
        }
    }

    @EventHandler
    public void handleClose(InventoryCloseEvent event) {
        if (isCorrectInventory(event.getView())) {
            oneTickScheduler.cancelAllOf(this);
            dropAllFish();
        }
    }

    @EventHandler
    public void handleDrag(InventoryDragEvent event) {
        if (isCorrectInventory(event.getView())) {
            inventory.setItem(49, ItemUtil.EMPTY);
            oneTickScheduler.scheduleLater(this, this::updatePriceIcon);
        }
    }

    private boolean isCorrectInventory(InventoryView inventoryView) {
        return inventoryView.getTitle().equals(name) && inventoryView.getPlayer().getUniqueId().equals(user.getUniqueId());
    }

    private void setButton(GUIButton guiButton) {
        buttons.removeIf(g -> g.getSlot() == guiButton.getSlot() && g.getClickType() == guiButton.getClickType());
        buttons.add(guiButton);
        inventory.setItem(guiButton.getSlot(), guiButton.getItemStack());
    }

    private void updatePriceIcon() {
        updatePriceIcon(getTotalPrice());
    }

    private final void updatePriceIcon(double price) {
        setButton(new GUIButton(49, ClickType.LEFT, ItemUtil.named(Material.EMERALD, Lang.INSTANCE.format("shop-emerald-icon-name").replace(ImmutableMap.of("%price%", String.valueOf(price))).output()), p -> {
            List<ItemStack> allFishItemStacks = allFishItemStacks();
            if (allFishItemStacks.isEmpty()) {
                p.sendMessage(Lang.INSTANCE.text("shop-no-fish"));
            }
            else {
                double totalPrice = getTotalPrice();
                List<Fish> fishList = new ArrayList<>();
                allFishItemStacks.forEach(itemStack -> {
                    Fish fish = converter.fish(itemStack);
                    for (int i = 0; i < itemStack.getAmount(); i++) {
                        fishList.add(fish);
                    }

                    itemStack.setAmount(0);
                });

                shop.sell(p, fishList);
                updatePriceIcon(0D);
                String msg = Lang.INSTANCE.format("shop-sold").replace(ImmutableMap.of("%price%", totalPrice)).output();
                p.sendMessage(msg);
            }
        }));
    }
}
