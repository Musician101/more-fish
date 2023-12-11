package me.elsiff.morefish.sponge.fishing.competition;

import java.util.List;
import java.util.UUID;
import me.elsiff.morefish.common.fishing.competition.Prize;
import me.elsiff.morefish.common.util.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;

import static me.elsiff.morefish.common.configuration.Lang.PREFIX;
import static me.elsiff.morefish.common.configuration.Lang.join;
import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public final class SpongePrize extends Prize {

    public SpongePrize(@NotNull List<String> commands) {
        super(commands);
    }

    public void giveTo(@NotNull UUID player, int rankNumber) {
        Sponge.server().gameProfileManager().cache().findById(player).ifPresent(profile -> Sponge.server().player(profile.uniqueId()).map(p -> {
            String name = p.name();
            commands.forEach(command -> {
                try {
                    Sponge.server().commandManager().process(command.replace("@p", name == null ? "null" : name));
                }
                catch (CommandException e) {
                    p.sendMessage(join(PREFIX, text("Seems the fish you caught has a broken catch handler. Please contact server staff.", RED)));
                }
            });
            return p;
        }).orElseGet(() -> {
            getPlugin().getLogger().warn(NumberUtils.ordinalOf(rankNumber) + " fisher " + profile.name().orElse(profile.uuid().toString()) + " isn't online! Contest prizes may not be sent.");
            return null;
        }));
    }
}
