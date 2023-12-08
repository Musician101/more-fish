package me.elsiff.morefish.sponge.command;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.elsiff.morefish.common.configuration.Lang;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetitionHost;
import me.elsiff.morefish.sponge.shop.SpongeFishShop;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.configurate.ConfigurationNode;

import static me.elsiff.morefish.common.configuration.Lang.join;
import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public abstract class MFCommand implements CommandExecutor {

    public boolean canUse(@NotNull CommandContext context) {
        return true;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        return CommandResult.error(join(Lang.PREFIX, text("Unknown or incomplete command, see below for error", RED)));
    }

    @NotNull
    public List<MFCommand> getChildren() {
        return List.of();
    }

    protected SpongeFishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    protected SpongeFishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    protected ConfigurationNode getConfig() {
        return getPlugin().getConfig();
    }

    protected SpongeFishShop getFishShop() {
        return getPlugin().getFishShop();
    }

    @NotNull
    public abstract String getName();

    @NotNull
    public List<Parameter> getParameters() {
        return List.of();
    }

    @NotNull
    public Optional<String> getPermission() {
        return Optional.empty();
    }

    protected boolean testAdmin(CommandContext context) {
        return context.hasPermission("morefish.admin");
    }

    @NotNull
    public Command.Parameterized toCommand() {
        Command.Builder builder = Command.builder().executor(this).addChildren(getChildren().stream().collect(Collectors.toMap(c -> List.of(c.getName()), MFCommand::toCommand))).addParameters(getParameters());
        getPermission().ifPresent(builder::permission);
        return builder.build();
    }
}
