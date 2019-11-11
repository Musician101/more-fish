package me.elsiff.morefish.shop;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import me.elsiff.egui.inventory.ChestInventoryGui;
import me.elsiff.egui.state.ComponentClickState;
import me.elsiff.egui.state.GuiCloseState;
import me.elsiff.egui.state.GuiDragState;
import me.elsiff.egui.state.GuiItemChangeState;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.item.FishItemStackConverter;
import me.elsiff.morefish.util.InventoryUtils;
import me.elsiff.morefish.util.OneTickScheduler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class FishShopGui extends ChestInventoryGui {

    private final List<Integer> bottomBarSlots = slotsOf(new IntRange(getMinX(), getMaxX()), this.getMaxY());
    private final FishItemStackConverter converter;
    private final List<Integer> fishSlots = slotsOf(new IntRange(getMinX(), this.getMaxX()), RangesKt.until(this.getMinY(), this.getMaxY()));
    private final OneTickScheduler oneTickScheduler;
    private final int priceIconSlot = slotOf(this.getCenterX(), this.getMaxY());
    private final FishShop shop;
    private final Player user;

    public FishShopGui(@Nonnull FishShop shop, @Nonnull FishItemStackConverter converter, @Nonnull OneTickScheduler oneTickScheduler, @Nonnull Player user) {
        super(user.getServer(), 4, Lang.INSTANCE.text("shop-gui-title"));
        this.shop = shop;
        this.converter = converter;
        this.oneTickScheduler = oneTickScheduler;
        this.user = user;
        ItemStack bottomBarIcon = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta itemMeta = bottomBarIcon.getItemMeta();
        itemMeta.setDisplayName(" ");
        bottomBarIcon.setItemMeta(itemMeta);
        bottomBarSlots.forEach(i -> getInventory().setItem(i, bottomBarIcon));
        updatePriceIcon(0D);
        controllableSlots().addAll(fishSlots);
    }

    private final List<ItemStack> allFishItemStacks() {
        return fishSlots.stream().map(i -> getInventory().getItem(i)).filter(Objects::nonNull).filter(converter::isFish).collect(Collectors.toList());
    }

    private final void dropAllFish() {
        allFishItemStacks().forEach(itemStack -> user.getWorld().dropItem(user.getLocation(), itemStack.clone()));
    }

    private final double getTotalPrice() {
        return allFishItemStacks().stream().mapToDouble(itemStack -> {
            Fish fish = converter.fish(itemStack);
            return (shop.priceOf(fish) * itemStack.getAmount());
        }).sum();
    }

    public void handleClose(@Nonnull GuiCloseState state) {
        oneTickScheduler.cancelAllOf(this);
        dropAllFish();
    }

    public void handleComponentClick(@Nonnull ComponentClickState state) {
        if (state.getSlot() == priceIconSlot) {
            List<ItemStack> allFishItemStacks = this.allFishItemStacks();
            if (allFishItemStacks.isEmpty()) {
                user.sendMessage(Lang.INSTANCE.text("shop-no-fish"));
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

                shop.sell(user, fishList);
                updatePriceIcon(0D);
                String msg = Lang.INSTANCE.format("shop-sold").replace(ImmutableMap.of("%price%", totalPrice)).output();
                user.sendMessage(msg);
            }
        }

    }

    public void handleDrag(@Nonnull GuiDragState state) {
        getInventory().setItem(this.priceIconSlot, InventoryUtils.emptyStack());
        oneTickScheduler.scheduleLater(this, this::updatePriceIcon);
    }

    public void handleItemChange(@Nonnull GuiItemChangeState state) {
        oneTickScheduler.scheduleLater(this, this::updatePriceIcon);
    }

    private void updatePriceIcon() {
        updatePriceIcon(getTotalPrice());
    }

    private final void updatePriceIcon(double price) {
        ItemStack emeraldIcon = new ItemStack(Material.EMERALD);
        ItemMeta itemMeta = emeraldIcon.getItemMeta();
        itemMeta.setDisplayName(Lang.INSTANCE.format("shop-emerald-icon-name").replace(ImmutableMap.of("%price%", String.valueOf(price))).output());
        emeraldIcon.setItemMeta(itemMeta);
        getInventory().setItem(priceIconSlot, emeraldIcon);
    }
}
