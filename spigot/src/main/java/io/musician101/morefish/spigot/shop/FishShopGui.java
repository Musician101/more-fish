package io.musician101.morefish.spigot.shop;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishBags;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import io.musician101.morefish.spigot.gui.MoreFishGUI;
import io.musician101.morefish.spigot.hooker.SpigotVaultHooker;
import io.musician101.morefish.spigot.item.FishItemStackConverter;
import io.musician101.musicianlibrary.java.minecraft.spigot.gui.chest.SpigotIconBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public final class FishShopGui extends MoreFishGUI {

    @Nonnull
    private final List<FishRarity> selectedRarities;
    private int page = 1;

    public FishShopGui(@Nonnull Player user) {
        super(getLangConfig().text("shop-gui-title"), user);
        this.selectedRarities = FishShopFilterGui.filters.getOrDefault(user.getUniqueId(), new ArrayList<>());
        this.extraClickHandler = event -> {
            if (!getConverter().isFish(event.getCurrentItem())) {
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotMoreFish.getInstance(), this::updatePriceIcon, 4);
        };
        this.extraCloseHandler = event -> {
            Player player = (Player) event.getPlayer();
            FishBags<ItemStack> fishBags = getPlugin().getFishBags();
            if (fishBags.getMaxAllowedPages(player.getUniqueId()) > 0) {
                fishBags.update(player.getUniqueId(), inventory.getContents(), page);
                return;
            }

            IntStream.range(0, 45).mapToObj(inventory::getItem).filter(Objects::nonNull).forEach(itemStack -> user.getWorld().dropItem(user.getLocation(), itemStack.clone()));
        };
        this.extraDragHandler = event -> {
            inventory.setItem(49, null);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::updatePriceIcon);
        };

        setButton(47, SpigotIconBuilder.of(Material.CHEST, "Set Sale Filter(s)"), ImmutableMap.of(ClickType.LEFT, FishShopFilterGui::new));
        updateSlots();
        IntStream.of(46, 48, 50, 52).forEach(this::glassPaneButton);
    }

    private static Config<SpigotTextFormat, SpigotTextListFormat, String> getConfig() {
        return getPlugin().getPluginConfig();
    }

    private static FishItemStackConverter getConverter() {
        return getPlugin().getConverter();
    }

    private static LangConfig<SpigotTextFormat, SpigotTextListFormat, String> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private static SpigotMoreFish getPlugin() {
        return SpigotMoreFish.getInstance();
    }

    private List<ItemStack> getFilteredFish() {
        return IntStream.range(0, 45).mapToObj(inventory::getItem).filter(Objects::nonNull).filter(getConverter()::isFish).filter(itemStack -> getShop().priceOf(getConverter().fish(itemStack)) >= 0).filter(itemStack -> selectedRarities.contains(getConverter().fish(itemStack).getType().getRarity())).collect(Collectors.toList());
    }

    private FishShopConfig getShop() {
        return getConfig().getFishShopConfig();
    }

    private double getTotalPrice() {
        return getFilteredFish().stream().mapToDouble(itemStack -> {
            Fish fish = getConverter().fish(itemStack);
            return getShop().priceOf(fish) * itemStack.getAmount();
        }).sum();
    }

    private void updatePriceIcon() {
        updatePriceIcon(getTotalPrice());
    }

    private void updatePriceIcon(double price) {
        setButton(49, SpigotIconBuilder.of(Material.EMERALD, getLangConfig().format("shop-emerald-icon-name").replace(ImmutableMap.of("%price%", String.valueOf(price))).output()), ImmutableMap.of(ClickType.LEFT, p -> {
            List<ItemStack> filteredFish = getFilteredFish();
            if (filteredFish.isEmpty()) {
                p.sendMessage(getLangConfig().text("shop-no-fish"));
            }
            else {
                double totalPrice = getTotalPrice();
                List<Fish> fishList = new ArrayList<>();
                filteredFish.forEach(itemStack -> {
                    Fish fish = getConverter().fish(itemStack);
                    for (int i = 0; i < itemStack.getAmount(); i++) {
                        fishList.add(fish);
                    }

                    itemStack.setAmount(0);
                });

                SpigotVaultHooker vault = getPlugin().getVault();
                if (!vault.hasHooked()) {
                    throw new IllegalStateException("Vault must be hooked for fish shop feature");
                }

                if (!vault.hasEconomy()) {
                    throw new IllegalStateException("Vault doesn't have economy plugin");
                }

                Economy economy = vault.getEconomy();
                if (economy == null || !economy.isEnabled()) {
                    throw new IllegalStateException("Economy must be enabled");
                }

                economy.depositPlayer(player, fishList.stream().mapToDouble(SpigotMoreFish.getInstance().getPluginConfig().getFishShopConfig()::priceOf).sum());
                updatePriceIcon(totalPrice);
                p.sendMessage(getLangConfig().format("shop-sold").replace(ImmutableMap.of("%price%", totalPrice)).output());
            }
        }));
    }

    private void updateSlots() {
        FishBags<ItemStack> fishBags = SpigotMoreFish.getInstance().getFishBags();
        Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotMoreFish.getInstance(), () -> inventory.addItem(fishBags.getFish(player.getUniqueId(), page).toArray(new ItemStack[0])));
        Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotMoreFish.getInstance(), () -> updatePriceIcon(getTotalPrice()), 2);
        int userMaxAllowedPages = fishBags.getMaxAllowedPages(player.getUniqueId());
        if (page == 1) {
            glassPaneButton(45);
        }
        else {
            setButton(45, PREVIOUS, ImmutableMap.of(ClickType.LEFT, p -> {
                page--;
                updateSlots();
            }));
        }

        if (page < userMaxAllowedPages) {
            setButton(53, NEXT, ImmutableMap.of(ClickType.LEFT, p -> {
                page++;
                updateSlots();
            }));
        }
        else {
            glassPaneButton(53);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotMoreFish.getInstance(), () -> updateUpgradeIcon(userMaxAllowedPages), 3);
    }

    @SuppressWarnings("ConstantConditions")
    private void updateUpgradeIcon(int userMaxAllowedPages) {
        Map<Integer, Integer> upgrades = SpigotMoreFish.getInstance().getPluginConfig().getFishBagUpgrades();
        List<Entry<Integer, Integer>> upgradeEntries = upgrades.entrySet().stream().filter(entry -> entry.getKey() > userMaxAllowedPages).sorted(Comparator.comparingInt(Entry::getKey)).toList();

        if (upgradeEntries.isEmpty()) {
            glassPaneButton(51);
            return;
        }

        Entry<Integer, Integer> upgrade = upgradeEntries.get(0);
        ItemStack icon = SpigotIconBuilder.builder(Material.GOLD_INGOT).name("Bag Upgrades").description(ChatColor.GREEN + "" + upgrade.getKey() + " page(s) for $" + upgrade.getValue()).build();
        setButton(51, icon, ImmutableMap.of(ClickType.LEFT, p -> {
            SpigotMoreFish plugin = SpigotMoreFish.getInstance();

            if (plugin.getVault().getEconomy().withdrawPlayer(player, upgrade.getValue()).type == ResponseType.SUCCESS) {
                plugin.getFishBags().setMaxAllowedPages(player.getUniqueId(), upgrade.getKey());
                Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotMoreFish.getInstance(), () -> updateUpgradeIcon(upgrade.getKey()), 2);
                return;
            }

            player.sendMessage(ChatColor.AQUA + "[MoreFish]" + ChatColor.RESET + " You do not have enough money for that upgrade!");
        }));
    }
}
