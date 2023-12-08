package me.elsiff.morefish.sponge.configuration;

import me.elsiff.morefish.common.configuration.Lang;
import me.elsiff.morefish.sponge.fishing.SpongeFish;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;

public class SpongeLang extends Lang<SpongeFishingCompetition, SpongeFish> {

    private SpongeLang() {

    }

    public static SpongeLang lang() {
        return new SpongeLang();
    }

    protected SpongeFishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }

    @Override
    protected String getPlayerName(int number) {
        return Sponge.server().gameProfileManager().cache().findById(getCompetition().recordOf(number).fisher()).flatMap(GameProfile::name).orElse("null");
    }
}
