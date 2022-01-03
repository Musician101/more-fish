package io.musician101.morefish.spigot.fishing;

import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishBags;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCompetitionRecordAdder;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotNewFirstBroadcaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
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

    @SuppressWarnings("ConstantConditions")
    public FishingListener() {
        this.replacingVanillaConditions = Arrays.asList(event -> {
            if (getConfig().onlyForContest()) {
                return getCompetition().isEnabled();
            }

            return true;
        }, event -> {
            if (getConfig().replaceOnlyFish()) {
                return fishMaterials.contains(((Item) event.getCaught()).getItemStack().getType());
            }

            return true;
        });
    }

    private boolean canReplaceVanillaFishing(PlayerFishEvent event) {
        return replacingVanillaConditions.stream().anyMatch(it -> it.test(event));
    }

    private Collection<CatchHandler> catchHandlersOf(PlayerFishEvent event, Fish fish) {
        List<CatchHandler> catchHandlers = new ArrayList<>(getPlugin().getGlobalCatchHandlers());
        catchHandlers.addAll(fish.getType().getCatchHandlers());
        List<World> contestDisabledWorlds = getConfig().getDisabledWorlds().stream().map(Bukkit::getWorld).filter(Objects::nonNull).toList();
        Player player = event.getPlayer();
        if (contestDisabledWorlds.contains(player.getWorld())) {
            return catchHandlers.stream().filter(catchHandler -> !(catchHandler instanceof SpigotCompetitionRecordAdder) && !(catchHandler instanceof SpigotNewFirstBroadcaster)).collect(Collectors.toList());
        }

        return catchHandlers;
    }

    private FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    private Config<SpigotTextFormat, SpigotTextListFormat, String> getConfig() {
        return getPlugin().getPluginConfig();
    }

    private SpigotMoreFish getPlugin() {
        return SpigotMoreFish.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFish(@Nonnull PlayerFishEvent event) {
        if (event.getState() == State.CAUGHT_FISH && event.getCaught() instanceof Item) {
            if (getCompetition().isDisabled()) {
                if (getConfig().noFishingUnlessContest()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(SpigotMoreFish.getInstance().getPluginConfig().getLangConfig().text("no-fishing-allowed"));
                }
            }
            else if (canReplaceVanillaFishing(event)) {
                Item caught = (Item) event.getCaught();
                UUID uuid = event.getPlayer().getUniqueId();
                Fish fish = getPlugin().getPluginConfig().getFishConfig().pickRandomType(caught.getUniqueId(), uuid).generateFish();
                catchHandlersOf(event, fish).forEach(handler -> handler.handle(uuid, fish));
                FishBags<ItemStack> fishBags = SpigotMoreFish.getInstance().getFishBags();
                ItemStack itemStack = getPlugin().getConverter().createItemStack(fish, uuid);
                if (fishBags.addFish(uuid, itemStack)) {
                    caught.remove();
                    return;
                }

                caught.setItemStack(itemStack);
            }
        }
    }
}
