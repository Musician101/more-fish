package io.musician101.morefish.sponge.fishing.competition;

import io.musician101.morefish.common.fishing.competition.Prize;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.util.NumberUtils;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;

public final class SpongePrize extends Prize {

    public SpongePrize(@Nonnull List<String> commands) {
        super(commands);
    }

    @Override
    public void giveTo(@Nonnull UUID user, int rankNumber) {
        Sponge.server().userManager().find(user).ifPresent(u -> {
            if (!u.isOnline()) {
                SpongeMoreFish.getInstance().getLogger().warn(NumberUtils.ordinalOf(rankNumber) + " fisher " + u.name() + " isn't online! Contest prizes may not be sent.");
            }

            commands.forEach(command -> {
                try {
                    Sponge.server().commandManager().process(command.replace("@p", u.name()));
                }
                catch (CommandException e) {
                    SpongeMoreFish.getInstance().getLogger().error("Failed to run command.", e);
                }
            });
        });
    }
}
