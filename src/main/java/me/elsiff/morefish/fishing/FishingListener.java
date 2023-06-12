package me.elsiff.morefish.fishing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.catchhandler.CatchBroadcaster;
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

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static me.elsiff.morefish.configuration.Lang.join;
import static me.elsiff.morefish.item.FishItemStackConverter.createItemStack;
import static net.kyori.adventure.text.Component.text;

public final class FishingListener implements Listener {

    private final FishingCompetition competition = getPlugin().getCompetition();
    private final FishTypeTable fishTypeTable = getPlugin().getFishTypeTable();

    private boolean canReplaceVanillaFishing(PlayerFishEvent event) {
        if (getConfig().getBoolean("general.only-for-contest")) {
            return competition.isEnabled();
        }

        if (getConfig().getBoolean("general.replace-only-fish")) {
            if (event.getCaught() != null) {
                Material type = ((Item) event.getCaught()).getItemStack().getType();
                return Stream.of(Material.COD, Material.SALMON, Material.PUFFERFISH, Material.TROPICAL_FISH).anyMatch(m -> m == type);
            }
        }

        return true;
    }

    private Collection<CatchHandler> catchHandlersOf(PlayerFishEvent event, Fish fish) {
        List<CatchHandler> catchHandlers = new ArrayList<>(Arrays.asList(new CatchBroadcaster(), new NewFirstBroadcaster(), new CompetitionRecordAdder()));
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
    public void onPlayerFish(@Nonnull PlayerFishEvent event) {
        if (event.getState() == State.CAUGHT_FISH && event.getCaught() instanceof Item) {
            if (competition.isDisabled()) {
                if (getConfig().getBoolean("general.no-fishing-unless-contest")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(join(PREFIX, text("You can't fish unless the contest is ongoing.")));
                }
            }
            else if (canReplaceVanillaFishing(event)) {
                Player player = event.getPlayer();
                getPlugin().getMusiBoard().addToLeaderboard(player);
                Item caught = (Item) event.getCaught();
                Fish fish = fishTypeTable.pickRandomType(caught, player, competition).generateFish();
                catchHandlersOf(event, fish).forEach(handler -> handler.handle(player, fish));
                FishBags fishBags = getPlugin().getFishBags();
                ItemStack itemStack = createItemStack(fish, player);
                if (fishBags.addFish(player, itemStack)) {
                    caught.remove();
                    return;
                }

                caught.setItemStack(itemStack);
            }
        }
    }
}
