package me.elsiff.morefish.paper.configuration;

import me.elsiff.morefish.common.configuration.Lang;
import me.elsiff.morefish.paper.fishing.PaperFish;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import org.bukkit.Bukkit;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;

public class PaperLang extends Lang<PaperFishingCompetition, PaperFish> {

    private PaperLang() {

    }

    public static PaperLang lang() {
        return new PaperLang();
    }

    protected PaperFishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    @Override
    protected String getPlayerName(int number) {
        return Bukkit.getOfflinePlayer(getCompetition().recordOf(number).fisher()).getName();
    }
}
