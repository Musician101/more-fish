package me.elsiff.morefish.text.tagresolver;

import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class PlayerTagResolver extends CompetitionTagResolver {

    @Nullable
    private final UUID uuid;

    protected PlayerTagResolver(@Nullable UUID uuid) {
        this.uuid = uuid;
    }

    @NotNull
    protected Optional<FishRecord> recordOf() {
        if (uuid == null) {
            return Optional.empty();
        }

        return Optional.of(getCompetition().recordOf(uuid));
    }
}
