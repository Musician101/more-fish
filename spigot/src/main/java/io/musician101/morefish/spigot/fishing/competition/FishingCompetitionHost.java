package io.musician101.morefish.spigot.fishing.competition;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.common.fishing.competition.Prize;
import io.musician101.morefish.common.fishing.competition.Record;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import io.musician101.morefish.spigot.util.NumberUtils;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public final class FishingCompetitionHost {

    private final FishingCompetitionTimerBarHandler timerBarHandler = new FishingCompetitionTimerBarHandler();
    private BukkitTask timerTask;

    public void closeCompetition() {
        closeCompetition(false);
    }

    public void closeCompetition(boolean suspend) {
        getCompetition().disable();
        if (timerTask != null) {
            timerTask.cancel();
            if (timerBarHandler.getHasTimerEnabled()) {
                timerBarHandler.disableTimer();
            }
        }

        boolean broadcast = getMessagesConfig().broadcastOnStop();
        if (broadcast) {
            Bukkit.broadcastMessage(getLangConfig().text("contest-stop"));
        }

        if (!suspend) {
            if (!getPrizes().isEmpty()) {
                List<Record> ranking = getCompetition().getRanking();
                if (!ranking.isEmpty()) {
                    getPrizes().forEach((place, prize) -> {
                        if (ranking.size() > place) {
                            Record record = ranking.get(place);
                            prize.giveTo(record.getFisher(), getCompetition().rankNumberOf(record));
                        }
                    });
                }
            }

            if (broadcast && getMessagesConfig().showTopOnEnding()) {
                Bukkit.getOnlinePlayers().forEach(this::informAboutRanking);
            }
        }

        if (!getConfig().saveRecords()) {
            getCompetition().clearRecords();
        }
    }

    @Nonnull
    private FishingCompetition getCompetition() {
        return SpigotMoreFish.getInstance().getCompetition();
    }

    private Config<SpigotTextFormat, SpigotTextListFormat, String> getConfig() {
        return SpigotMoreFish.getInstance().getPluginConfig();
    }

    private LangConfig<SpigotTextFormat, SpigotTextListFormat, String> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private MessagesConfig getMessagesConfig() {
        return getConfig().getMessagesConfig();
    }

    @Nonnull
    private Map<Integer, Prize> getPrizes() {
        return getConfig().getPrizes();
    }

    public void informAboutRanking(@Nonnull CommandSender receiver) {
        if (getCompetition().getRanking().isEmpty()) {
            receiver.sendMessage(getLangConfig().text("top-no-record"));
        }
        else {
            int topSize = getMessagesConfig().getTopNumber();
            List<Record> top = getCompetition().top(topSize);
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                receiver.sendMessage(getLangConfig().format("top-list").replace(topReplacementOf(number, record)).output());
            });

            if (receiver instanceof Player) {
                if (!getCompetition().containsContestant(((Player) receiver).getUniqueId())) {
                    receiver.sendMessage(getLangConfig().text("top-mine-no-record"));
                }
                else {
                    Entry<Integer, Record> entry = getCompetition().rankedRecordOf(((Player) receiver).getUniqueId());
                    receiver.sendMessage(getLangConfig().format("top-mine").replace(topReplacementOf(entry.getKey() + 1, entry.getValue())).output());
                }
            }
        }

    }

    public void openCompetition() {
        getCompetition().enable();
        if (getMessagesConfig().broadcastOnStart()) {
            Bukkit.broadcastMessage(getLangConfig().text("contest-start"));
        }

    }

    public void openCompetitionFor(long tick) {
        long duration = tick / (long) 20;
        getCompetition().enable();
        timerTask = Bukkit.getScheduler().runTaskLater(SpigotMoreFish.getInstance(), (Runnable) this::closeCompetition, tick);
        if (getConfig().useBossBar()) {
            timerBarHandler.enableTimer(duration);
        }

        if (getMessagesConfig().broadcastOnStart()) {
            Bukkit.broadcastMessage(getLangConfig().text("contest-start"));
            String msg = getLangConfig().format("contest-start-timer").replace(ImmutableMap.of("%time%", getLangConfig().time(duration))).output();
            Bukkit.broadcastMessage(msg);
        }
    }

    private Map<String, Object> topReplacementOf(int number, Record record) {
        String player = Bukkit.getOfflinePlayer(record.getFisher()).getName();
        if (player == null) {
            player = record.getFisher().toString();
        }

        return ImmutableMap.of("%ordinal%", NumberUtils.ordinalOf(number), "%number%", String.valueOf(number), "%player%", player, "%length%", String.valueOf(record.getFish().getLength()), "%fish%", record.getFish().getType().getName());
    }
}
