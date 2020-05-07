package io.musician101.morefish.sponge.shop;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishBags;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchHandler;
import io.musician101.morefish.sponge.fishing.competition.SpongePrize;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import io.musician101.morefish.sponge.gui.MoreFishGUI;
import io.musician101.morefish.sponge.item.FishItemStackConverter;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.SpongeIconBuilder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent.Primary;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public final class FishShopGui extends MoreFishGUI {

    @Nonnull
    private final List<FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>> selectedRarities;
    private int page = 1;

    public FishShopGui(@Nonnull Player user) {
        super(getLangConfig().text("shop-gui-title"), user);
        this.selectedRarities = FishShopFilterGui.filters.getOrDefault(user.getUniqueId(), new ArrayList<>());
        this.extraClickHandler = event -> {
            if (!getConverter().isFish(event.getCursorTransaction().getFinal().createStack())) {
                return;
            }

            Task.builder().delayTicks(4).execute((Runnable) this::updatePriceIcon).submit(SpongeMoreFish.getInstance());
        };
        this.extraCloseHandler = event -> {
            //noinspection OptionalGetWithoutIsPresent
            Player player = event.getCause().first(Player.class).get();
            FishBags<ItemStack> fishBags = getPlugin().getFishBags();
            if (fishBags.getMaxAllowedPages(player.getUniqueId()) > 0) {
                fishBags.update(player.getUniqueId(), StreamSupport.stream(inventory.slots().spliterator(), false).map(Inventory::peek).map(i -> i.orElse(null)).toArray(ItemStack[]::new), page);
                return;
            }

            IntStream.range(0, 45).mapToObj(i -> inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(i))).peek()).filter(Optional::isPresent).map(Optional::get).forEach(itemStack -> {
                World world = player.getWorld();
                Entity item = world.createEntity(EntityTypes.ITEM, player.getPosition());
                item.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
            });
        };

        setButton(47, SpongeIconBuilder.of(ItemTypes.CHEST, Text.of("Set Sale Filter(s)")), ImmutableMap.of(Primary.class, FishShopFilterGui::new));
        updateSlots();
        IntStream.of(46, 48, 50, 52).forEach(this::glassPaneButton);
    }

    private static Config<FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>>, LangConfig<SpongeTextFormat, SpongeTextListFormat, Text>, MessagesConfig<SpongePlayerAnnouncement, BossBarColor>, FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text>, SpongePrize> getConfig() {
        return getPlugin().getConfig();
    }

    private static FishItemStackConverter getConverter() {
        return getPlugin().getConverter();
    }

    private static LangConfig<SpongeTextFormat, SpongeTextListFormat, Text> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private static SpongeMoreFish getPlugin() {
        return SpongeMoreFish.getInstance();
    }

    private List<ItemStack> getFilteredFish() {
        return IntStream.range(0, 45).<Inventory>mapToObj(i -> inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(new SlotIndex(i)))).map(Inventory::peek).filter(Optional::isPresent).map(Optional::get).filter(getConverter()::isFish).filter(itemStack -> getShop().priceOf(getConverter().fish(itemStack)) >= 0).filter(itemStack -> selectedRarities.contains(getConverter().fish(itemStack).getType().getRarity())).collect(Collectors.toList());
    }

    private FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text> getShop() {
        return getConfig().getFishShopConfig();
    }

    private double getTotalPrice() {
        return getFilteredFish().stream().mapToDouble(itemStack -> {
            Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish = getConverter().fish(itemStack);
            return getShop().priceOf(fish) * itemStack.getQuantity();
        }).sum();
    }

    private void updatePriceIcon() {
        updatePriceIcon(getTotalPrice());
    }

    private void updatePriceIcon(double price) {
        setButton(49, SpongeIconBuilder.of(ItemTypes.EMERALD, getLangConfig().format("shop-emerald-icon-name").replace(ImmutableMap.of("%price%", String.valueOf(price))).output()), ImmutableMap.of(Primary.class, p -> {
            List<ItemStack> filteredFish = getFilteredFish();
            if (filteredFish.isEmpty()) {
                p.sendMessage(getLangConfig().text("shop-no-fish"));
            }
            else {
                double totalPrice = getTotalPrice();
                List<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>> fishList = new ArrayList<>();
                filteredFish.forEach(itemStack -> {
                    Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish = getConverter().fish(itemStack);
                    for (int i = 0; i < itemStack.getQuantity(); i++) {
                        fishList.add(fish);
                    }

                    itemStack.setQuantity(0);
                });

                getShop().sell(p, fishList);
                updatePriceIcon(totalPrice);
                p.sendMessage(getLangConfig().format("shop-sold").replace(ImmutableMap.of("%price%", totalPrice)).output());
            }
        }));
    }

    private void updateSlots() {
        FishBags<ItemStack> fishBags = SpongeMoreFish.getInstance().getFishBags();
        Task.builder().delayTicks(1).execute(() -> fishBags.getFish(player.getUniqueId(), page).forEach(inventory::offer)).submit(SpongeMoreFish.getInstance());
        Task.builder().delayTicks(2).execute((Runnable) this::updatePriceIcon).submit(SpongeMoreFish.getInstance());
        int userMaxAllowedPages = fishBags.getMaxAllowedPages(player.getUniqueId());
        if (page == 1) {
            glassPaneButton(45);
        }
        else {
            setButton(45, PREVIOUS, ImmutableMap.of(Primary.class, p -> {
                page--;
                updateSlots();
            }));
        }

        if (page < userMaxAllowedPages) {
            setButton(53, NEXT, ImmutableMap.of(Primary.class, p -> {
                page++;
                updateSlots();
            }));
        }
        else {
            glassPaneButton(53);
        }

        Task.builder().delayTicks(3).execute(() -> updateUpgradeIcon(userMaxAllowedPages)).submit(SpongeMoreFish.getInstance());
    }

    private void updateUpgradeIcon(int userMaxAllowedPages) {
        Map<Integer, Integer> upgrades = SpongeMoreFish.getInstance().getConfig().getFishBagUpgrades();
        List<Entry<Integer, Integer>> upgradeEntries = upgrades.entrySet().stream().filter(entry -> entry.getKey() > userMaxAllowedPages).sorted(Comparator.comparingInt(Entry::getKey)).collect(Collectors.toList());

        if (upgradeEntries.isEmpty()) {
            glassPaneButton(51);
            return;
        }

        Entry<Integer, Integer> upgrade = upgradeEntries.get(0);
        ItemStack icon = SpongeIconBuilder.builder(ItemTypes.GOLD_INGOT).name(Text.of("Bag Upgrades")).description(Text.of(TextColors.GREEN, upgrade.getKey(), " page(s) for $" + upgrade.getValue())).build();
        setButton(51, icon, ImmutableMap.of(Primary.class, p -> {
            SpongeMoreFish plugin = SpongeMoreFish.getInstance();
            EconomyService economy = plugin.getEconomy().getEconomy();
            //noinspection ConstantConditions
            ResultType result = economy.getOrCreateAccount(p.getUniqueId()).map(account -> account.withdraw(economy.getDefaultCurrency(), BigDecimal.valueOf(upgrade.getValue()), Cause.of(EventContext.builder().add(EventContextKeys.PLAYER, p).add(EventContextKeys.PLUGIN, SpongeMoreFish.getInstance().getPluginContainer()).build(), p)).getResult()).orElse(ResultType.FAILED);
            if (result == ResultType.SUCCESS) {
                plugin.getFishBags().setMaxAllowedPages(p.getUniqueId(), upgrade.getKey());
                Task.builder().delayTicks(2).execute(() -> updateUpgradeIcon(upgrade.getKey())).submit(SpongeMoreFish.getInstance());
                return;
            }

            p.sendMessage(Text.of(TextColors.AQUA + "[MoreFish]" + TextColors.RESET + " You do not have enough money for that upgrade!"));
        }));
    }
}
