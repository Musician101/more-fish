package io.musician101.morefish.sponge.shop;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishBags;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import io.musician101.morefish.sponge.gui.MoreFishGUI;
import io.musician101.morefish.sponge.hooker.SpongeEconomyHooker;
import io.musician101.morefish.sponge.item.FishItemStackConverter;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.chest.SpongeIconBuilder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.menu.ClickTypes;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.server.ServerWorld;

@SuppressWarnings("ConstantConditions")
public final class FishShopGui extends MoreFishGUI {

    @Nonnull
    private final List<FishRarity> selectedRarities;
    private int page = 1;

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public FishShopGui(@Nonnull ServerPlayer player) {
        super(getLangConfig().text("shop-gui-title"), player, false);
        this.selectedRarities = FishShopFilterGui.filters.getOrDefault(player.uniqueId(), new ArrayList<>());
        this.extraClickHandler = (cause, container, slot, slotIndex, clickType) -> {
            if (!getConverter().isFish(slot.peek())) {
                return false;
            }

            Sponge.asyncScheduler().submit(Task.builder().delay(Ticks.of(4)).execute((Runnable) this::updatePriceIcon).plugin(SpongeMoreFish.getInstance().getPluginContainer()).build());
            return true;
        };
        this.extraCloseHandler = event -> {
            ServerPlayer p = event.cause().first(ServerPlayer.class).get();
            FishBags<ItemStack> fishBags = getPlugin().getFishBags();
            if (fishBags.getMaxAllowedPages(p.uniqueId()) > 0) {
                fishBags.update(p.uniqueId(), inventory.slots().stream().map(Inventory::peek).toArray(ItemStack[]::new), page);
                return;
            }

            IntStream.range(0, 45).mapToObj(inventory::slot).filter(Optional::isPresent).map(Optional::get).forEach(slot -> {
                ServerWorld world = p.world();
                Entity item = world.createEntity(EntityTypes.ITEM, p.position());
                item.offer(Keys.ITEM_STACK_SNAPSHOT, slot.peek().createSnapshot());
            });
        };

        setButton(47, SpongeIconBuilder.of(ItemTypes.CHEST, Component.text("Set Sale Filter(s)")), ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), FishShopFilterGui::new));
        updateSlots();
        IntStream.of(46, 48, 50, 52).forEach(this::glassPaneButton);
    }

    private static Config<SpongeTextFormat, SpongeTextListFormat, Component> getConfig() {
        return getPlugin().getConfig();
    }

    private static FishItemStackConverter getConverter() {
        return getPlugin().getConverter();
    }

    private static LangConfig<SpongeTextFormat, SpongeTextListFormat, Component> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private static SpongeMoreFish getPlugin() {
        return SpongeMoreFish.getInstance();
    }

    private List<ItemStack> getFilteredFish() {
        return IntStream.range(0, 45).mapToObj(inventory::slot).filter(Optional::isPresent).map(Optional::get).map(Inventory::peek).filter(getConverter()::isFish).filter(itemStack -> getShop().priceOf(getConverter().fish(itemStack)) >= 0).filter(itemStack -> selectedRarities.contains(getConverter().fish(itemStack).getType().getRarity())).collect(Collectors.toList());
    }

    private FishShopConfig getShop() {
        return getConfig().getFishShopConfig();
    }

    private double getTotalPrice() {
        return getFilteredFish().stream().mapToDouble(itemStack -> {
            Fish fish = getConverter().fish(itemStack);
            return getShop().priceOf(fish) * itemStack.quantity();
        }).sum();
    }

    private void updatePriceIcon() {
        updatePriceIcon(getTotalPrice());
    }

    private void updatePriceIcon(double price) {
        setButton(49, SpongeIconBuilder.of(ItemTypes.EMERALD, getLangConfig().format("shop-emerald-icon-name").replace(ImmutableMap.of("%price%", String.valueOf(price))).output()), ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
            List<ItemStack> filteredFish = getFilteredFish();
            if (filteredFish.isEmpty()) {
                p.sendMessage(getLangConfig().text("shop-no-fish"));
            }
            else {
                double totalPrice = getTotalPrice();
                List<Fish> fishList = new ArrayList<>();
                filteredFish.forEach(itemStack -> {
                    IntStream.range(0, itemStack.quantity()).forEach(i -> {
                        Fish fish = getConverter().fish(itemStack);
                        fishList.add(fish);
                    });

                    itemStack.setQuantity(0);
                });

                SpongeEconomyHooker economyHooker = SpongeMoreFish.getInstance().getEconomy();
                if (!economyHooker.hasHooked()) {
                    throw new IllegalStateException("An economy service must be hooked for fish shop feature");
                }

                if (!economyHooker.hasEconomy()) {
                    throw new IllegalStateException("Economy hooker doesn't have an economy service");
                }

                EconomyService economy = economyHooker.getEconomy();
                economy.findOrCreateAccount(p.uniqueId()).ifPresent(account -> account.deposit(economy.defaultCurrency(), BigDecimal.valueOf(fishList.stream().mapToDouble(SpongeMoreFish.getInstance().getConfig().getFishShopConfig()::priceOf).sum())));
                updatePriceIcon(totalPrice);
                p.sendMessage(getLangConfig().format("shop-sold").replace(ImmutableMap.of("%price%", totalPrice)).output());
            }
        }));
    }

    private void updateSlots() {
        FishBags<ItemStack> fishBags = SpongeMoreFish.getInstance().getFishBags();
        Scheduler scheduler = Sponge.asyncScheduler();
        scheduler.submit(Task.builder().delay(Ticks.of(1)).execute(() -> fishBags.getFish(player.uniqueId(), page).forEach(inventory::offer)).plugin(SpongeMoreFish.getInstance().getPluginContainer()).build());
        scheduler.submit(Task.builder().delay(Ticks.of(2)).execute((Runnable) this::updatePriceIcon).plugin(SpongeMoreFish.getInstance().getPluginContainer()).build());
        int userMaxAllowedPages = fishBags.getMaxAllowedPages(player.uniqueId());
        if (page == 1) {
            glassPaneButton(45);
        }
        else {
            setButton(45, PREVIOUS, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
                page--;
                updateSlots();
            }));
        }

        if (page < userMaxAllowedPages) {
            setButton(53, NEXT, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
                page++;
                updateSlots();
            }));
        }
        else {
            glassPaneButton(53);
        }

        Task.builder().delay(Ticks.of(3)).execute(() -> updateUpgradeIcon(userMaxAllowedPages)).plugin(SpongeMoreFish.getInstance().getPluginContainer());
    }

    private void updateUpgradeIcon(int userMaxAllowedPages) {
        Map<Integer, Integer> upgrades = SpongeMoreFish.getInstance().getConfig().getFishBagUpgrades();
        List<Entry<Integer, Integer>> upgradeEntries = upgrades.entrySet().stream().filter(entry -> entry.getKey() > userMaxAllowedPages).sorted(Comparator.comparingInt(Entry::getKey)).collect(Collectors.toList());

        if (upgradeEntries.isEmpty()) {
            glassPaneButton(51);
            return;
        }

        Entry<Integer, Integer> upgrade = upgradeEntries.get(0);
        ItemStack icon = SpongeIconBuilder.builder(ItemTypes.GOLD_INGOT).name(Component.text("Bag Upgrades")).description(Component.text(upgrade.getKey() + " page(s) for $" + upgrade.getValue(), NamedTextColor.GREEN)).build();
        setButton(51, icon, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
            SpongeMoreFish plugin = SpongeMoreFish.getInstance();
            EconomyService economy = plugin.getEconomy().getEconomy();
            ResultType result = economy.findOrCreateAccount(p.uniqueId()).map(account -> account.withdraw(economy.defaultCurrency(), BigDecimal.valueOf(upgrade.getValue())).result()).orElse(ResultType.FAILED);
            if (result == ResultType.SUCCESS) {
                plugin.getFishBags().setMaxAllowedPages(p.uniqueId(), upgrade.getKey());
                Task.builder().delay(Ticks.of(2)).execute(() -> updateUpgradeIcon(upgrade.getKey())).plugin(SpongeMoreFish.getInstance().getPluginContainer());
                return;
            }

            p.sendMessage(Component.join(Component.text(), Component.text("[MoreFish]", NamedTextColor.AQUA), Component.text(" You do not have enough money for that upgrade!", NamedTextColor.WHITE)));
        }));
    }
}
