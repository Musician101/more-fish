package io.musician101.morefish.spigot.fishing;

import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishBags;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchHandler;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCompetitionRecordAdder;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotNewFirstBroadcaster;
import io.musician101.morefish.spigot.fishing.competition.SpigotPrize;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

public final class FishingListener implements Listener {

    private final List<Material> fishMaterials = Arrays.asList(Material.COD, Material.SALMON, Material.PUFFERFISH, Material.TROPICAL_FISH);
    private final List<Predicate<PlayerFishEvent>> replacingVanillaConditions;

    public FishingListener() {
        this.replacingVanillaConditions = Arrays.asList(event -> {
            if (getConfig().onlyForContest()) {
                return getCompetition().isEnabled();
            }

            return true;
        }, event -> {
            if (getConfig().replaceOnlyFish()) {
                //noinspection ConstantConditions
                return fishMaterials.contains(((Item) event.getCaught()).getItemStack().getType());
            }

            return true;
        });
    }

    private boolean canReplaceVanillaFishing(PlayerFishEvent event) {
        return replacingVanillaConditions.stream().anyMatch(it -> it.test(event));
    }

    private Collection<SpigotCatchHandler> catchHandlersOf(PlayerFishEvent event, Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish) {
        List<SpigotCatchHandler> catchHandlers = new ArrayList<>(getPlugin().getGlobalCatchHandlers());
        catchHandlers.addAll(fish.getType().getCatchHandlers());
        List<World> contestDisabledWorlds = getConfig().getDisabledWorlds().stream().map(Bukkit::getWorld).filter(Objects::nonNull).collect(Collectors.toList());
        Player player = event.getPlayer();
        if (contestDisabledWorlds.contains(player.getWorld())) {
            return catchHandlers.stream().filter(catchHandler -> !(catchHandler instanceof SpigotCompetitionRecordAdder) && !(catchHandler instanceof SpigotNewFirstBroadcaster)).collect(Collectors.toList());
        }

        return catchHandlers;
    }

    private FishingCompetition<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> getCompetition() {
        return getPlugin().getCompetition();
    }

    private Config<FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>>, LangConfig<SpigotTextFormat, SpigotTextListFormat, String>, MessagesConfig<SpigotPlayerAnnouncement, BarColor>, FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String>, SpigotPrize> getConfig() {
        return getPlugin().getPluginConfig();
    }

    private SpigotMoreFish getPlugin() {
        return SpigotMoreFish.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public final void onPlayerFish(@Nonnull PlayerFishEvent event) {
        if (event.getState() == State.CAUGHT_FISH && event.getCaught() instanceof Item) {
            if (getCompetition().isDisabled()) {
                if (getConfig().noFishingUnlessContest()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(SpigotMoreFish.getInstance().getPluginConfig().getLangConfig().text("no-fishing-allowed"));
                }
            }
            else if (canReplaceVanillaFishing(event)) {
                Item caught = (Item) event.getCaught();
                Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fish = getPlugin().getPluginConfig().getFishConfig().pickRandomType(caught, event.getPlayer()).generateFish();
                catchHandlersOf(event, fish).forEach(handler -> handler.handle(event.getPlayer(), fish));
                Player player = event.getPlayer();
                FishBags<ItemStack> fishBags = SpigotMoreFish.getInstance().getFishBags();
                ItemStack itemStack = getPlugin().getConverter().createItemStack(fish, player);
                if (fishBags.addFish(player.getUniqueId(), itemStack)) {
                    caught.remove();
                    return;
                }

                caught.setItemStack(itemStack);
            }
        }
    }
}
