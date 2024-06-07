package me.elsiff.morefish.fish;

import me.elsiff.morefish.fish.catchhandler.CatchBroadcaster;
import me.elsiff.morefish.fish.catchhandler.CatchHandler;
import me.elsiff.morefish.fish.catchhandler.CompetitionRecordAdder;
import me.elsiff.morefish.fish.catchhandler.NewFirstBroadcaster;
import me.elsiff.morefish.competition.FishingCompetition;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class FishingListener implements Listener {

    private final FishingCompetition competition = getPlugin().getCompetition();
    private final FishTypeTable fishTypeTable = getPlugin().getFishTypeTable();

    private Collection<CatchHandler> catchHandlersOf(PlayerFishEvent event, Fish fish) {
        List<CatchHandler> catchHandlers = new ArrayList<>(List.of(new CatchBroadcaster(), new NewFirstBroadcaster(), new CompetitionRecordAdder()));
        catchHandlers.addAll(fish.type().catchHandlers());
        List<World> contestDisabledWorlds = getPlugin().getConfig().getStringList("general.contest-disabled-worlds").stream().map(Bukkit::getWorld).filter(Objects::nonNull).toList();
        Player player = event.getPlayer();
        if (contestDisabledWorlds.contains(player.getWorld())) {
            return catchHandlers.stream().filter(catchHandler -> !(catchHandler instanceof CompetitionRecordAdder) && !(catchHandler instanceof NewFirstBroadcaster)).toList();
        }

        return catchHandlers;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerFish(@NotNull PlayerFishEvent event) {
        if (event.getState() == State.CAUGHT_FISH && event.getCaught() instanceof Item) {
            if (competition.isEnabled()) {
                Player player = event.getPlayer();
                getPlugin().getMusiBoard().addToLeaderboard(player);
                Item caught = (Item) event.getCaught();
                getPlugin().getFishTypeTable().caughtFish(caught, player, true);
            }
        }
    }
}
