package me.elsiff.morefish.fishing;

import me.elsiff.morefish.fishing.catchhandler.CatchBroadcaster;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.catchhandler.CompetitionRecordAdder;
import me.elsiff.morefish.fishing.catchhandler.NewFirstBroadcaster;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.bukkit.Bukkit;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.item.FishItemStackConverter.createItemStack;

public final class FishingListener implements Listener {

    private final FishingCompetition competition = getPlugin().getCompetition();
    private final FishTypeTable fishTypeTable = getPlugin().getFishTypeTable();

    private Collection<CatchHandler> catchHandlersOf(PlayerFishEvent event, Fish fish) {
        List<CatchHandler> catchHandlers = new ArrayList<>(List.of(new CatchBroadcaster(), new NewFirstBroadcaster(), new CompetitionRecordAdder()));
        catchHandlers.addAll(fish.type().catchHandlers());
        List<World> contestDisabledWorlds = getConfig().getStringList("general.contest-disabled-worlds").stream().map(Bukkit::getWorld).filter(Objects::nonNull).toList();
        Player player = event.getPlayer();
        if (contestDisabledWorlds.contains(player.getWorld())) {
            return catchHandlers.stream().filter(catchHandler -> !(catchHandler instanceof CompetitionRecordAdder) && !(catchHandler instanceof NewFirstBroadcaster)).toList();
        }

        return catchHandlers;
    }

    private FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerFish(@NotNull PlayerFishEvent event) {
        if (event.getState() == State.CAUGHT_FISH && event.getCaught() instanceof Item) {
            if (competition.isEnabled()) {
                Player player = event.getPlayer();
                getPlugin().getMusiBoard().addToLeaderboard(player);
                Item caught = (Item) event.getCaught();
                List<Fish> fishes = fishTypeTable.pickRandomTypes(caught, player).stream().map(FishType::generateFish).toList();
                fishes.forEach(fish -> catchHandlersOf(event, fish).forEach(handler -> handler.handle(player, fish)));
                List<ItemStack> fishItems = fishes.stream().map(fish -> createItemStack(fish, player)).toList();
                // There's always one fish
                ItemStack fishItem = fishItems.get(0);
                caught.setItemStack(fishItem);
                fishItems.stream().filter(item -> !getPlugin().getFishBags().addFish(player, item)).forEach(item -> {
                    World world = player.getWorld();
                    world.dropItem(player.getLocation(), item);
                });
                caught.remove();
            }
        }
    }
}
