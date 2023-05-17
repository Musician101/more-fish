package me.elsiff.morefish.fishing.competition;

import java.util.List;
import java.util.stream.IntStream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import static me.elsiff.morefish.MoreFish.getPlugin;

public class MFScoreboard {

    private Objective leaderboard;
    private Scoreboard scoreboard;

    public void addPlayer(Player player) {
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        player.setScoreboard(scoreboard);
    }

    public void clear() {
        Bukkit.getOnlinePlayers().stream().filter(player -> player.getScoreboard().equals(scoreboard)).forEach(player -> player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard()));
    }

    public void update() {
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        if (leaderboard != null && scoreboard.getObjective("leaderboard") != null) {
            leaderboard.unregister();
        }

        this.leaderboard = scoreboard.registerNewObjective("leaderboard", Criteria.DUMMY, Component.text("Top 5 Fishers (mm)", NamedTextColor.AQUA));
        List<Record> records = getPlugin().getCompetition().getRanking();
        IntStream.range(0, Math.min(5, records.size())).forEach(i -> {
            Record record = records.get(i);
            leaderboard.getScore(Bukkit.getOfflinePlayer(record.fisher())).setScore((int) (record.fish().length() * 100));
        });
        leaderboard.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
}
