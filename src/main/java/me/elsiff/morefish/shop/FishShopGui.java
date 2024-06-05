package me.elsiff.morefish.shop;

import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishBags;
import me.elsiff.morefish.fishing.FishRarity;
import me.elsiff.morefish.hooker.VaultHooker;
import me.elsiff.morefish.item.FishItemStackConverter;
import me.elsiff.morefish.text.Lang;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.musician101.musigui.paper.chest.PaperIconUtil.customName;
import static io.musician101.musigui.paper.chest.PaperIconUtil.setLore;
import static me.elsiff.morefish.MoreFish.getPlugin;

public final class FishShopGui extends AbstractFishShopGUI {

    @NotNull
    private final FishBags fishBags;
    @NotNull
    private final List<FishRarity> selectedRarities;
    private int page;

    public FishShopGui(@NotNull Player user, int page) {
        super(Lang.replace("<mf-lang:shop-gui-title>"), user);
        this.page = page;
        this.selectedRarities = FishShopFilterGui.FILTERS.getOrDefault(user.getUniqueId(), new ArrayList<>());
        this.fishBags = getPlugin().getFishBags();
        updateButtons();
        IntStream.of(46, 48, 50, 52).forEach(this::glassPaneButton);
        setButton(47, customName(new ItemStack(Material.CHEST), Lang.replace("<mf-lang:shop-sale-filter-icon-name>")), ClickType.LEFT, p -> new FishShopFilterGui(1, p));
    }

    private Economy getEconomy() {
        VaultHooker vault = plugin.getVault();
        if (!vault.hasHooked()) {
            throw new IllegalStateException("Vault must be hooked for fish shop feature");
        }

        if (vault.hasEconomy()) {
            return vault.getEconomy().orElseThrow(() -> new IllegalStateException("Economy must be enabled"));
        }

        throw new IllegalStateException("Vault doesn't have economy plugin");
    }

    private List<ItemStack> getFilteredFish() {
        return IntStream.range(0, 45).mapToObj(inventory::getItem).filter(Objects::nonNull).filter(FishItemStackConverter::isFish).filter(itemStack -> price(FishItemStackConverter.fish(itemStack)) >= 0).filter(itemStack -> selectedRarities.contains(FishItemStackConverter.fish(itemStack).rarity())).collect(Collectors.toList());
    }

    private double price(Fish fish) {
        double multi = getPlugin().getConfig().getDouble("fish-shop.multiplier");
        return multi * fish.length() + fish.type().additionalPrice();
    }

    private double getTotalPrice() {
        return BigDecimal.valueOf(getFilteredFish().stream().mapToDouble(itemStack -> {
            Fish fish = FishItemStackConverter.fish(itemStack);
            return price(fish) * itemStack.getAmount();
        }).sum()).setScale(2, RoundingMode.DOWN).doubleValue();
    }

    @Override
    protected void handleExtraClick(InventoryClickEvent event) {
        if (!(FishItemStackConverter.isFish(event.getCurrentItem()) || FishItemStackConverter.isFish(event.getCursor()))) {
            event.setCancelled(true);
            return;
        }

        if (event.getClick() == ClickType.SHIFT_LEFT) {
            event.getWhoClicked().getScheduler().runDelayed(getPlugin(), task -> updatePriceIcon(), null, 1L);
        }
        else {
            updatePriceIcon();
        }

        if (fishBags.getMaxAllowedPages(player) > 0) {
            fishBags.update(player, inventory.getContents(), page);
        }
    }

    @Override
    protected void handleExtraClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        FishBags fishBags = getPlugin().getFishBags();
        if (fishBags.getMaxAllowedPages(player) > 0) {
            fishBags.update(player, inventory.getContents(), page);
            return;
        }

        IntStream.range(0, 45).mapToObj(inventory::getItem).filter(Objects::nonNull).forEach(itemStack -> this.player.getWorld().dropItem(this.player.getLocation(), itemStack.clone()));
    }

    @Override
    protected void handleExtraDrag(InventoryDragEvent event) {
        inventory.setItem(49, null);
        updatePriceIcon();
    }

    private void updateButtons() {
        player.getScheduler().runDelayed(getPlugin(), task -> {
            List<ItemStack> fish = fishBags.getFish(player, page);
            IntStream.range(0, 45).forEach(i -> {
                removeButton(i);
                if (i < fish.size()) {
                    addItem(i, fish.get(i));
                }
            });

            updatePriceIcon();
            int userMaxAllowedPages = fishBags.getMaxAllowedPages(player);
            if (page == 1) {
                glassPaneButton(45);
            }
            else {
                setButton(45, customName(new ItemStack(Material.ARROW), Lang.replace("<mf-lang:shop-previous-page-icon-name>")), ClickType.LEFT, p -> {
                    page--;
                    updateButtons();
                });
            }

            if (page < userMaxAllowedPages) {
                setButton(53, customName(new ItemStack(Material.ARROW), Lang.replace("<mf-lang:shop-next-page-icon-name>")), ClickType.LEFT, p -> {
                    page++;
                    updateButtons();
                });
            }
            else {
                glassPaneButton(53);
            }

            updateUpgradeIcon(userMaxAllowedPages);
        }, null, 2);
    }

    private void updatePriceIcon() {
        updatePriceIcon(getTotalPrice());
    }

    private void updatePriceIcon(double price) {
        player.getScheduler().run(getPlugin(), task -> {
            Component name = Lang.replace("<mf-lang:shop-sell-icon-name>", Lang.tagResolver("amount", price));
            setButton(49, customName(new ItemStack(Material.EMERALD), name), ClickType.LEFT, p -> {
                List<ItemStack> filteredFish = getFilteredFish();
                if (filteredFish.isEmpty()) {
                    p.sendMessage(Lang.replace("<mf-lang:shop-no-fish-to-sell>"));
                }
                else {
                    double totalPrice = getTotalPrice();
                    List<Fish> fishList = new ArrayList<>();
                    filteredFish.forEach(itemStack -> {
                        Fish fish = FishItemStackConverter.fish(itemStack);
                        for (int i = 0; i < itemStack.getAmount(); i++) {
                            fishList.add(fish);
                        }

                        itemStack.setAmount(0);
                    });

                    getEconomy().depositPlayer(player, fishList.stream().mapToDouble(this::price).sum());
                    fishBags.update(player, inventory.getContents(), page);
                    updatePriceIcon(totalPrice);
                    p.sendMessage(Lang.replace("<mf-lang:shop-fish-sold>", Lang.tagResolver("total-price", totalPrice)));
                }
            });
        }, null);
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

            Entry<Integer, Integer> upgrade = upgradeEntries.getFirst();
            Component lore = Lang.replace("<mf-lang:shop-upgrade-icon-name>");
            ItemStack icon = setLore(customName(new ItemStack(Material.GOLD_INGOT), Lang.replace("<mf-lang:shop-upgrade-icon-name>")), lore);
            setButton(51, icon, ClickType.LEFT, p -> {
                MoreFish plugin = getPlugin();
                if (plugin.getVault().getEconomy().filter(economy -> economy.withdrawPlayer(p, upgrade.getValue()).type == ResponseType.SUCCESS).isPresent()) {
                    plugin.getFishBags().setMaxAllowedPages(p, upgrade.getKey());
                    updateUpgradeIcon(upgrade.getKey());
                    return;
                }

                p.sendMessage(Lang.replace("<mf-lang:shop-not-enough-money>"));
            });
        }
    }
}
