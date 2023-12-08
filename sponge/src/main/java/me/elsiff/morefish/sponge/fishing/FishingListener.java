package me.elsiff.morefish.sponge.fishing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import me.elsiff.morefish.common.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.sponge.fishing.catchhandler.CompetitionRecordAdder;
import me.elsiff.morefish.sponge.fishing.catchhandler.NewFirstBroadcaster;
import me.elsiff.morefish.sponge.fishing.catchhandler.SpongeCatchBroadcaster;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.projectile.FishingBobber;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldManager;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.PREFIX;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static me.elsiff.morefish.sponge.item.FishItemStackConverter.createItemStack;
import static net.kyori.adventure.text.Component.text;

public final class FishingListener {

    private final SpongeFishingCompetition competition = getPlugin().getCompetition();
    private final SpongeFishTypeTable fishTypeTable = getPlugin().getFishTypeTable();

    private boolean canReplaceVanillaFishing(Item caught) {
        if (getConfig().node("general.only-for-contest").getBoolean()) {
            return competition.isEnabled();
        }

        if (getConfig().node("general.replace-only-fish").getBoolean()) {
            if (caught != null) {
                ItemType type = caught.item().get().type();
                return Stream.of(ItemTypes.COD, ItemTypes.SALMON, ItemTypes.PUFFERFISH, ItemTypes.TROPICAL_FISH).anyMatch(m -> m.get().equals(type));
            }
        }

        return true;
    }

    private Collection<CatchHandler<SpongeFish, ServerPlayer>> catchHandlersOf(ServerPlayer player, SpongeFish fish) {
        List<CatchHandler<SpongeFish, ServerPlayer>> catchHandlers = new ArrayList<>(List.of(new SpongeCatchBroadcaster(), new NewFirstBroadcaster(), new CompetitionRecordAdder()));
        catchHandlers.addAll(fish.type().catchHandlers());
        List<ServerWorld> contestDisabledWorlds;
        try {
            contestDisabledWorlds = getConfig().node("general.contest-disabled-worlds").getList(String.class, List.of()).stream().map(UUID::fromString).map(uuid -> {
                WorldManager wm = Sponge.server().worldManager();
                return wm.worldKey(uuid).flatMap(wm::world);
            }).filter(Optional::isPresent).map(Optional::get).toList();
        }
        catch (SerializationException e) {
            throw new RuntimeException(e);
        }

        if (contestDisabledWorlds.contains(player.world())) {
            return catchHandlers.stream().filter(catchHandler -> !(catchHandler instanceof CompetitionRecordAdder) && !(catchHandler instanceof NewFirstBroadcaster)).toList();
        }

        return catchHandlers;
    }

    private ConfigurationNode getConfig() {
        return getPlugin().getConfig();
    }

    @Listener(beforeModifications = true, order = Order.FIRST)
    public void onPlayerFish(@NotNull FishingEvent.Stop event) {
        FishingBobber bobber = event.fishHook();
        bobber.targetEntity().map(Value.Mutable::get).filter(Item.class::isInstance).map(Item.class::cast).ifPresent(caught -> bobber.shooter().map(Value.Mutable::get).filter(ServerPlayer.class::isInstance).map(ServerPlayer.class::cast).ifPresent(player -> {
            if (competition.isDisabled()) {
                if (getConfig().node("general.no-fishing-unless-contest").getBoolean()) {
                    event.setCancelled(true);
                    player.sendMessage(join(PREFIX, text("You can't fish unless the contest is ongoing.")));
                }
            }
            else if (canReplaceVanillaFishing(caught)) {
                SpongeFish fish = fishTypeTable.pickRandomType(caught, player, competition).generateFish();
                catchHandlersOf(player, fish).forEach(handler -> handler.handle(player, fish));
                SpongeFishBags fishBags = getPlugin().getFishBags();
                ItemStack itemStack = createItemStack(fish, player);
                if (fishBags.addFish(player.uniqueId(), itemStack)) {
                    caught.remove();
                    return;
                }

                caught.offer(caught.item().set(itemStack.createSnapshot()));
            }
        }));
    }
}
