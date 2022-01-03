package io.musician101.morefish.forge.shop;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishBags;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.morefish.forge.announcement.ForgePlayerAnnouncement;
import io.musician101.morefish.forge.fishing.catchhandler.ForgeCatchHandler;
import io.musician101.morefish.forge.item.FishItemStackConverter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

//TODO create custom inventory and bags
public final class FishShopGui extends Screen {

    @Nonnull
    private final List<Fish> fish;
    @Nonnull
    private final List<FishRarity<ForgePlayerAnnouncement, TextFormatting, ForgeCatchHandler>> selectedRarities;
    private int page = 1;

    public FishShopGui(@Nonnull List<ItemStack> fish, @Nonnull List<FishRarity<ForgePlayerAnnouncement, TextFormatting, ForgeCatchHandler>> selectedRarities) {
        super(getLangConfig().text("shop-gui-title"));
        this.fish = fish;
        this.selectedRarities = selectedRarities;
        this.extraClickHandler = event -> {
            if (!getConverter().isFish(event.getCurrentItem())) {
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(ForgeMoreFish.getInstance(), this::updatePriceIcon, 4);
        };
        this.extraCloseHandler = event -> {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            FishBags<ItemStack> fishBags = getPlugin().getFishBags();
            if (fishBags.getMaxAllowedPages(player.getUniqueID()) > 0) {
                fishBags.update(player.getUniqueID(), inventory.getContents(), page);
                return;
            }

            IntStream.range(0, 45).mapToObj(inventory::getItem).filter(Objects::nonNull).forEach(itemStack -> user.getWorld().dropItem(user.getLocation(), itemStack.clone()));
        };
        this.extraDragHandler = event -> {
            inventory.setItem(49, null);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::updatePriceIcon);
        };

        setButton(47, ForgeIconBuilder.of(Material.CHEST, "Set Sale Filter(s)"), ImmutableMap.of(ClickType.LEFT, FishShopFilterGui::new));
        updateSlots();
        IntStream.of(46, 48, 50, 52).forEach(this::glassPaneButton);
    }

    private static Config getConfig() {
        return getPlugin().getPluginConfig();
    }

    private static FishItemStackConverter getConverter() {
        return getPlugin().getConverter();
    }

    private static LangConfig<?, ?, ?> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private static ForgeMoreFish getPlugin() {
        return ForgeMoreFish.getInstance();
    }

    private List<Object> getFilteredFish() {
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

    @Override
    protected void init() {

    }

    private void updatePriceIcon() {
        updatePriceIcon(getTotalPrice());
    }

    private void updatePriceIcon(double price) {
        setButton(49, ForgeIconBuilder.of(Material.EMERALD, getLangConfig().format("shop-emerald-icon-name").replace(ImmutableMap.of("%price%", String.valueOf(price))).output()), ImmutableMap.of(ClickType.LEFT, p -> {
            List<Object> filteredFish = getFilteredFish();
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

                getShop().sell(p, fishList);
                updatePriceIcon(totalPrice);
                p.sendMessage(getLangConfig().format("shop-sold").replace(ImmutableMap.of("%price%", totalPrice)).output());
            }
        }));
    }

    private void updateSlots() {
        FishBags<ItemStack> fishBags = ForgeMoreFish.getInstance().getFishBags();
        Bukkit.getScheduler().scheduleSyncDelayedTask(ForgeMoreFish.getInstance(), () -> {
            return inventory.addItem(fishBags.getFish(player.getUniqueID(), page).toArray(new ItemStack[0]));
        });
        Bukkit.getScheduler().scheduleSyncDelayedTask(ForgeMoreFish.getInstance(), () -> {
            return updatePriceIcon(getTotalPrice());
        }, 2);
        int userMaxAllowedPages = fishBags.getMaxAllowedPages(player.getUniqueID());
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

        Bukkit.getScheduler().scheduleSyncDelayedTask(ForgeMoreFish.getInstance(), () -> updateUpgradeIcon(userMaxAllowedPages), 3);
    }

    private void updateUpgradeIcon(int userMaxAllowedPages) {
        Map<Integer, Integer> upgrades = ForgeMoreFish.getInstance().getPluginConfig().getFishBagUpgrades();
        List<Entry<Integer, Integer>> upgradeEntries = upgrades.entrySet().stream().filter(entry -> entry.getKey() > userMaxAllowedPages).sorted(Comparator.comparingInt(Entry::getKey)).collect(Collectors.toList());

        if (upgradeEntries.isEmpty()) {
            glassPaneButton(51);
            return;
        }

        Entry<Integer, Integer> upgrade = upgradeEntries.get(0);
        ItemStack icon = ForgeIconBuilder.builder(Material.GOLD_INGOT).name("Bag Upgrades").description(TextFormatting.GREEN + "" + upgrade.getKey() + " page(s) for $" + upgrade.getValue()).build();
        setButton(51, icon, ImmutableMap.of(ClickType.LEFT, p -> {
            ForgeMoreFish plugin = ForgeMoreFish.getInstance();

            if (plugin.getVault().getEconomy().withdrawPlayer(player, upgrade.getValue()).type == ResponseType.SUCCESS) {
                plugin.getFishBags().setMaxAllowedPages(player.getUniqueID(), upgrade.getKey());
                Bukkit.getScheduler().scheduleSyncDelayedTask(ForgeMoreFish.getInstance(), () -> updateUpgradeIcon(upgrade.getKey()), 2);
                return;
            }

            player.sendMessage(TextFormatting.AQUA + "[MoreFish]" + TextFormatting.RESET + " You do not have enough money for that upgrade!");
        }));
    }
}
