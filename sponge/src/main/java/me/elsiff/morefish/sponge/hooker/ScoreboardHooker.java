package me.elsiff.morefish.sponge.hooker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import me.elsiff.morefish.common.fishing.competition.FishRecord;
import me.elsiff.morefish.common.hooker.PluginHooker;
import me.elsiff.morefish.sponge.fishing.SpongeFish;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.criteria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;

public class ScoreboardHooker extends SpongePluginHooker {

    private final Map<UUID, Scoreboard> previousBoards = new HashMap<>();
    private Objective leaderboard;
    private Scoreboard scoreboard;

    public void addToLeaderboard(@NotNull ServerPlayer player) {
        if (hasHooked) {
            previousBoards.put(player.uniqueId(), player.scoreboard());
            player.setScoreboard(scoreboard);
        }
    }

    public void clear() {
        Sponge.server().onlinePlayers().forEach(this::restorePreviousBoard);
    }

    @Override
    public @NotNull String getPluginName() {
        return "spongeapi";
    }

    @Override
    public void hook() {
        PluginHooker.checkEnabled(this);
        scoreboard = Scoreboard.builder().build();
        hasHooked = true;
    }

    public void restorePreviousBoard(@NotNull ServerPlayer player) {
        if (hasHooked) {
            if (previousBoards.containsKey(player.uniqueId())) {
                player.setScoreboard(previousBoards.get(player.uniqueId()));
            }
        }
    }

    public void swapScoreboards(@NotNull ServerPlayer player) {
        if (previousBoards.containsKey(player.uniqueId())) {
            restorePreviousBoard(player);
        }
        else {
            addToLeaderboard(player);
        }
    }

    public void update() {
        if (leaderboard != null && scoreboard.objective("leaderboard").isPresent()) {
            scoreboard.removeObjective(leaderboard);
        }

        leaderboard = Objective.builder().name("leaderboard").criterion(Criteria.DUMMY).displayName(text("Top 5 Fishers (mm)", AQUA)).objectiveDisplayMode(ObjectiveDisplayModes.INTEGER).build();
        scoreboard.addObjective(leaderboard);
        scoreboard.updateDisplaySlot(leaderboard, DisplaySlots.SIDEBAR);
        List<FishRecord<SpongeFish>> records = getPlugin().getCompetition().getRanking();
        IntStream.range(0, Math.min(5, records.size())).forEach(i -> {
            FishRecord<SpongeFish> record = records.get(i);
            Sponge.server().gameProfileManager().cache().findById(record.fisher()).ifPresent(profile -> leaderboard.findOrCreateScore(text(profile.name().orElse("null"))).setScore((int) (record.fish().length() * 100)));
        });
    }
}
