package me.elsiff.morefish.shop;

import io.musician101.musicianlibrary.java.minecraft.spigot.gui.chest.SpigotIconBuilder;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishBags;
import me.elsiff.morefish.fishing.FishRarity;
import me.elsiff.morefish.item.FishItemStackConverter;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import static me.elsiff.morefish.item.FishItemStackConverter.fish;
import static me.elsiff.morefish.item.FishItemStackConverter.isFish;

public final class FishShopGui extends AbstractFishShopGUI {

    @Nonnull
    private final List<FishRarity> selectedRarities;

    public FishShopGui(@Nonnull Player user, int page) {
        super(Lang.SHOP_GUI_TITLE, user);
        this.selectedRarities = FishShopFilterGui.filters.getOrDefault(user.getUniqueId(), new ArrayList<>());
        this.extraClickHandler = event -> {
            if (!(isFish(event.getCurrentItem()) || isFish(event.getCursor()))) {
                event.setCancelled(true);
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), this::updatePriceIcon, 4);
        };
        this.extraCloseHandler = event -> {
            Player player = (Player) event.getPlayer();
            FishBags fishBags = MoreFish.instance().getFishBags();
            if (fishBags.getMaxAllowedPages(player) > 0) {
                fishBags.update(player, inventory.getContents(), page);
                return;
            }

            IntStream.range(0, 45).mapToObj(inventory::getItem).filter(Objects::nonNull).forEach(itemStack -> user.getWorld().dropItem(user.getLocation(), itemStack.clone()));
        };
        this.extraDragHandler = event -> {
            inventory.setItem(49, null);
            Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), this::updatePriceIcon);
        };
        FishBags fishBags = MoreFish.instance().getFishBags();
        Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), () -> inventory.addItem(fishBags.getFish(user, page).toArray(new ItemStack[0])));
        IntStream.of(46, 48, 50, 52).forEach(this::glassPaneButton);
        Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), () -> updatePriceIcon(getTotalPrice()), 4);
        int userMaxAllowedPages = fishBags.getMaxAllowedPages(user);
        if (page == 1) {
            glassPaneButton(45);
        }
        else {
            setButton(45, SpigotIconBuilder.of(Material.ARROW, "Back Page"), Map.of(ClickType.LEFT, p -> new FishShopGui(p, page - 1)));
        }

        if (page < userMaxAllowedPages) {
            setButton(53, SpigotIconBuilder.of(Material.ARROW, "Next Page"), Map.of(ClickType.LEFT, p -> new FishShopGui(p, page + 1)));
        }
        else {
            glassPaneButton(53);
        }

        setButton(47, SpigotIconBuilder.of(Material.CHEST, "Set Sale Filter(s)"), Map.of(ClickType.LEFT, p -> new FishShopFilterGui(1, p)));
        Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), () -> updateUpgradeIcon(userMaxAllowedPages), 3);
    }

    private List<ItemStack> getFilteredFish() {
        return IntStream.range(0, 45).mapToObj(inventory::getItem).filter(Objects::nonNull).filter(FishItemStackConverter::isFish).filter(itemStack -> shop.priceOf(fish(itemStack)) >= 0).filter(itemStack -> selectedRarities.contains(fish(itemStack).getType().getRarity())).collect(Collectors.toList());
    }

    private double getTotalPrice() {
        return getFilteredFish().stream().mapToDouble(itemStack -> {
            Fish fish = fish(itemStack);
            return shop.priceOf(fish) * itemStack.getAmount();
        }).sum();
    }

    private void updatePriceIcon() {
        updatePriceIcon(getTotalPrice());
    }

    private void updatePriceIcon(double price) {
        setButton(49, SpigotIconBuilder.of(Material.EMERALD, Lang.replace(Lang.SHOP_EMERALD_ICON_NAME, Map.of("%price%", String.valueOf(price)))), Map.of(ClickType.LEFT, p -> {
            List<ItemStack> filteredFish = getFilteredFish();
            if (filteredFish.isEmpty()) {
                p.sendMessage(Lang.SHOP_NO_FISH);
            }
            else {
                double totalPrice = getTotalPrice();
                List<Fish> fishList = new ArrayList<>();
                filteredFish.forEach(itemStack -> {
                    Fish fish = fish(itemStack);
                    for (int i = 0; i < itemStack.getAmount(); i++) {
                        fishList.add(fish);
                    }

                    itemStack.setAmount(0);
                });

                shop.sell(p, fishList);
                updatePriceIcon(totalPrice);
                p.sendMessage(Lang.replace(Lang.SHOP_SOLD, Map.of("%price%", totalPrice)));
            }
        }));
    }

    private void updateUpgradeIcon(int userMaxAllowedPages) {
        ConfigurationSection upgrades = plugin.getConfig().getConfigurationSection("fish-bag-upgrades");
        if (upgrades != null) {
            List<SimpleEntry<Integer, Integer>> upgradeEntries = upgrades.getKeys(false).stream().map(key -> {
                int maxAllowedPages = Integer.parseInt(key);
                int price = upgrades.getInt(key);
                return new SimpleEntry<>(maxAllowedPages, price);
            }).filter(entry -> entry.getKey() > userMaxAllowedPages).sorted(Comparator.comparingInt(Entry::getKey)).toList();

            if (upgradeEntries.isEmpty()) {
                glassPaneButton(51);
                return;
            }

            Entry<Integer, Integer> upgrade = upgradeEntries.get(0);
            ItemStack icon = SpigotIconBuilder.builder(Material.GOLD_INGOT).name("Bag Upgrades").description(List.of(ChatColor.GREEN + "" + upgrade.getKey() + " page(s) for $" + upgrade.getValue())).build();
            setButton(51, icon, Map.of(ClickType.LEFT, p -> {
                MoreFish plugin = MoreFish.instance();
                if (plugin.getVault().getEconomy().filter(economy -> economy.withdrawPlayer(p, upgrade.getValue()).type == ResponseType.SUCCESS).isPresent()) {
                    plugin.getFishBags().setMaxAllowedPages(p, upgrade.getKey());
                    Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), () -> updateUpgradeIcon(upgrade.getKey()), 2);
                    return;
                }

                p.sendMessage(ChatColor.AQUA + "[MoreFish]" + ChatColor.RESET + " You do not have enough money for that upgrade!");
            }));
        }
    }
}
