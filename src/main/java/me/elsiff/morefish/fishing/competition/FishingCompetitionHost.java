package me.elsiff.morefish.fishing.competition;

import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import me.elsiff.morefish.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public final class FishingCompetitionHost {

    @NotNull
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

        Bukkit.broadcast(Lang.CONTEST_STOP);
        if (!suspend) {
            if (!getPrizes().isEmpty()) {
                List<FishRecord> ranking = getCompetition().getRecords();
                ranking.sort(SortType.LENGTH.sorter());
                if (!ranking.isEmpty()) {
                    getPrizes().forEach((place, prize) -> {
                        if (ranking.size() > place) {
                            FishRecord record = ranking.get(place);
                            prize.giveTo(Bukkit.getOfflinePlayer(record.fisher()), getCompetition().rankNumberOf(record), getPlugin());
                        }
                    });
                }
            }

            Bukkit.getOnlinePlayers().forEach(this::informAboutRanking);
        }

        if (getConfig().getBoolean("general.save-records")) {
            getCompetition().getRecords().forEach(r -> getPlugin().getFishingLogs().add(r));
        }

        getCompetition().clear();
    }

    @NotNull
    public FishingCompetition getCompetition() {
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
    private Map<Integer, Prize> getPrizes() {
        return Config.getPrizes();
    }

    public void informAboutRanking(@NotNull CommandSender receiver) {
        if (getCompetition().getRecords().isEmpty()) {
            receiver.sendMessage(join(PREFIX, text("Nobody has caught anything yet.")));
        }
        else {
            int topSize = getMsgConfig().getInt("top-number", 1);
            List<FishRecord> top = getCompetition().top(topSize);
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                receiver.sendMessage(Lang.replace(join(PREFIX, text("%ordinal%. ", YELLOW), text(": %player%, %length%cm %fish%", DARK_GRAY)), topReplacementOf(number, record)));
            });

            if (receiver instanceof Player) {
                if (!getCompetition().containsContestant(((Player) receiver).getUniqueId())) {
                    receiver.sendMessage(join(PREFIX, text("You didn't catch any fish.")));
                }
                else {
                    Entry<Integer, FishRecord> entry = getCompetition().rankedRecordOf((OfflinePlayer) receiver);
                    receiver.sendMessage(Lang.replace(join(PREFIX, text("You're %ordinal%: %length%cm %fish%")), topReplacementOf(entry.getKey() + 1, entry.getValue())));
                }
            }
        }
    }

    public void openCompetitionFor(long tick) {
        long duration = tick / (long) 20;
        getCompetition().enable();
        timerTask = Bukkit.getScheduler().runTaskLater(getPlugin(), (Runnable) this::closeCompetition, tick);
        timerBarHandler.enableTimer(duration);
        Bukkit.broadcast(Lang.CONTEST_START);
        Bukkit.broadcast(Lang.replace(Lang.CONTEST_START_TIMER, Map.of("%time%", Lang.time(duration))));
    }

    private Map<String, Object> topReplacementOf(int number, FishRecord record) {
        String player = Bukkit.getOfflinePlayer(record.fisher()).getName();
        return Map.of("%ordinal%", NumberUtils.ordinalOf(number), "%number%", String.valueOf(number), "%player%", player == null ? "null" : player, "%length%", String.valueOf(record.getLength()), "%fish%", record.getFishName());
    }
}
