package me.elsiff.morefish.sponge.shop;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.elsiff.morefish.common.fishing.FishRarity;
import me.elsiff.morefish.sponge.SpongeMoreFish;
import me.elsiff.morefish.sponge.fishing.SpongeFish;
import me.elsiff.morefish.sponge.fishing.SpongeFishBags;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import me.elsiff.morefish.sponge.hooker.EconomyHooker;
import me.elsiff.morefish.sponge.item.FishItemStackConverter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.menu.ClickTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.configurate.ConfigurationNode;

import static io.musician101.musigui.sponge.chest.SpongeIconUtil.customName;
import static io.musician101.musigui.sponge.chest.SpongeIconUtil.setLore;
import static me.elsiff.morefish.common.configuration.Lang.SHOP_GUI_TITLE;
import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.PREFIX;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.lang;
import static me.elsiff.morefish.sponge.item.FishItemStackConverter.fish;
import static me.elsiff.morefish.sponge.item.FishItemStackConverter.isFish;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

public final class FishShopGui extends AbstractFishShopGUI {

    @NotNull private final SpongeFishBags fishBags;
    @NotNull private final List<FishRarity<SpongeFishingCompetition, SpongeFish, Item, ServerPlayer>> selectedRarities;
    private int page;

    //TODO need to check if this is needed in Sponge version
    /*@Override
    protected void handleExtraDrag(InventoryDragEvent event) {
        inventory.setItem(49, null);
        updatePriceIcon();
    }*/

    public FishShopGui(@NotNull ServerPlayer user, int page) {
        super(SHOP_GUI_TITLE, user);
        this.page = page;
        this.selectedRarities = FishShopFilterGui.FILTERS.getOrDefault(user.uniqueId(), new ArrayList<>());
        this.fishBags = getPlugin().getFishBags();
        this.extraClickHandler = (cause, container, slot, slotIndex, clickType) -> {
            ItemStack itemStack = slot.peek();
            if (!isFish(itemStack)) {
                return false;
            }

            updatePriceIcon();
            fishBags.update(player.uniqueId(), inventory.slots().stream().map(Inventory::peek).toArray(ItemStack[]::new), page);
            return true;
        };
        this.extraCloseHandler = (cause, container) -> cause.first(ServerPlayer.class).ifPresent(player -> {
            SpongeFishBags fishBags = getPlugin().getFishBags();
            if (fishBags.getMaxAllowedPages(player.uniqueId()) > 0) {
                fishBags.update(player.uniqueId(), inventory.slots().stream().map(Inventory::peek).toArray(ItemStack[]::new), page);
                return;
            }

            IntStream.range(0, 45).mapToObj(inventory::slot).filter(Optional::isPresent).map(Optional::get).map(Inventory::peek).forEach(itemStack -> {
                ServerWorld world = player.world();
                Item item = world.createEntity(EntityTypes.ITEM, player.position());
                item.offer(item.item().set(itemStack.copy().createSnapshot()));
                world.spawnEntity(item);
            });
        });
        updateButtons();
        IntStream.of(46, 48, 50, 52).forEach(this::glassPaneButton);
        setButton(47, customName(ItemStack.of(ItemTypes.CHEST), text("Set Sale Filter(s)")), ClickTypes.CLICK_LEFT, p -> new FishShopFilterGui(1, p));
    }

    private EconomyService getEconomy() {
        EconomyHooker vault = getPlugin().getEconomyHooker();
        if (!vault.hasHooked()) {
            throw new IllegalStateException("Vault must be hooked for fish shop feature");
        }

        if (vault.hasEconomy()) {
            return vault.getEconomy().orElseThrow(() -> new IllegalStateException("Economy must be enabled"));
        }

        throw new IllegalStateException("Vault doesn't have economy plugin");
    }

    private List<ItemStack> getFilteredFish() {
        return IntStream.range(0, 45).mapToObj(i -> inventory.slot(i).map(Inventory::peek)).filter(Optional::isPresent).map(Optional::get).filter(FishItemStackConverter::isFish).filter(itemStack -> shop.priceOf(fish(itemStack)) >= 0).filter(itemStack -> selectedRarities.contains(fish(itemStack).type().rarity())).collect(Collectors.toList());
    }

    private double getTotalPrice() {
        return getFilteredFish().stream().mapToDouble(itemStack -> {
            SpongeFish fish = fish(itemStack);
            return shop.priceOf(fish) * itemStack.quantity();
        }).sum();
    }

    private void updateButtons() {
        Sponge.asyncScheduler().submit(Task.builder().plugin(getPlugin().getPluginContainer()).execute(() -> {
            List<ItemStack> fish = fishBags.getFish(player.uniqueId(), page);
            IntStream.range(0, 45).forEach(i -> {
                removeButton(i);
                if (i < fish.size()) {
                    addItem(i, fish.get(i));
                }
            });

            updatePriceIcon();
            int userMaxAllowedPages = fishBags.getMaxAllowedPages(player.uniqueId());
            if (page == 1) {
                glassPaneButton(45);
            }
            else {
                setButton(45, customName(ItemStack.of(ItemTypes.ARROW), text("Back Page")), ClickTypes.CLICK_LEFT, p -> {
                    page--;
                    updateButtons();
                });
            }

            if (page < userMaxAllowedPages) {
                setButton(53, customName(ItemStack.of(ItemTypes.ARROW), text("Next Page")), ClickTypes.CLICK_LEFT, p -> {
                    page++;
                    updateButtons();
                });
            }
            else {
                glassPaneButton(53);
            }

            updateUpgradeIcon(userMaxAllowedPages);
        }).delay(Ticks.of(2)).build());
    }

    private void updatePriceIcon() {
        updatePriceIcon(getTotalPrice());
    }

    private void updatePriceIcon(double price) {
        Sponge.asyncScheduler().submit(Task.builder().plugin(getPlugin().getPluginContainer()).execute(() -> {
            Component name = lang().replace(text("Sell for $%price%", GREEN), Map.of("%price%", String.valueOf(price)));
            setButton(49, customName(ItemStack.of(ItemTypes.EMERALD), name), ClickTypes.CLICK_LEFT, p -> {
                List<ItemStack> filteredFish = getFilteredFish();
                if (filteredFish.isEmpty()) {
                    p.sendMessage(join(PREFIX, text("There's no fish to sell. Please put them on the slots.")));
                }
                else {
                    double totalPrice = getTotalPrice();
                    List<SpongeFish> fishList = new ArrayList<>();
                    filteredFish.forEach(itemStack -> {
                        SpongeFish fish = fish(itemStack);
                        for (int i = 0; i < itemStack.quantity(); i++) {
                            fishList.add(fish);
                        }

                        itemStack.setQuantity(0);
                    });

                    getEconomy().findOrCreateAccount(player.uniqueId()).ifPresent(account -> account.deposit(getEconomy().defaultCurrency(), BigDecimal.valueOf(fishList.stream().mapToDouble(shop::priceOf).sum())));
                    fishBags.update(player.uniqueId(), inventory.slots().stream().map(Inventory::peek).toArray(ItemStack[]::new), page);
                    updatePriceIcon(totalPrice);
                    p.sendMessage(lang().replace(join(PREFIX, text("You sold fish for "), text("$%price%", GREEN), text(".")), Map.of("%price%", totalPrice)));
                }
            });
        }).delay(Ticks.zero()).build());
    }

    private void updateUpgradeIcon(int userMaxAllowedPages) {
        ConfigurationNode upgrades = getPlugin().getConfig().node("fish-bag-upgrades");
        if (upgrades != null) {
            List<SimpleEntry<Integer, Integer>> upgradeEntries = upgrades.childrenMap().entrySet().stream().map(e -> {
                String key = e.getKey().toString();
                int maxAllowedPages = Integer.parseInt(key);
                int price = e.getValue().getInt();
                return new SimpleEntry<>(maxAllowedPages, price);
            }).filter(entry -> entry.getKey() > userMaxAllowedPages).sorted(Comparator.comparingInt(Entry::getKey)).toList();

            if (upgradeEntries.isEmpty()) {
                glassPaneButton(51);
                return;
            }

            Entry<Integer, Integer> upgrade = upgradeEntries.get(0);
            Component lore = text(upgrade.getKey() + " page(s) for $" + upgrade.getValue(), GREEN);
            ItemStack icon = setLore(customName(ItemStack.of(ItemTypes.GOLD_INGOT), text("Bag Upgrades")), lore);
            setButton(51, icon, ClickTypes.CLICK_LEFT, p -> {
                SpongeMoreFish plugin = getPlugin();
                if (plugin.getEconomyHooker().getEconomy().flatMap(economy -> economy.findOrCreateAccount(p.uniqueId()).filter(account -> account.withdraw(economy.defaultCurrency(), BigDecimal.valueOf(upgrade.getValue())).result().equals(ResultType.SUCCESS))).isPresent()) {
                    plugin.getFishBags().setMaxAllowedPages(p.uniqueId(), upgrade.getKey());
                    updateUpgradeIcon(upgrade.getKey());
                    return;
                }

                p.sendMessage(join(PREFIX, text("You do not have enough money for that upgrade!")));
            });
        }
    }
}
