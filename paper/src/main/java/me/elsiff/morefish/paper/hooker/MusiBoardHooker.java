package me.elsiff.morefish.paper.hooker;

import io.musician101.musiboard.MusiBoard;
import io.musician101.musiboard.scoreboard.MusiScoreboard;
import io.musician101.musiboard.scoreboard.MusiScoreboardManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import me.elsiff.morefish.common.fishing.competition.FishRecord;
import me.elsiff.morefish.common.hooker.PluginHooker;
import me.elsiff.morefish.paper.fishing.PaperFish;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;

public class MusiBoardHooker extends PaperPluginHooker {

    private final Map<UUID, String> previousBoards = new HashMap<>();
    private Objective leaderboard;
    private MusiScoreboard scoreboard;

    public void addToLeaderboard(@NotNull Player player) {
        if (hasHooked) {
            previousBoards.put(player.getUniqueId(), getManager().getScoreboard(player).getName());
            getManager().setScoreboard(player, scoreboard);
        }
    }

    public void clear() {
        Bukkit.getOnlinePlayers().forEach(this::restorePreviousBoard);
    }

    private MusiScoreboardManager getManager() {
        return MusiBoard.getPlugin().getManager();
    }

    @NotNull
    @Override
    public String getPluginName() {
        return "MusiBoard";
    }

    @Override
    public void hook() {
        PluginHooker.checkEnabled(this);
        getManager().registerNewScoreboard("morefish");
        scoreboard = getManager().getScoreboard("morefish").orElseThrow(() -> new IllegalStateException("Ok, who broke it?"));
        hasHooked = true;
    }

    public void restorePreviousBoard(@NotNull Player player) {
        if (hasHooked) {
            if (previousBoards.containsKey(player.getUniqueId())) {
                MusiScoreboard scoreboard = getManager().getScoreboardOrDefaultOrVanilla(previousBoards.get(player.getUniqueId()));
                getManager().setScoreboard(player, scoreboard);
            }
        }
    }

    public void swapScoreboards(@NotNull Player player) {
        if (previousBoards.containsKey(player.getUniqueId())) {
            restorePreviousBoard(player);
        }
        else {
            addToLeaderboard(player);
        }
    }

    public void update() {
        if (leaderboard != null && scoreboard.getObjective("leaderboard") != null) {
            leaderboard.unregister();
        }

        leaderboard = scoreboard.registerNewObjective("leaderboard", Criteria.DUMMY, text("Top 5 Fishers (mm)", AQUA));
        leaderboard.setDisplaySlot(DisplaySlot.SIDEBAR);
        List<FishRecord<PaperFish>> records = getPlugin().getCompetition().getRanking();
        IntStream.range(0, Math.min(5, records.size())).forEach(i -> {
            FishRecord<PaperFish> record = records.get(i);
            leaderboard.getScore(Bukkit.getOfflinePlayer(record.fisher())).setScore((int) (record.fish().length() * 100));
        });
    }
}
