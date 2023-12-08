package me.elsiff.morefish.paper.fishing.competition;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.elsiff.morefish.common.fishing.competition.FishRecord;
import me.elsiff.morefish.common.fishing.competition.FishingCompetitionHost;
import me.elsiff.morefish.paper.configuration.Config;
import me.elsiff.morefish.paper.fishing.PaperFish;
import me.elsiff.morefish.common.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.common.configuration.Lang.CONTEST_START;
import static me.elsiff.morefish.common.configuration.Lang.CONTEST_START_TIMER;
import static me.elsiff.morefish.common.configuration.Lang.CONTEST_STOP;
import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;
import static me.elsiff.morefish.paper.configuration.PaperLang.PREFIX;
import static me.elsiff.morefish.paper.configuration.PaperLang.join;
import static me.elsiff.morefish.paper.configuration.PaperLang.lang;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public final class PaperFishingCompetitionHost extends FishingCompetitionHost<PaperTimerBarHandler, CommandSender, BukkitTask> {

    public PaperFishingCompetitionHost() {
        super(new PaperTimerBarHandler());
    }

    @Override
    public void closeCompetition(boolean suspend) {
        getCompetition().disable();
        if (timerTask != null) {
            timerTask.cancel();
            if (timerBarHandler.getHasTimerEnabled()) {
                timerBarHandler.disableTimer();
            }
        }

        boolean broadcast = getMsgConfig().getBoolean("broadcast-stop");
        if (broadcast) {
            Bukkit.broadcast(CONTEST_STOP);
        }

        if (!suspend) {
            if (!getPrizes().isEmpty()) {
                List<FishRecord<PaperFish>> ranking = getCompetition().getRanking();
                if (!ranking.isEmpty()) {
                    getPrizes().forEach((place, prize) -> {
                        if (ranking.size() > place) {
                            FishRecord<PaperFish> record = ranking.get(place);
                            prize.giveTo(record.fisher(), getCompetition().rankNumberOf(record));
                        }
                    });
                }
            }

            if (broadcast && getMsgConfig().getBoolean("show-top-on-ending")) {
                Bukkit.getOnlinePlayers().forEach(this::informAboutRanking);
            }
        }

        if (!getConfig().getBoolean("general.save-records")) {
            getCompetition().clearRecords();
        }
    }

    PaperFishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    @NotNull
    private FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    private ConfigurationSection getMsgConfig() {
        return getConfig().getConfigurationSection("messages");
    }

    @NotNull
    private Map<Integer, PaperPrize> getPrizes() {
        return Config.getPrizes();
    }

    @Override
    public void informAboutRanking(@NotNull CommandSender receiver) {
        if (getCompetition().getRanking().isEmpty()) {
            receiver.sendMessage(join(PREFIX, text("Nobody made any record yet.")));
        }
        else {
            int topSize = getMsgConfig().getInt("top-number", 1);
            List<FishRecord<PaperFish>> top = getCompetition().top(topSize);
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                receiver.sendMessage(lang().replace(join(PREFIX, text("%ordinal%. ", YELLOW), text(": %player%, %length%cm %fish%", DARK_GRAY)), topReplacementOf(number, record)));
            });

            if (receiver instanceof Player) {
                if (!getCompetition().containsContestant(((Player) receiver).getUniqueId())) {
                    receiver.sendMessage(join(PREFIX, text("You didn't catch any fish.")));
                }
                else {
                    Entry<Integer, FishRecord<PaperFish>> entry = getCompetition().rankedRecordOf(((Player) receiver).getUniqueId());
                    receiver.sendMessage(lang().replace(join(PREFIX, text("You're %ordinal%: %length%cm %fish%")), topReplacementOf(entry.getKey() + 1, entry.getValue())));
                }
            }
        }

    }

    @Override
    public void openCompetition() {
        getCompetition().enable();
        if (getMsgConfig().getBoolean("broadcast-start")) {
            Bukkit.broadcast(CONTEST_START);
        }
    }

    @Override
    public void openCompetitionFor(long tick) {
        long duration = tick / (long) 20;
        getCompetition().enable();
        timerTask = Bukkit.getScheduler().runTaskLater(getPlugin(), (Runnable) this::closeCompetition, tick);
        if (getConfig().getBoolean("general.use-boss-bar")) {
            timerBarHandler.enableTimer(duration);
        }

        if (getMsgConfig().getBoolean("broadcast-start")) {
            Bukkit.broadcast(CONTEST_START);
            Bukkit.broadcast(lang().replace(CONTEST_START_TIMER, Map.of("%time%", lang().time(duration))));
        }
    }

    private Map<String, Object> topReplacementOf(int number, FishRecord<PaperFish> record) {
        String player = Bukkit.getOfflinePlayer(record.fisher()).getName();
        return Map.of("%ordinal%", NumberUtils.ordinalOf(number), "%number%", String.valueOf(number), "%player%", player == null ? "null" : player, "%length%", String.valueOf(record.fish().length()), "%fish%", record.fish().type().name());
    }
}
