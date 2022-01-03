package io.musician101.morefish.forge.fishing.competition;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.common.fishing.competition.Prize;
import io.musician101.morefish.common.fishing.competition.Record;
import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.morefish.forge.announcement.ForgePlayerAnnouncement;
import io.musician101.morefish.forge.fishing.catchhandler.ForgeCatchHandler;
import io.musician101.morefish.forge.fishing.condition.ForgeFishCondition;
import io.musician101.morefish.forge.scheduler.Scheduler;
import io.musician101.morefish.forge.scheduler.Task;
import io.musician101.morefish.forge.util.NumberUtils;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

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
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        if (broadcast) {
            server.getPlayerList().sendMessage(getLangConfig().text("contest-stop"));
        }

        if (!suspend) {
            if (!getPrizes().isEmpty()) {
                List<Record<ForgePlayerAnnouncement, TextFormatting, ForgeFishCondition, ForgeCatchHandler, ItemStack>> ranking = getCompetition().getRanking();
                if (!ranking.isEmpty()) {
                    getPrizes().forEach((place, prize) -> {
                        if (ranking.size() > place) {
                            Record<ForgePlayerAnnouncement, TextFormatting, ForgeFishCondition, ForgeCatchHandler, ItemStack> record = ranking.get(place);

                            prize.giveTo(server.getPlayerProfileCache().getProfileByUUID(record.getFisher()), getCompetition().rankNumberOf(record));
                        }
                    });
                }
            }

            if (broadcast && getMessagesConfig().showTopOnEnding()) {
                server.getPlayerList().getPlayers().stream().map(ServerPlayerEntity::getCommandSource).forEach(this::informAboutRanking);
            }
        }

        if (!getConfig().saveRecords()) {
            getCompetition().clearRecords();
        }
    }

    @Nonnull
    private FishingCompetition<ItemStack> getCompetition() {
        return ForgeMoreFish.getInstance().getCompetition();
    }

    private Config getConfig() {
        return ForgeMoreFish.getInstance().getPluginConfig();
    }

    private LangConfig<?, ?, ?> getLangConfig() {
        return ForgeMoreFish.getInstance().getPluginConfig().getLangConfig();
    }

    private MessagesConfig<?, ?> getMessagesConfig() {
        return getConfig().getMessagesConfig();
    }

    @Nonnull
    private Map<Integer, Prize> getPrizes() {
        return getConfig().getPrizes();
    }

    public final void informAboutRanking(@Nonnull CommandSource receiver) {
        if (getCompetition().getRanking().isEmpty()) {
            receiver.sendFeedback(getLangConfig().text("top-no-record"), true);
        }
        else {
            int topSize = getMessagesConfig().getTopNumber();
            List<Record<ForgePlayerAnnouncement, TextFormatting, ForgeFishCondition, ForgeCatchHandler, ItemStack>> top = getCompetition().top(topSize);
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                receiver.sendFeedback(getLangConfig().format("top-list").replace(topReplacementOf(number, record)).output(), true);
            });

            try {
                ServerPlayerEntity player = receiver.asPlayer();
                if (!getCompetition().containsContestant(player.getUniqueID())) {
                    receiver.sendFeedback(getLangConfig().text("top-mine-no-record"), true);
                }
                else {
                    Entry<Integer, Record<ForgePlayerAnnouncement, TextFormatting, ForgeFishCondition, ForgeCatchHandler, ItemStack>> entry = getCompetition().rankedRecordOf(player.getUniqueID());
                    receiver.sendFeedback(getLangConfig().format("top-mine").replace(topReplacementOf(entry.getKey() + 1, entry.getValue())).output(), true);
                }
            }
            catch (CommandSyntaxException ignored) {

            }
        }

    }

    public final void openCompetition() {
        getCompetition().enable();
        if (getMessagesConfig().broadcastOnStart()) {
            LogicalSidedProvider.INSTANCE.<MinecraftServer>get(LogicalSide.SERVER).getPlayerList().sendMessage(getLangConfig().text("contest-start"));
        }
    }

    public final void openCompetitionFor(long tick) {
        long duration = tick / (long) 20;
        getCompetition().enable();
        timerTask = new Task((int) tick) {

            @Override
            public void run() {
                closeCompetition();
            }
        };
        Scheduler.scheduleTask(timerTask);
        if (getConfig().useBossBar()) {
            timerBarHandler.enableTimer(duration);
        }

        if (getMessagesConfig().broadcastOnStart()) {
            MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
            server.getPlayerList().sendMessage(getLangConfig().text("contest-start"));
            ITextComponent msg = getLangConfig().format("contest-start-timer").replace(ImmutableMap.of("%time%", getLangConfig().time(duration))).output();
            server.getPlayerList().sendMessage(msg);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private Map<String, String> topReplacementOf(int number, Record<ForgePlayerAnnouncement, TextFormatting> record) {
        String playerName = LogicalSidedProvider.INSTANCE.<MinecraftServer>get(LogicalSide.SERVER).getPlayerProfileCache().getProfileByUUID(record.getFisher()).getName();
        return ImmutableMap.of("%ordinal%", NumberUtils.ordinalOf(number), "%number%", String.valueOf(number), "%player%", playerName, "%length%", String.valueOf(record.getFish().getLength()), "%fish%", record.getFish().getType().getName());
    }
}
