package me.elsiff.morefish.hooker;

import io.musician101.musiboard.MusiBoard;
import io.musician101.musiboard.scoreboard.MusiScoreboard;
import io.musician101.musiboard.scoreboard.MusiScoreboardManager;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;

public class MusiBoardHooker implements PluginHooker {

    private final Map<UUID, String> previousBoards = new HashMap<>();
    private boolean hasHooked;
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
    public boolean hasHooked() {
        return hasHooked;
    }

    @Override
    public void hook(@NotNull MoreFish plugin) {
        PluginHooker.checkEnabled(this, plugin.getServer().getPluginManager());
        hasHooked = true;
        getManager().registerNewScoreboard("morefish");
        scoreboard = getManager().getScoreboard("morefish").orElseThrow(() -> new IllegalStateException("Ok, who broke it?"));
        scoreboard.saveData(false);
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
        if (hasHooked) {
            if (leaderboard != null && scoreboard.getObjective("leaderboard") != null) {
                leaderboard.unregister();
            }

            leaderboard = scoreboard.registerNewObjective("leaderboard", Criteria.DUMMY, text("Top 5 Fishers (mm)", AQUA));
            leaderboard.setDisplaySlot(DisplaySlot.SIDEBAR);
            List<FishRecord> records = getPlugin().getCompetition().getRecords();
            IntStream.range(0, Math.min(5, records.size())).forEach(i -> {
                FishRecord record = records.get(i);
                leaderboard.getScore(Bukkit.getOfflinePlayer(record.fisher())).setScore((int) (record.getLength() * 100));
            });
        }
    }
}
