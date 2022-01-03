package io.musician101.morefish.sponge.fishing;

import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishBags;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCompetitionRecordAdder;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeNewFirstBroadcaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.projectile.FishingBobber;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.FishingEvent.Stop;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.server.ServerWorld;

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
                return event.fishHook().targetEntity().map(Value::get).filter(Item.class::isInstance).map(Item.class::cast).map(Item::item).map(Value::get).map(ItemStackSnapshot::type).filter(itemType -> itemType.isAnyOf(ItemTypes.COD.get(), ItemTypes.SALMON.get(), ItemTypes.TROPICAL_FISH.get(), ItemTypes.PUFFERFISH.get())).isPresent();
            }

            return true;
        });
    }

    private boolean canReplaceVanillaFishing(Stop event) {
        return replacingVanillaConditions.stream().anyMatch(it -> it.test(event));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private Collection<CatchHandler> catchHandlersOf(UUID player, Fish fish) {
        List<CatchHandler> catchHandlers = new ArrayList<>(getPlugin().getGlobalCatchHandlers());
        catchHandlers.addAll(fish.getType().getCatchHandlers());
        Server server = Sponge.server();
        List<ServerWorld> contestDisabledWorlds = getConfig().getDisabledWorlds().stream().map(ResourceKey::resolve).map(server.worldManager()::world).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        if (contestDisabledWorlds.contains(server.player(player).get().world())) {
            return catchHandlers.stream().filter(catchHandler -> !(catchHandler instanceof SpongeCompetitionRecordAdder) && !(catchHandler instanceof SpongeNewFirstBroadcaster)).collect(Collectors.toList());
        }

        return catchHandlers;
    }

    private FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    private Config<SpongeTextFormat, SpongeTextListFormat, Component> getConfig() {
        return getPlugin().getConfig();
    }

    private SpongeMoreFish getPlugin() {
        return SpongeMoreFish.getInstance();
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public final void onPlayerFish(@Nonnull Stop event, @First ServerPlayer player, @Getter("fishHook") FishingBobber fishingBobber) {
        fishingBobber.targetEntity().map(Value::get).filter(Item.class::isInstance).map(Item.class::cast).ifPresent(caught -> {
            if (getCompetition().isDisabled()) {
                if (getConfig().noFishingUnlessContest()) {
                    event.setCancelled(true);
                    player.sendMessage(SpongeMoreFish.getInstance().getConfig().getLangConfig().text("no-fishing-allowed"));
                }
            }
            else if (canReplaceVanillaFishing(event)) {
                Fish fish = getPlugin().getConfig().getFishConfig().pickRandomType(caught.uniqueId(), player.uniqueId()).generateFish();
                catchHandlersOf(player.uniqueId(), fish).forEach(handler -> handler.handle(player.uniqueId(), fish));
                FishBags<ItemStack> fishBags = SpongeMoreFish.getInstance().getFishBags();
                ItemStack itemStack = getPlugin().getConverter().createItemStack(fish, player.uniqueId());
                if (fishBags.addFish(player.uniqueId(), itemStack)) {
                    caught.remove();
                    return;
                }

                caught.offer(Keys.ITEM_STACK_SNAPSHOT, itemStack.createSnapshot());
            }
        });
    }
}
