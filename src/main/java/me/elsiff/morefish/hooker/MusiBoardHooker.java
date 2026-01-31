package me.elsiff.morefish.hooker;

import io.musician101.musiboard.MusiBoard;
import io.musician101.musiboard.scoreboard.MusiScoreboard;
import io.musician101.musiboard.scoreboard.MusiScoreboardManager;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.lang.TagResolverUtil;
import me.elsiff.morefish.records.FishRecord;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.NodePath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
public class MusiBoardHooker implements PluginHooker {

    private final Map<UUID, String> previousBoards = new HashMap<>();
    private boolean hasHooked;
    @Nullable
    private Objective leaderboard;
    @Nullable
    private MusiScoreboard scoreboard;

    public void addToLeaderboard(Player player) {
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

    @Override
    public String getPluginName() {
        return "MusiBoard";
    }

    @Override
    public boolean hasHooked() {
        return hasHooked;
    }

    @Override
    public void hook() {
        if (canHook()) {
            hasHooked = true;
            getManager().registerNewScoreboard("morefish");
            scoreboard = getManager().getScoreboard("morefish").orElseThrow(() -> new IllegalStateException("Ok, who broke it?"));
            scoreboard.saveData(false);
        }
    }

    public void restorePreviousBoard(Player player) {
        if (hasHooked) {
            if (previousBoards.containsKey(player.getUniqueId())) {
                MusiScoreboard scoreboard = getManager().getScoreboardOrDefaultOrVanilla(previousBoards.get(player.getUniqueId()));
                getManager().setScoreboard(player, scoreboard);
                previousBoards.remove(player.getUniqueId());
            }
        }
    }

    public void swapScoreboards(Player player) {
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

            NodePath scoreboardPath = NodePath.path("main", "scoreboard");
            leaderboard = scoreboard.registerNewObjective("leaderboard", Criteria.DUMMY, lang().getComponent(scoreboardPath.withAppendedChild("display-name")));
            leaderboard.setDisplaySlot(DisplaySlot.SIDEBAR);
            leaderboard.numberFormat(NumberFormat.blank());
            List<FishRecord> records = getPlugin().getCompetition().getRecords();
            records.sort(SortType.LENGTH.reversed());
            IntStream.range(0, Math.min(5, records.size())).forEach(i -> {
                FishRecord record = records.get(i);
                OfflinePlayer player = Bukkit.getOfflinePlayer(record.fisher());
                Score score = leaderboard.getScore(player);
                score.setScore((int) (record.fish().length() * 100));
                TagResolver resolver = TagResolver.resolver(TagResolverUtil.playerNameResolver(player), record);
                score.customName(lang().getComponent(scoreboardPath.withAppendedChild("entry"), resolver));
            });
        }
    }
}
