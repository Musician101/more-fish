package me.elsiff.morefish.fishing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import me.elsiff.morefish.fishing.catchhandler.CompetitionRecordAdder;
import me.elsiff.morefish.fishing.catchhandler.NewFirstBroadcaster;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.item.FishItemStackConverter;
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

public final class FishingListener implements Listener {

    private final FishingCompetition competition;
    private final FishItemStackConverter converter;
    private final List<Material> fishMaterials = Arrays.asList(Material.COD, Material.SALMON, Material.PUFFERFISH, Material.TROPICAL_FISH);
    private final FishTypeTable fishTypeTable;
    private final List<CatchHandler> globalCatchHandlers;
    private final List<Predicate<PlayerFishEvent>> replacingVanillaConditions;

    public FishingListener(@Nonnull FishTypeTable fishTypeTable, @Nonnull FishItemStackConverter converter, @Nonnull FishingCompetition competition, @Nonnull List<CatchHandler> globalCatchHandlers) {
        this.fishTypeTable = fishTypeTable;
        this.converter = converter;
        this.competition = competition;
        this.globalCatchHandlers = globalCatchHandlers;
        this.replacingVanillaConditions = Arrays.asList(event -> {
            if (Config.INSTANCE.getStandard().getBoolean("general.only-for-contest")) {
                return competition.isEnabled();
            }

            return true;
        }, event -> {
            if (Config.INSTANCE.getStandard().getBoolean("general.replace-only-fish")) {
                return fishMaterials.contains(((Item) event.getCaught()).getItemStack().getType());
            }

            return true;
        });
    }

    private boolean canReplaceVanillaFishing(PlayerFishEvent event) {
        return replacingVanillaConditions.stream().anyMatch(it -> it.test(event));
    }

    private Collection<CatchHandler> catchHandlersOf(PlayerFishEvent event, Fish fish) {
        List<CatchHandler> catchHandlers = new ArrayList<>(globalCatchHandlers);
        catchHandlers.addAll(fish.getType().getCatchHandlers());
        List<World> contestDisabledWorlds = Config.INSTANCE.getStandard().getStringList("general.contest-disabled-worlds").stream().map(Bukkit::getWorld).filter(Objects::nonNull).collect(Collectors.toList());
        Player player = event.getPlayer();
        if (contestDisabledWorlds.contains(player.getWorld())) {
            return catchHandlers.stream().filter(catchHandler -> !(catchHandler instanceof CompetitionRecordAdder) && !(catchHandler instanceof NewFirstBroadcaster)).collect(Collectors.toList());
        }

        return catchHandlers;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public final void onPlayerFish(@Nonnull PlayerFishEvent event) {
        if (event.getState() == State.CAUGHT_FISH && event.getCaught() instanceof Item) {
            if (competition.isDisabled()) {
                if (Config.INSTANCE.getStandard().getBoolean("general.no-fishing-unless-contest")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Lang.INSTANCE.text("no-fishing-allowed"));
                }
            }
            else if (canReplaceVanillaFishing(event)) {
                Item caught = (Item) event.getCaught();
                Fish fish = fishTypeTable.pickRandomType(caught, event.getPlayer(), competition).generateFish();
                catchHandlersOf(event, fish).forEach(handler -> handler.handle(event.getPlayer(), fish));
                caught.setItemStack(converter.createItemStack(fish, event.getPlayer()));
            }
        }
    }
}
