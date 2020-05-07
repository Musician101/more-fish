package io.musician101.morefish.common.fishing.competition;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.Records;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class FishingCompetition<A extends PlayerAnnouncement<?>, B, C extends FishCondition<?, ?>, H extends CatchHandler<?, ?>, I> {

    @Nonnull
    private final Records<A, B, C, H, I> records;
    @Nonnull
    private State state = State.DISABLED;

    public FishingCompetition(@Nonnull Records<A, B, C, H, I> records) {
        this.records = records;
    }

    private void checkStateDisabled() {
        if (state != State.DISABLED) {
            throw new IllegalStateException("Fishing competition hasn't disabled");
        }
    }

    private void checkStateEnabled() {
        if (state != State.ENABLED) {
            throw new IllegalStateException("Fishing competition hasn't enabled");
        }
    }

    public final void clearRecords() {
        records.clear();
    }

    public final boolean containsContestant(@Nonnull UUID contestant) {
        return getRanking().stream().anyMatch(record -> contestant.equals(record.getFisher()));
    }

    public final void disable() {
        this.checkStateEnabled();
        this.state = State.DISABLED;
    }

    public final void enable() {
        this.checkStateDisabled();
        this.state = State.ENABLED;
    }

    @Nonnull
    public final List<Record<A, B, C, H, I>> getRanking() {
        return this.records.all();
    }

    @Nonnull
    public final State getState() {
        return this.state;
    }

    public final boolean isDisabled() {
        return this.state == State.DISABLED;
    }

    public final boolean isEnabled() {
        return this.state == State.ENABLED;
    }

    public final void putRecord(@Nonnull Record<A, B, C, H, I> record) {
        checkStateEnabled();
        if (containsContestant(record.getFisher())) {
            Record<A, B, C, H, I> oldRecord = recordOf(record.getFisher());
            if (record.getFish().getLength() > oldRecord.getFish().getLength()) {
                records.update(record);
            }
        }
        else {
            records.insert(record);
        }

    }

    public final int rankNumberOf(@Nonnull Record<A, B, C, H, I> record) {
        return getRanking().indexOf(record) + 1;
    }

    @Nonnull
    public final Entry<Integer, Record<A, B, C, H, I>> rankedRecordOf(@Nonnull UUID contestant) {
        List<Record<A, B, C, H, I>> records = getRanking();
        int place = 0;
        for (Record<A, B, C, H, I> record : records) {
            if (contestant.equals(record.getFisher())) {
                return new SimpleEntry<>(place, record);
            }

            place++;
        }

        throw new IllegalStateException("Record not found");
    }

    @Nonnull
    public final Record<A, B, C, H, I> recordOf(@Nonnull UUID contestant) {
        return getRanking().stream().filter(record -> contestant.equals(record.getFisher())).findFirst().orElseThrow(() -> new IllegalStateException("Record not found"));
    }

    @Nonnull
    public final Record<A, B, C, H, I> recordOf(int rankNumber) {
        if (rankNumber >= 1 && rankNumber <= getRanking().size()) {
            return getRanking().get(rankNumber - 1);
        }

        throw new IllegalArgumentException("Rank number is out of records size.");
    }

    @Nonnull
    public final List<Record<A, B, C, H, I>> top(int size) {
        return records.top(size);
    }

    public final boolean willBeNewFirst(@Nonnull UUID catcher, @Nonnull Fish<A, B, C, H, I> fish) {
        if (!getRanking().isEmpty()) {
            Record<A, B, C, H, I> record = getRanking().get(0);
            return fish.getLength() > record.getFish().getLength() && !catcher.equals(record.getFisher());
        }

        return true;
    }

    public enum State {
        ENABLED,
        DISABLED
    }
}
