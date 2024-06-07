package me.elsiff.morefish.text.tagresolver;

import me.elsiff.morefish.competition.FishingCompetition;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;

public abstract class CompetitionTagResolver implements TagResolver {

    @NotNull
    protected FishingCompetition getCompetition() {
        return getPlugin().getCompetition();
    }
}
