package io.musician101.morefish.sponge.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import java.util.List;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColor;

public final class SpongeCatchCommandExecutor implements SpongeCatchHandler {

    private final List<String> commands;

    public SpongeCatchCommandExecutor(@Nonnull List<String> commands) {
        this.commands = commands;
    }

    public void handle(@Nonnull Player catcher, @Nonnull Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fish) {
        commands.forEach(command -> Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command.replace("@p", catcher.getName())));
    }
}
