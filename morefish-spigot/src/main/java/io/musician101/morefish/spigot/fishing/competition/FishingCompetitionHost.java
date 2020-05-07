package io.musician101.morefish.spigot.fishing.competition;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.common.fishing.competition.Record;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchHandler;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import io.musician101.morefish.spigot.util.NumberUtils;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public final class FishingCompetitionHost {

    private final FishingCompetitionTimerBarHandler timerBarHandler = new FishingCompetitionTimerBarHandler();
    private BukkitTask timerTask;

    public final void closeCompetition() {
        closeCompetition(false);
    }

    public final void closeCompetition(boolean suspend) {
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
                List<Record<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>> ranking = getCompetition().getRanking();
                if (!ranking.isEmpty()) {
                    getPrizes().forEach((place, prize) -> {
                        if (ranking.size() > place) {
                            Record<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> record = ranking.get(place);
                            prize.giveTo(Bukkit.getOfflinePlayer(record.getFisher()), getCompetition().rankNumberOf(record));
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
    private FishingCompetition<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> getCompetition() {
        return SpigotMoreFish.getInstance().getCompetition();
    }

    private Config<FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>>, LangConfig<SpigotTextFormat, SpigotTextListFormat, String>, MessagesConfig<SpigotPlayerAnnouncement, BarColor>, FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String>, SpigotPrize> getConfig() {
        return SpigotMoreFish.getInstance().getPluginConfig();
    }

    private LangConfig<SpigotTextFormat, SpigotTextListFormat, String> getLangConfig() {
        return SpigotMoreFish.getInstance().getPluginConfig().getLangConfig();
    }

    private MessagesConfig<SpigotPlayerAnnouncement, BarColor> getMessagesConfig() {
        return getConfig().getMessagesConfig();
    }

    @Nonnull
    private Map<Integer, SpigotPrize> getPrizes() {
        return getConfig().getPrizes();
    }

    public final void informAboutRanking(@Nonnull CommandSender receiver) {
        if (getCompetition().getRanking().isEmpty()) {
            receiver.sendMessage(getLangConfig().text("top-no-record"));
        }
        else {
            int topSize = getMessagesConfig().getTopNumber();
            List<Record<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>> top = getCompetition().top(topSize);
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                receiver.sendMessage(getLangConfig().format("top-list").replace(topReplacementOf(number, record)).output());
            });

            if (receiver instanceof Player) {
                if (!getCompetition().containsContestant(((Player) receiver).getUniqueId())) {
                    receiver.sendMessage(getLangConfig().text("top-mine-no-record"));
                }
                else {
                    Entry<Integer, Record<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>> entry = getCompetition().rankedRecordOf(((Player) receiver).getUniqueId());
                    receiver.sendMessage(getLangConfig().format("top-mine").replace(topReplacementOf(entry.getKey() + 1, entry.getValue())).output());
                }
            }
        }

    }

    public final void openCompetition() {
        getCompetition().enable();
        if (getMessagesConfig().broadcastOnStart()) {
            Bukkit.broadcastMessage(getLangConfig().text("contest-start"));
        }

    }

    public final void openCompetitionFor(long tick) {
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

    private Map<String, Object> topReplacementOf(int number, Record<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> record) {
        return ImmutableMap.of("%ordinal%", NumberUtils.ordinalOf(number), "%number%", String.valueOf(number), "%player%", Bukkit.getOfflinePlayer(record.getFisher()).getName(), "%length%", String.valueOf(record.getFish().getLength()), "%fish%", record.getFish().getType().getName());
    }
}
