package io.musician101.morefish.sponge.fishing.competition;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.common.fishing.competition.Prize;
import io.musician101.morefish.common.fishing.competition.Record;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import io.musician101.morefish.sponge.util.NumberUtils;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

public final class FishingCompetitionHost {

    private final FishingCompetitionTimerBarHandler timerBarHandler = new FishingCompetitionTimerBarHandler();
    private ScheduledTask timerTask;

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
            Sponge.server().broadcastAudience().sendMessage(getLangConfig().text("contest-stop"));
        }

        if (!suspend) {
            if (!getPrizes().isEmpty()) {
                List<Record> ranking = getCompetition().getRanking();
                if (!ranking.isEmpty()) {
                    getPrizes().forEach((place, prize) -> {
                        if (ranking.size() > place) {
                            Record record = ranking.get(place);
                            Sponge.server().userManager().find(record.getFisher()).ifPresent(user -> prize.giveTo(user.uniqueId(), getCompetition().rankNumberOf(record)));
                        }
                    });
                }
            }

            if (broadcast && getMessagesConfig().showTopOnEnding()) {
                Sponge.server().onlinePlayers().forEach(this::informAboutRanking);
            }
        }

        if (!getConfig().saveRecords()) {
            getCompetition().clearRecords();
        }
    }

    @Nonnull
    private FishingCompetition getCompetition() {
        return SpongeMoreFish.getInstance().getCompetition();
    }

    private Config<SpongeTextFormat, SpongeTextListFormat, Component> getConfig() {
        return SpongeMoreFish.getInstance().getConfig();
    }

    private LangConfig<SpongeTextFormat, SpongeTextListFormat, Component> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private MessagesConfig getMessagesConfig() {
        return getConfig().getMessagesConfig();
    }

    @Nonnull
    private Map<Integer, Prize> getPrizes() {
        return getConfig().getPrizes();
    }

    public final void informAboutRanking(@Nonnull Audience receiver) {
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

            if (receiver instanceof ServerPlayer) {
                if (!getCompetition().containsContestant(((ServerPlayer) receiver).uniqueId())) {
                    receiver.sendMessage(getLangConfig().text("top-mine-no-record"));
                }
                else {
                    Entry<Integer, Record> entry = getCompetition().rankedRecordOf((((ServerPlayer) receiver).uniqueId()));
                    receiver.sendMessage(getLangConfig().format("top-mine").replace(topReplacementOf(entry.getKey() + 1, entry.getValue())).output());
                }
            }
        }

    }

    public final void openCompetition() {
        getCompetition().enable();
        if (getMessagesConfig().broadcastOnStart()) {
            Sponge.server().broadcastAudience().sendMessage(getLangConfig().text("contest-start"));
        }

    }

    public final void openCompetitionFor(long tick) {
        long duration = tick / (long) 20;
        getCompetition().enable();
        timerTask = Sponge.asyncScheduler().submit(Task.builder().execute((Runnable) this::closeCompetition).delay(Ticks.of(tick)).plugin(SpongeMoreFish.getInstance().getPluginContainer()).build());
        if (getConfig().useBossBar()) {
            timerBarHandler.enableTimer(duration);
        }

        if (getMessagesConfig().broadcastOnStart()) {
            Audience audience = Sponge.server().broadcastAudience();
            audience.sendMessage(getLangConfig().text("contest-start"));
            Component msg = getLangConfig().format("contest-start-timer").replace(ImmutableMap.of("%time%", getLangConfig().time(duration))).output();
            audience.sendMessage(msg);
        }
    }

    private Map<String, Object> topReplacementOf(int number, Record record) {
        String name = Sponge.server().userManager().find(record.getFisher()).map(User::name).orElse("ERROR");
        return ImmutableMap.of("%ordinal%", NumberUtils.ordinalOf(number), "%number%", String.valueOf(number), "%player%", name, "%length%", String.valueOf(record.getFish().getLength()), "%fish%", record.getFish().getType().getName());
    }
}
