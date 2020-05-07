package io.musician101.morefish.sponge.fishing.competition;

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
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchHandler;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import io.musician101.morefish.sponge.util.NumberUtils;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

public final class FishingCompetitionHost {

    private final FishingCompetitionTimerBarHandler timerBarHandler = new FishingCompetitionTimerBarHandler();
    private Task timerTask;

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
            Sponge.getServer().getBroadcastChannel().send(Text.of(getLangConfig().text("contest-stop")));
        }

        if (!suspend) {
            if (!getPrizes().isEmpty()) {
                List<Record<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>> ranking = getCompetition().getRanking();
                if (!ranking.isEmpty()) {
                    getPrizes().forEach((place, prize) -> {
                        if (ranking.size() > place) {
                            Record<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> record = ranking.get(place);
                            Sponge.getGame().getServiceManager().provideUnchecked(UserStorageService.class).get(record.getFisher()).ifPresent(user -> {
                                prize.giveTo(user, getCompetition().rankNumberOf(record));
                            });
                        }
                    });
                }
            }

            if (broadcast && getMessagesConfig().showTopOnEnding()) {
                Sponge.getServer().getOnlinePlayers().forEach(this::informAboutRanking);
            }
        }

        if (!getConfig().saveRecords()) {
            getCompetition().clearRecords();
        }
    }

    @Nonnull
    private FishingCompetition<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> getCompetition() {
        return SpongeMoreFish.getInstance().getCompetition();
    }

    private Config<FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>>, LangConfig<SpongeTextFormat, SpongeTextListFormat, Text>, MessagesConfig<SpongePlayerAnnouncement, BossBarColor>, FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text>, SpongePrize> getConfig() {
        return SpongeMoreFish.getInstance().getConfig();
    }

    private LangConfig<SpongeTextFormat, SpongeTextListFormat, Text> getLangConfig() {
        return SpongeMoreFish.getInstance().getConfig().getLangConfig();
    }

    private MessagesConfig<SpongePlayerAnnouncement, BossBarColor> getMessagesConfig() {
        return getConfig().getMessagesConfig();
    }

    @Nonnull
    private Map<Integer, SpongePrize> getPrizes() {
        return getConfig().getPrizes();
    }

    public final void informAboutRanking(@Nonnull CommandSource receiver) {
        if (getCompetition().getRanking().isEmpty()) {
            receiver.sendMessage(Text.of(getLangConfig().text("top-no-record")));
        }
        else {
            int topSize = getMessagesConfig().getTopNumber();
            List<Record<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>> top = getCompetition().top(topSize);
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                receiver.sendMessage(getLangConfig().format("top-list").replace(topReplacementOf(number, record)).output());
            });

            if (receiver instanceof Player) {
                if (!getCompetition().containsContestant(((Player) receiver).getUniqueId())) {
                    receiver.sendMessage(getLangConfig().text("top-mine-no-record"));
                }
                else {
                    Entry<Integer, Record<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>> entry = getCompetition().rankedRecordOf((((Player) receiver).getUniqueId()));
                    receiver.sendMessage(getLangConfig().format("top-mine").replace(topReplacementOf(entry.getKey() + 1, entry.getValue())).output());
                }
            }
        }

    }

    public final void openCompetition() {
        getCompetition().enable();
        if (getMessagesConfig().broadcastOnStart()) {
            Sponge.getServer().getBroadcastChannel().send(getLangConfig().text("contest-start"));
        }

    }

    public final void openCompetitionFor(long tick) {
        long duration = tick / (long) 20;
        getCompetition().enable();
        timerTask = Task.builder().execute((Runnable) this::closeCompetition).delayTicks(tick).submit(SpongeMoreFish.getInstance());
        if (getConfig().useBossBar()) {
            timerBarHandler.enableTimer(duration);
        }

        if (getMessagesConfig().broadcastOnStart()) {
            Sponge.getServer().getBroadcastChannel().send(getLangConfig().text("contest-start"));
            Text msg = getLangConfig().format("contest-start-timer").replace(ImmutableMap.of("%time%", getLangConfig().time(duration))).output();
            Sponge.getServer().getBroadcastChannel().send(msg);
        }
    }

    private Map<String, Object> topReplacementOf(int number, Record<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> record) {
        String name = "ERROR";
        try {
            name = Sponge.getServer().getGameProfileManager().get(record.getFisher()).get().getName().orElse(name);
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return ImmutableMap.of("%ordinal%", NumberUtils.ordinalOf(number), "%number%", String.valueOf(number), "%player%", name, "%length%", String.valueOf(record.getFish().getLength()), "%fish%", record.getFish().getType().getName());
    }
}
