package me.elsiff.morefish.fishing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.catchhandler.CompetitionRecordAdder;
import me.elsiff.morefish.fishing.catchhandler.NewFirstBroadcaster;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

import static me.elsiff.morefish.item.FishItemStackConverter.createItemStack;

public final class FishingListener implements Listener {

    private final List<Material> fishMaterials = Arrays.asList(Material.COD, Material.SALMON, Material.PUFFERFISH, Material.TROPICAL_FISH);
    private final MoreFish plugin = MoreFish.instance();
    private final FishingCompetition competition = plugin.getCompetition();
    private final List<Predicate<PlayerFishEvent>> replacingVanillaConditions = Arrays.asList(event -> {
        if (getConfig().getBoolean("general.only-for-contest")) {
            return competition.isEnabled();
        }

        return true;
    }, event -> {
        if (getConfig().getBoolean("general.replace-only-fish")) {
            if (event.getCaught() != null) {
                return fishMaterials.contains(((Item) event.getCaught()).getItemStack().getType());
            }
        }

        return true;
    });
    private final FishTypeTable fishTypeTable = plugin.getFishTypeTable();
    private final List<CatchHandler> globalCatchHandlers = plugin.getGlobalCatchHandlers();

    private boolean canReplaceVanillaFishing(PlayerFishEvent event) {
        return replacingVanillaConditions.stream().anyMatch(it -> it.test(event));
    }

    private Collection<CatchHandler> catchHandlersOf(PlayerFishEvent event, Fish fish) {
        List<CatchHandler> catchHandlers = new ArrayList<>(globalCatchHandlers);
        catchHandlers.addAll(fish.getType().getCatchHandlers());
        List<World> contestDisabledWorlds = getConfig().getStringList("general.contest-disabled-worlds").stream().map(Bukkit::getWorld).filter(Objects::nonNull).toList();
        Player player = event.getPlayer();
        if (contestDisabledWorlds.contains(player.getWorld())) {
            return catchHandlers.stream().filter(catchHandler -> !(catchHandler instanceof CompetitionRecordAdder) && !(catchHandler instanceof NewFirstBroadcaster)).collect(Collectors.toList());
        }

        return catchHandlers;
    }

    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFish(@Nonnull PlayerFishEvent event) {
        if (event.getState() == State.CAUGHT_FISH && event.getCaught() instanceof Item) {
            if (competition.isDisabled()) {
                if (getConfig().getBoolean("general.no-fishing-unless-contest")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Lang.NO_FISHING_ALLOWED);
                }
            }
            else if (canReplaceVanillaFishing(event)) {
                Item caught = (Item) event.getCaught();
                Fish fish = fishTypeTable.pickRandomType(caught, event.getPlayer(), competition).generateFish();
                catchHandlersOf(event, fish).forEach(handler -> handler.handle(event.getPlayer(), fish));
                Player player = event.getPlayer();
                FishBags fishBags = plugin.getFishBags();
                ItemStack itemStack = createItemStack(fish, player);
                if (fishBags.addFish(player, itemStack)) {
                    caught.remove();
                    return;
                }

                caught.setItemStack(createItemStack(fish, event.getPlayer()));
            }
        }
    }
}
