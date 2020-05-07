package io.musician101.morefish.sponge.fishing.competition;

import io.musician101.morefish.common.fishing.competition.Prize;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.util.NumberUtils;
import java.util.List;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;

public final class SpongePrize extends Prize<User> {

    public SpongePrize(@Nonnull List<String> commands) {
        super(commands);
    }

    @Override
    public void giveTo(@Nonnull User user, int rankNumber) {
        if (!user.isOnline()) {
            SpongeMoreFish.getInstance().getLogger().warn(NumberUtils.ordinalOf(rankNumber) + " fisher " + user.getName() + " isn't online! Contest prizes may not be sent.");
        }

        commands.forEach(command -> Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command.replace("@p", user.getName())));
    }
}
