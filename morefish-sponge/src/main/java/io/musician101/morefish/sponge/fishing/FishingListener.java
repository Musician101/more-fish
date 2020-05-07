package io.musician101.morefish.sponge.fishing;

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
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchHandler;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCompetitionRecordAdder;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeNewFirstBroadcaster;
import io.musician101.morefish.sponge.fishing.competition.SpongePrize;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.FishingEvent.Stop;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.World;

public final class FishingListener {

    private final List<Predicate<Stop>> replacingVanillaConditions;

    public FishingListener() {
        this.replacingVanillaConditions = Arrays.asList(event -> {
            if (getConfig().onlyForContest()) {
                return getCompetition().isEnabled();
            }

            return true;
        }, event -> {
            if (getConfig().replaceOnlyFish()) {
                return event.getFishHook().getHookedEntity().filter(Item.class::isInstance).map(Item.class::cast).map(Item::getItemType).filter(itemType -> itemType == ItemTypes.FISH).isPresent();
            }

            return true;
        });
    }

    private boolean canReplaceVanillaFishing(Stop event) {
        return replacingVanillaConditions.stream().anyMatch(it -> it.test(event));
    }

    private Collection<SpongeCatchHandler> catchHandlersOf(Player player, Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish) {
        List<SpongeCatchHandler> catchHandlers = new ArrayList<>(getPlugin().getGlobalCatchHandlers());
        catchHandlers.addAll(fish.getType().getCatchHandlers());
        List<World> contestDisabledWorlds = getConfig().getDisabledWorlds().stream().map(Sponge.getServer()::getWorld).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        if (contestDisabledWorlds.contains(player.getWorld())) {
            return catchHandlers.stream().filter(catchHandler -> !(catchHandler instanceof SpongeCompetitionRecordAdder) && !(catchHandler instanceof SpongeNewFirstBroadcaster)).collect(Collectors.toList());
        }

        return catchHandlers;
    }

    private FishingCompetition<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> getCompetition() {
        return getPlugin().getCompetition();
    }

    private Config<FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>>, LangConfig<SpongeTextFormat, SpongeTextListFormat, Text>, MessagesConfig<SpongePlayerAnnouncement, BossBarColor>, FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text>, SpongePrize> getConfig() {
        return getPlugin().getConfig();
    }

    private SpongeMoreFish getPlugin() {
        return SpongeMoreFish.getInstance();
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public final void onPlayerFish(@Nonnull Stop event, @First Player player) {
        event.getFishHook().getHookedEntity().filter(Item.class::isInstance).map(Item.class::cast).ifPresent(caught -> {
            if (getCompetition().isDisabled()) {
                if (getConfig().noFishingUnlessContest()) {
                    event.setCancelled(true);
                    player.sendMessage(SpongeMoreFish.getInstance().getConfig().getLangConfig().text("no-fishing-allowed"));
                }
            }
            else if (canReplaceVanillaFishing(event)) {
                Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish = getPlugin().getConfig().getFishConfig().pickRandomType(caught, player).generateFish();
                catchHandlersOf(player, fish).forEach(handler -> handler.handle(player, fish));
                FishBags<ItemStack> fishBags = SpongeMoreFish.getInstance().getFishBags();
                ItemStack itemStack = getPlugin().getConverter().createItemStack(fish, player);
                if (fishBags.addFish(player.getUniqueId(), itemStack)) {
                    caught.remove();
                    return;
                }

                caught.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
            }
        });
    }
}
