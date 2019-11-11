package me.elsiff.morefish.fishing.catchhandler;

import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.configuration.format.TextFormat;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import org.bukkit.entity.Player;

public final class NewFirstBroadcaster extends AbstractBroadcaster {

    private final FishingCompetition competition;

    public NewFirstBroadcaster(@Nonnull FishingCompetition competition) {
        super();
        this.competition = competition;
    }

    @Nonnull
    public PlayerAnnouncement announcement(@Nonnull Fish fish) {
        return Config.INSTANCE.getNewFirstAnnouncement();
    }

    @Nonnull
    public TextFormat getCatchMessageFormat() {
        return Lang.INSTANCE.format("get-1st");
    }

    public boolean meetBroadcastCondition(@Nonnull Player catcher, @Nonnull Fish fish) {
        return this.competition.isEnabled() && this.competition.willBeNewFirst(catcher, fish);
    }
}
