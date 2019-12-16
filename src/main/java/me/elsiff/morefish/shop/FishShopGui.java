package me.elsiff.morefish.shop;

import com.google.common.collect.ImmutableMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishBags;
import me.elsiff.morefish.fishing.FishRarity;
import me.elsiff.morefish.gui.GUIButton;
import me.elsiff.morefish.item.FishItemStackConverter;
import me.elsiff.morefish.util.ItemUtil;
import me.elsiff.morefish.util.OneTickScheduler;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public final class FishShopGui extends AbstractFishShopGUI {

    @Nonnull
    private final List<FishRarity> selectedRarities;

    public FishShopGui(@Nonnull FishShop shop, @Nonnull FishItemStackConverter converter, @Nonnull OneTickScheduler oneTickScheduler, @Nonnull Player user, int page) {
        super(Lang.INSTANCE.text("shop-gui-title"), shop, converter, oneTickScheduler, user);
        this.selectedRarities = FishShopFilterGui.filters.getOrDefault(user.getUniqueId(), new ArrayList<>());
        this.clickExtraHandler = event -> {
            if (!converter.isFish(event.getCurrentItem())) {
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), this::updatePriceIcon, 4);
        };
        this.closeExtraHandler = event -> {
            Player player = (Player) event.getPlayer();
            FishBags fishBags = MoreFish.instance().getFishBags();
            if (fishBags.getMaxAllowedPages(player) > 0) {
                fishBags.update(player, inventory.getContents(), page);
                return;
            }

            IntStream.range(0, 45).mapToObj(inventory::getItem).filter(Objects::nonNull).forEach(itemStack -> user.getWorld().dropItem(user.getLocation(), itemStack.clone()));
        };
        this.dragExtraHandler = event -> {
            inventory.setItem(49, ItemUtil.EMPTY);
            oneTickScheduler.scheduleLater(this, this::updatePriceIcon);
        };
        FishBags fishBags = MoreFish.instance().getFishBags();
        Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), () -> inventory.addItem(fishBags.getFish(user, page).toArray(new ItemStack[0])));
        IntStream.of(46, 48, 50, 52).forEach(this::glassPaneButton);
        Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), () -> updatePriceIcon(getTotalPrice()), 2);
        int userMaxAllowedPages = fishBags.getMaxAllowedPages(user);
        if (page == 1) {
            glassPaneButton(45);
        }
        else {
            setButton(new GUIButton(45, ClickType.LEFT, ItemUtil.named(Material.ARROW, "Back Page"), p -> new FishShopGui(shop, converter, oneTickScheduler, p, page - 1)));
        }

        if (page < userMaxAllowedPages) {
            setButton(new GUIButton(53, ClickType.LEFT, ItemUtil.named(Material.ARROW, "Next Page"), p -> new FishShopGui(shop, converter, oneTickScheduler, p, page + 1)));
        }
        else {
            glassPaneButton(53);
        }

        setButton(new GUIButton(47, ClickType.LEFT, ItemUtil.named(Material.CHEST, "Set Sale Filter(s)"), p -> new FishShopFilterGui(1, shop, converter, oneTickScheduler, p)));
        Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), () -> updateUpgradeIcon(userMaxAllowedPages), 3);
    }

    private List<ItemStack> getFilteredFish() {
        return IntStream.range(0, 45).mapToObj(inventory::getItem).filter(Objects::nonNull).filter(converter::isFish).filter(itemStack -> shop.priceOf(converter.fish(itemStack)) >= 0).filter(itemStack -> selectedRarities.contains(converter.fish(itemStack).getType().getRarity())).collect(Collectors.toList());
    }

    private double getTotalPrice() {
        return getFilteredFish().stream().mapToDouble(itemStack -> {
            Fish fish = converter.fish(itemStack);
            return shop.priceOf(fish) * itemStack.getAmount();
        }).sum();
    }

    private void updatePriceIcon() {
        updatePriceIcon(getTotalPrice());
    }

    private void updatePriceIcon(double price) {
        setButton(new GUIButton(49, ClickType.LEFT, ItemUtil.named(Material.EMERALD, Lang.INSTANCE.format("shop-emerald-icon-name").replace(ImmutableMap.of("%price%", String.valueOf(price))).output()), p -> {
            List<ItemStack> filteredFish = getFilteredFish();
            if (filteredFish.isEmpty()) {
                p.sendMessage(Lang.INSTANCE.text("shop-no-fish"));
            }
            else {
                double totalPrice = getTotalPrice();
                List<Fish> fishList = new ArrayList<>();
                filteredFish.forEach(itemStack -> {
                    Fish fish = converter.fish(itemStack);
                    for (int i = 0; i < itemStack.getAmount(); i++) {
                        fishList.add(fish);
                    }

                    itemStack.setAmount(0);
                });

                shop.sell(p, fishList);
                updatePriceIcon(totalPrice);
                p.sendMessage(Lang.INSTANCE.format("shop-sold").replace(ImmutableMap.of("%price%", totalPrice)).output());
            }
        }));
    }

    private void updateUpgradeIcon(int userMaxAllowedPages) {
        ConfigurationSection upgrades = Config.INSTANCE.getStandard().getConfigurationSection("fish-bag-upgrades");
        List<Entry<Integer, Integer>> upgradeEntries = upgrades.getKeys(false).stream().map(key -> {
            int maxAllowedPages = Integer.parseInt(key);
            int price = upgrades.getInt(key);
            return new SimpleEntry<>(maxAllowedPages, price);
        }).filter(entry -> entry.getKey() > userMaxAllowedPages).sorted(Comparator.comparingInt(Entry::getKey)).collect(Collectors.toList());

        if (upgradeEntries.isEmpty()) {
            glassPaneButton(51);
            return;
        }

        Entry<Integer, Integer> upgrade = upgradeEntries.get(0);
        ItemStack icon = ItemUtil.setLore(ItemUtil.named(Material.GOLD_INGOT, "Bag Upgrades"), Collections.singletonList(ChatColor.GREEN + "" + upgrade.getKey() + " page(s) for $" + upgrade.getValue()));
        setButton(new GUIButton(51, ClickType.LEFT, icon, p -> {
            MoreFish plugin = MoreFish.instance();
            if (plugin.getVault().getEconomy().withdrawPlayer(user, upgrade.getValue()).type == ResponseType.SUCCESS) {
                plugin.getFishBags().setMaxAllowedPages(user, upgrade.getKey());
                Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), () -> updateUpgradeIcon(upgrade.getKey()), 2);
                return;
            }

            user.sendMessage(ChatColor.AQUA + "[MoreFish]" + ChatColor.RESET + " You do not have enough money for that upgrade!");
        }));
    }
}
