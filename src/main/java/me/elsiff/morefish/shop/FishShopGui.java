package me.elsiff.morefish.shop;

import io.musician101.musigui.paper.chest.PaperPagedChestGUI;
import io.papermc.paper.dialog.Dialog;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.bags.FishBags;
import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.gui.MoreFishGUI;
import me.elsiff.morefish.hooker.VaultHooker;
import me.elsiff.morefish.item.FishItemStackUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.translation.Argument;
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
import org.jspecify.annotations.NullMarked;

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

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public final class FishShopGui extends MoreFishGUI implements PaperPagedChestGUI {

    private final FishBags fishBags;
    private final List<FishRarity> selectedRarities;
    private int page = 1;

    public FishShopGui(Player user) {
        super(user, Component.translatable("morefish.main.shop.title"), 54);
        this.selectedRarities = FishShopFilterDialog.FILTERS.getOrDefault(user.getUniqueId(), new ArrayList<>());
        this.fishBags = getPlugin().getFishBags();
        update();
    }

    @Override
    public void update() {
        IntStream.range(45, 54).forEach(this::addGlassPane);
        List<ItemStack> fish = fishBags.getFish(player, page);
        int slotsPerPage = 45;
        List<ItemStack> fishToAdd = getPage(fish, page, slotsPerPage, (a, b) -> 0);
        int pages = pages(fish.size(), slotsPerPage);
        previousPageButton(page, p -> {
            page--;
            update();
        });

        nextPageButton(page, pages, p -> {
            page++;
            update();
        });

        IntStream.range(0, 45).forEach(i -> {
            removeButton(i);
            if (i < fishToAdd.size()) {
                addItem(i, fishToAdd.get(i));
            }
        });

        updatePriceIcon();
        updateUpgradeIcon(slotsPerPage);
        setButton(47, createIcon(Material.CHEST, Component.translatable("morefish.main.shop.sale-filter-icon-name")), ClickType.LEFT, p -> {
            Dialog dialog = new FishShopFilterDialog(selectedRarities, p.locale()).build();
            p.showDialog(dialog);
        });
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

    @SuppressWarnings("NullableProblems")
    private List<ItemStack> getFilteredFish() {
        return IntStream.range(0, 45).mapToObj(inventory::getItem).filter(Objects::nonNull).filter(FishItemStackUtil::isFish).filter(itemStack -> price(FishItemStackUtil.fish(itemStack)) >= 0).filter(itemStack -> selectedRarities.contains(FishItemStackUtil.fish(itemStack).rarity())).collect(Collectors.toList());
    }

    private double price(Fish fish) {
        double multi = getPlugin().getConfig().getDouble("fish-shop.multiplier");
        FishType type = fish.type();
        return multi * fish.length() * type.rarity().priceMultiplier() * type.priceMultiplier();
    }

    private double getTotalPrice() {
        return BigDecimal.valueOf(getFilteredFish().stream().mapToDouble(itemStack -> {
            Fish fish = FishItemStackUtil.fish(itemStack);
            return price(fish) * itemStack.getAmount();
        }).sum()).setScale(2, RoundingMode.DOWN).doubleValue();
    }

    @Override
    protected void handleExtraClick(InventoryClickEvent event) {
        if (!(FishItemStackUtil.isFish(event.getCurrentItem()) || FishItemStackUtil.isFish(event.getCursor()))) {
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

    private void updatePriceIcon() {
        updatePriceIcon(getTotalPrice());
    }

    private void updatePriceIcon(double price) {
        TranslatableComponent name = Component.translatable("morefish.main.shop.sell-icon-name", Argument.numeric("amount", price));
        setButton(49, createIcon(Material.EMERALD, name), ClickType.LEFT, this::priceClick);
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
                addGlassPane(51);
                return;
            }

            Entry<Integer, Integer> upgrade = upgradeEntries.getFirst();
            TranslatableComponent name = Component.translatable("morefish.main.shop.upgrade-icon.name");
            TranslatableComponent lore = Component.translatable("morefish.main.shop.upgrade-icon.lore");
            ItemStack icon = createIcon(Material.GOLD_INGOT, name, lore);
            setButton(51, icon, ClickType.LEFT, p -> {
                MoreFish plugin = getPlugin();
                if (plugin.getVault().getEconomy().filter(economy -> economy.withdrawPlayer(p, upgrade.getValue()).type == ResponseType.SUCCESS).isPresent()) {
                    plugin.getFishBags().setMaxAllowedPages(p, upgrade.getKey());
                    updateUpgradeIcon(upgrade.getKey());
                    return;
                }

                p.sendMessage(Component.translatable("morefish.main.shop.not-enough-money"));
            });
        }
    }

    private void priceClick(Player p) {
        List<ItemStack> filteredFish = getFilteredFish();
        if (filteredFish.isEmpty()) {
            p.sendMessage(Component.translatable("morefish.main.shop.no-fish-to-sell"));
        }
        else {
            double totalPrice = getTotalPrice();
            List<Fish> fishList = new ArrayList<>();
            filteredFish.forEach(itemStack -> {
                Fish fish = FishItemStackUtil.fish(itemStack);
                for (int i = 0; i < itemStack.getAmount(); i++) {
                    fishList.add(fish);
                }

                itemStack.setAmount(0);
            });

            getEconomy().depositPlayer(player, fishList.stream().mapToDouble(this::price).sum());
            fishBags.update(player, inventory.getContents(), page);
            updatePriceIcon(totalPrice);
            p.sendMessage(Component.translatable("morefish.main.shop.fish-sold", Argument.numeric("total-price", totalPrice)));
        }
    }
}
