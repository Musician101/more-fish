package me.elsiff.morefish.fish;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.jspecify.annotations.NullMarked;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public final class FishingListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == State.CAUGHT_FISH && event.getCaught() instanceof Item caught) {
            if (getPlugin().getCompetition().isEnabled()) {
                Player player = event.getPlayer();
                getPlugin().getMusiBoard().addToLeaderboard(player);
                getPlugin().getFishTypeTable().caughtFish(caught, player);
            }
        }
    }
}
