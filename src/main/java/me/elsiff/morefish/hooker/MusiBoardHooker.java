package me.elsiff.morefish.hooker;

import io.musician101.musiboard.MusiBoard;
import io.musician101.musiboard.scoreboard.MusiScoreboard;
import io.musician101.musiboard.scoreboard.MusiScoreboardManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.fishing.competition.Record;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;

public class MusiBoardHooker implements PluginHooker {

    private boolean hasHooked;
    private MusiScoreboard scoreboard;
    private Objective leaderboard;
    private final Map<UUID, String> previousBoards = new HashMap<>();

    public void swapScoreboards(@Nonnull Player player) {
        if (previousBoards.containsKey(player.getUniqueId())) {
            restorePreviousBoard(player);
        }
        else {
            addToLeaderboard(player);
        }
    }

    public void addToLeaderboard(@Nonnull Player player) {
        if (hasHooked) {
            previousBoards.put(player.getUniqueId(), getManager().getScoreboard(player).getName());
            getManager().setScoreboard(player, scoreboard);
        }
    }

    public void clear() {
        Bukkit.getOnlinePlayers().forEach(this::restorePreviousBoard);
    }

    public void restorePreviousBoard(@Nonnull Player player) {
        if (hasHooked) {
            if (previousBoards.containsKey(player.getUniqueId())) {
                MusiScoreboard scoreboard = getManager().getScoreboardOrDefaultOrVanilla(previousBoards.get(player.getUniqueId()));
                getManager().setScoreboard(player, scoreboard);
            }
        }
    }

    public void update() {
        if (leaderboard != null && scoreboard.getObjective("leaderboard") != null) {
            leaderboard.unregister();
        }

        leaderboard = scoreboard.registerNewObjective("leaderboard", Criteria.DUMMY, text("Top 5 Fishers (mm)", AQUA));
        leaderboard.setDisplaySlot(DisplaySlot.SIDEBAR);
        List<Record> records = getPlugin().getCompetition().getRanking();
        IntStream.range(0, Math.min(5, records.size())).forEach(i -> {
            Record record = records.get(i);
            leaderboard.getScore(Bukkit.getOfflinePlayer(record.fisher())).setScore((int) (record.fish().length() * 100));
        });
    }

    private MusiScoreboardManager getManager() {
        return MusiBoard.getPlugin().getManager();
    }

    @Nonnull
    @Override
    public String getPluginName() {
        return "MusiBoard";
    }

    @Override
    public boolean hasHooked() {
        return hasHooked;
    }

    @Override
    public void hook(@Nonnull MoreFish plugin) {
        PluginHooker.checkEnabled(this, plugin.getServer().getPluginManager());
        hasHooked = true;
        getManager().registerNewScoreboard("morefish");
        scoreboard = getManager().getScoreboard("morefish").orElseThrow(() -> new IllegalStateException("Ok, who broke it?"));
    }
}
