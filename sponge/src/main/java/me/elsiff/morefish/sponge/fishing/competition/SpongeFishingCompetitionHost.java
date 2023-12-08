package me.elsiff.morefish.sponge.fishing.competition;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.elsiff.morefish.common.fishing.competition.FishRecord;
import me.elsiff.morefish.common.fishing.competition.FishingCompetitionHost;
import me.elsiff.morefish.common.util.NumberUtils;
import me.elsiff.morefish.sponge.configuration.Config;
import me.elsiff.morefish.sponge.fishing.SpongeFish;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.configurate.ConfigurationNode;

import static me.elsiff.morefish.common.configuration.Lang.CONTEST_START;
import static me.elsiff.morefish.common.configuration.Lang.CONTEST_START_TIMER;
import static me.elsiff.morefish.common.configuration.Lang.CONTEST_STOP;
import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.PREFIX;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.lang;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public final class SpongeFishingCompetitionHost extends FishingCompetitionHost<SpongeTimerBarHandler, Audience, ScheduledTask> {

    public SpongeFishingCompetitionHost() {
        super(new SpongeTimerBarHandler());
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

        boolean broadcast = getMsgConfig().node("broadcast-stop").getBoolean();
        if (broadcast) {
            Sponge.server().broadcastAudience().sendMessage(CONTEST_STOP);
        }

        if (!suspend) {
            if (!getPrizes().isEmpty()) {
                List<FishRecord<SpongeFish>> ranking = getCompetition().getRanking();
                if (!ranking.isEmpty()) {
                    getPrizes().forEach((place, prize) -> {
                        if (ranking.size() > place) {
                            FishRecord<SpongeFish> record = ranking.get(place);
                            prize.giveTo(record.fisher(), getCompetition().rankNumberOf(record));
                        }
                    });
                }
            }

            if (broadcast && getMsgConfig().node("show-top-on-ending").getBoolean()) {
                Sponge.server().onlinePlayers().forEach(this::informAboutRanking);
            }
        }

        if (!getConfig().node("general.save-records").getBoolean()) {
            getCompetition().clearRecords();
        }
    }

    SpongeFishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    @NotNull
    private ConfigurationNode getConfig() {
        return getPlugin().getConfig();
    }

    private ConfigurationNode getMsgConfig() {
        return getConfig().node("messages");
    }

    @NotNull
    private Map<Integer, SpongePrize> getPrizes() {
        return Config.getPrizes();
    }

    @Override
    public void informAboutRanking(@NotNull Audience receiver) {
        if (getCompetition().getRanking().isEmpty()) {
            receiver.sendMessage(join(PREFIX, text("Nobody made any record yet.")));
        }
        else {
            int topSize = getMsgConfig().node("top-number").getInt(1);
            List<FishRecord<SpongeFish>> top = getCompetition().top(topSize);
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                receiver.sendMessage(lang().replace(join(PREFIX, text("%ordinal%. ", YELLOW), text(": %player%, %length%cm %fish%", DARK_GRAY)), topReplacementOf(number, record)));
            });

            if (receiver instanceof ServerPlayer player) {
                if (!getCompetition().containsContestant(player.uniqueId())) {
                    receiver.sendMessage(join(PREFIX, text("You didn't catch any fish.")));
                }
                else {
                    Entry<Integer, FishRecord<SpongeFish>> entry = getCompetition().rankedRecordOf(player.uniqueId());
                    receiver.sendMessage(lang().replace(join(PREFIX, text("You're %ordinal%: %length%cm %fish%")), topReplacementOf(entry.getKey() + 1, entry.getValue())));
                }
            }
        }

    }

    @Override
    public void openCompetition() {
        getCompetition().enable();
        if (getMsgConfig().node("broadcast-start").getBoolean()) {
            Sponge.server().broadcastAudience().sendMessage(CONTEST_START);
        }
    }

    @Override
    public void openCompetitionFor(long tick) {
        long duration = tick / (long) 20;
        getCompetition().enable();
        timerTask = Sponge.asyncScheduler().submit(Task.builder().execute(() -> closeCompetition()).plugin(getPlugin().getPluginContainer()).delay(Ticks.of(tick)).build());
        if (getConfig().node("general.use-boss-bar").getBoolean()) {
            timerBarHandler.enableTimer(duration);
        }

        if (getMsgConfig().node("broadcast-start").getBoolean()) {
            Sponge.server().broadcastAudience().sendMessage(CONTEST_START);
            Sponge.server().broadcastAudience().sendMessage(lang().replace(CONTEST_START_TIMER, Map.of("%time%", lang().time(duration))));
        }
    }

    private Map<String, Object> topReplacementOf(int number, FishRecord<SpongeFish> record) {
        String player = Sponge.server().gameProfileManager().cache().findById(record.fisher()).flatMap(GameProfile::name).orElse("null");
        return Map.of("%ordinal%", NumberUtils.ordinalOf(number), "%number%", String.valueOf(number), "%player%", player, "%length%", String.valueOf(record.fish().length()), "%fish%", record.fish().type().name());
    }
}
