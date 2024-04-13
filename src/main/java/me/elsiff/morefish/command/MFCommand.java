package me.elsiff.morefish.command;

import me.elsiff.morefish.RecordHandler;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.FishingCompetitionHost;
import me.elsiff.morefish.shop.FishShop;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import static me.elsiff.morefish.MoreFish.getPlugin;

public class MFCommand {

    protected RecordHandler getAllTimeRecords() {
        return getPlugin().getAllTimeRecords();
    }

    protected FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    protected FishingCompetitionHost getCompetitionHost() {
        return getPlugin().getCompetitionHost();
    }

    protected FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    protected FishShop getFishShop() {
        return getPlugin().getFishShop();
    }

    protected boolean testAdmin(CommandSender sender) {
        return sender.hasPermission("morefish.admin");
    }
}
