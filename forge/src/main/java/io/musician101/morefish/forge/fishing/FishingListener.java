package io.musician101.morefish.forge.fishing;

import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishBags;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.morefish.forge.fishing.catchhandler.ForgeCompetitionRecordAdder;
import io.musician101.morefish.forge.fishing.catchhandler.ForgeNewFirstBroadcaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public final class FishingListener {

    private final List<Item> fishMaterials = Arrays.asList(Items.COD, Items.SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH);
    private final List<Predicate<ItemFishedEvent>> replacingVanillaConditions;

    public FishingListener() {
        this.replacingVanillaConditions = Arrays.asList(event -> {
            if (getConfig().onlyForContest()) {
                return getCompetition().isEnabled();
            }

            return true;
        }, event -> {
            if (getConfig().replaceOnlyFish()) {
                return event.getDrops().stream().map(ItemStack::getItem).allMatch(fishMaterials::contains);
            }

            return true;
        });
    }

    private boolean canReplaceVanillaFishing(ItemFishedEvent event) {
        return replacingVanillaConditions.stream().anyMatch(it -> it.test(event));
    }

    private Collection<CatchHandler> catchHandlersOf(ItemFishedEvent event, Fish fish) {
        List<CatchHandler> catchHandlers = new ArrayList<>(getPlugin().getGlobalCatchHandlers());
        catchHandlers.addAll(fish.getType().getCatchHandlers());
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        List<String> disabledWorlds = getConfig().getDisabledWorlds();
        List<DimensionType> contestDisabledWorlds = StreamSupport.stream(server.getWorlds().spliterator(), false).map(world -> disabledWorlds.stream().filter(s -> s.equals(world.getDimensionKey().toString())).map(s -> world.getDimensionType()).findFirst()).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        PlayerEntity player = event.getPlayer();
        if (contestDisabledWorlds.contains(player.getEntityWorld().getDimensionType())) {
            return catchHandlers.stream().filter(catchHandler -> !(catchHandler instanceof ForgeCompetitionRecordAdder) && !(catchHandler instanceof ForgeNewFirstBroadcaster)).collect(Collectors.toList());
        }

        return catchHandlers;
    }

    private FishingCompetition<ItemStack> getCompetition() {
        return getPlugin().getCompetition();
    }

    private Config getConfig() {
        return getPlugin().getPluginConfig();
    }

    private ForgeMoreFish getPlugin() {
        return ForgeMoreFish.getInstance();
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public final void onPlayerFish(@Nonnull ItemFishedEvent event) {
        Entity caughtEntity = event.getHookEntity().func_234607_k_();
        if (caughtEntity instanceof ItemEntity) {
            if (getCompetition().isDisabled()) {
                if (getConfig().noFishingUnlessContest()) {
                    event.setCanceled(true);
                    event.getPlayer().sendMessage(ForgeMoreFish.getInstance().getPluginConfig().getLangConfig().text("no-fishing-allowed"), Util.DUMMY_UUID);
                }
            }
            else if (canReplaceVanillaFishing(event)) {
                UUID player = (ServerPlayerEntity) event.getPlayer();
                ItemEntity caught = (ItemEntity) caughtEntity;
                Fish fish = getPlugin().getPluginConfig().getFishConfig().pickRandomType(caught, player).generateFish();
                catchHandlersOf(event, fish).forEach(handler -> handler.handle(player, fish));
                ItemStack itemStack = getPlugin().getConverter().createItemStack(fish, player);
                FishBags<ItemStack> fishBags = ForgeMoreFish.getInstance().getFishBags();
                if (fishBags.addFish(player.getUniqueID(), itemStack)) {
                    caught.remove();
                    return;
                }

                World world = player.getEntityWorld();
                Vector3d pos = player.getPositionVec();
                ItemEntity item = new ItemEntity(world, pos.x, pos.y, pos.z, itemStack);
                world.addEntity(item);
            }
        }
    }
}
