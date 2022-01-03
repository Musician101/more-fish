package io.musician101.morefish.forge.fishing.competition;

import com.mojang.authlib.GameProfile;
import io.musician101.morefish.common.fishing.competition.Prize;
import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.morefish.forge.util.NumberUtils;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public final class ForgePrize extends Prize {

    public ForgePrize(@Nonnull List<String> commands) {
        super(commands);
    }

    @Override
    public void giveTo(@Nonnull GameProfile user, int rankNumber) {
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        if (Stream.of(server.getOnlinePlayerNames()).noneMatch(s -> s.equals(user.getName()))) {
            ForgeMoreFish.getInstance().getLogger().warn(NumberUtils.ordinalOf(rankNumber) + " fisher " + user.getName() + " isn't online! Contest prizes may not be sent.");
        }

        commands.forEach(command -> server.getCommandManager().handleCommand(server.getCommandSource(), command.replace("@p", user.getName())));
    }
}
