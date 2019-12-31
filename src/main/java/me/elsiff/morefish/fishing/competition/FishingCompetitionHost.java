package me.elsiff.morefish.fishing.competition;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class FishingCompetitionHost {

    @Nonnull
    private final FishingCompetition competition;
    private final Plugin plugin;
    private final FishingCompetitionTimerBarHandler timerBarHandler;
    private BukkitTask timerTask;

    public FishingCompetitionHost(@Nonnull Plugin plugin, @Nonnull FishingCompetition competition) {
        this.plugin = plugin;
        this.competition = competition;
        this.timerBarHandler = new FishingCompetitionTimerBarHandler(plugin);
    }

    public final void closeCompetition() {
        closeCompetition(false);
    }

    public final void closeCompetition(boolean suspend) {
        competition.disable();
        if (timerTask != null) {
            timerTask.cancel();
            if (timerBarHandler.getHasTimerEnabled()) {
                timerBarHandler.disableTimer();
            }
        }

        boolean broadcast = getMsgConfig().getBoolean("broadcast-stop");
        if (broadcast) {
            plugin.getServer().broadcastMessage(Lang.INSTANCE.text("contest-stop"));
        }

        if (!suspend) {
            if (!getPrizes().isEmpty()) {
                List<Record> ranking = competition.getRanking();
                getPrizes().forEach((place, prize) -> {
                    Record record = ranking.get(place);
                    prize.giveTo(Bukkit.getOfflinePlayer(record.getFisher()), competition.rankNumberOf(record), plugin);
                });
            }

            if (broadcast && getMsgConfig().getBoolean("show-top-on-ending")) {
                plugin.getServer().getOnlinePlayers().forEach(this::informAboutRanking);
            }
        }

        if (!Config.INSTANCE.getStandard().getBoolean("general.save-records")) {
            competition.clearRecords();
        }

    }

    @Nonnull
    public final FishingCompetition getCompetition() {
        return competition;
    }

    private ConfigurationSection getMsgConfig() {
        return Config.INSTANCE.getStandard().getConfigurationSection("messages");
    }

    @Nonnull
    private Map<Integer, Prize> getPrizes() {
        return Config.INSTANCE.getPrizeMapLoader().loadFrom(Config.INSTANCE.getStandard(), "contest-prizes");
    }

    public final void informAboutRanking(@Nonnull CommandSender receiver) {
        if (competition.getRanking().isEmpty()) {
            receiver.sendMessage(Lang.INSTANCE.text("top-no-record"));
        }
        else {
            int topSize = getMsgConfig().getInt("top-number", 1);
            List<Record> top = competition.top(topSize);
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                receiver.sendMessage(Lang.INSTANCE.format("top-list").replace(topReplacementOf(number, record)).output());
            });

            if (receiver instanceof Player) {
                if (!competition.containsContestant(((Player) receiver).getUniqueId())) {
                    receiver.sendMessage(Lang.INSTANCE.text("top-mine-no-record"));
                }
                else {
                    Entry<Integer, Record> entry = competition.rankedRecordOf((OfflinePlayer) receiver);
                    receiver.sendMessage(Lang.INSTANCE.format("top-mine").replace(topReplacementOf(entry.getKey() + 1, entry.getValue())).output());
                }
            }
        }

    }

    public final void openCompetition() {
        competition.enable();
        if (getMsgConfig().getBoolean("broadcast-start")) {
            plugin.getServer().broadcastMessage(Lang.INSTANCE.text("contest-start"));
        }

    }

    public final void openCompetitionFor(long tick) {
        long duration = tick / (long) 20;
        competition.enable();
        timerTask = plugin.getServer().getScheduler().runTaskLater(plugin, (Runnable) this::closeCompetition, tick);
        if (Config.INSTANCE.getStandard().getBoolean("general.use-boss-bar")) {
            timerBarHandler.enableTimer(duration);
        }

        if (getMsgConfig().getBoolean("broadcast-start")) {
            plugin.getServer().broadcastMessage(Lang.INSTANCE.text("contest-start"));
            String msg = Lang.INSTANCE.format("contest-start-timer").replace(ImmutableMap.of("%time%", Lang.INSTANCE.time(duration))).output();
            plugin.getServer().broadcastMessage(msg);
        }
    }

    private Map<String, Object> topReplacementOf(int number, Record record) {
        return ImmutableMap.of("%ordinal%", NumberUtils.ordinalOf(number), "%number%", String.valueOf(number), "%player%", Bukkit.getOfflinePlayer(record.getFisher()).getName(), "%length%", String.valueOf(record.getFish().getLength()), "%fish%", record.getFish().getType().getName());
    }
}
