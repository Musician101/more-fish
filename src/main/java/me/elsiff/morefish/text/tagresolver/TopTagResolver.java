package me.elsiff.morefish.text.tagresolver;

import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class TopTagResolver extends CompetitionTagResolver {

    @Nullable
    protected Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, Function<FishRecord, String> getter) {
        if (!has(name)) {
            return null;
        }

        return arguments.popOr("Rank number is required").asInt().stream().filter(i -> getCompetition().getRecords().size() >= i).mapToObj(getCompetition()::recordOf).map(getter).map(Component::text).map(Tag::selfClosingInserting).findFirst().orElse(null);
    }
}
