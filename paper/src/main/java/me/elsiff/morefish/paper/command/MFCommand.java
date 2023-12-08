package me.elsiff.morefish.paper.command;

import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetitionHost;
import me.elsiff.morefish.paper.shop.PaperFishShop;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;

public class MFCommand {

    protected PaperFishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    protected PaperFishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    protected FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    protected PaperFishShop getFishShop() {
        return getPlugin().getFishShop();
    }

    protected boolean testAdmin(CommandSender sender) {
        return sender.hasPermission("morefish.admin");
    }
}
