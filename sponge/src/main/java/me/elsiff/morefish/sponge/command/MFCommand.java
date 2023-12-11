package me.elsiff.morefish.sponge.command;

import io.musician101.spongecmd.CMDExecutor;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetitionHost;
import me.elsiff.morefish.sponge.shop.SpongeFishShop;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.configurate.ConfigurationNode;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;

public abstract class MFCommand implements CMDExecutor {

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

    protected boolean testAdmin(CommandContext context) {
        return context.hasPermission("morefish.admin");
    }
}
