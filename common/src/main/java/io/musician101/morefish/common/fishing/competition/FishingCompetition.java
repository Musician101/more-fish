package io.musician101.morefish.common.fishing.competition;

import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.Records;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class FishingCompetition {

    @Nonnull
    private final Records records;
    @Nonnull
    private State state = State.DISABLED;

    public FishingCompetition(@Nonnull Records records) {
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

    public void clearRecords() {
        records.clear();
    }

    public boolean containsContestant(@Nonnull UUID contestant) {
        return getRanking().stream().anyMatch(record -> contestant.equals(record.getFisher()));
    }

    public void disable() {
        this.checkStateEnabled();
        this.state = State.DISABLED;
    }

    public void enable() {
        this.checkStateDisabled();
        this.state = State.ENABLED;
    }

    @Nonnull
    public List<Record> getRanking() {
        return this.records.all();
    }

    @Nonnull
    public State getState() {
        return this.state;
    }

    public boolean isDisabled() {
        return this.state == State.DISABLED;
    }

    public boolean isEnabled() {
        return this.state == State.ENABLED;
    }

    public void putRecord(@Nonnull Record record) {
        checkStateEnabled();
        if (containsContestant(record.getFisher())) {
            Record oldRecord = recordOf(record.getFisher());
            if (record.getFish().getLength() > oldRecord.getFish().getLength()) {
                records.update(record);
            }
        }
        else {
            records.insert(record);
        }

    }

    public int rankNumberOf(@Nonnull Record record) {
        return getRanking().indexOf(record) + 1;
    }

    @Nonnull
    public Entry<Integer, Record> rankedRecordOf(@Nonnull UUID contestant) {
        List<Record> records = getRanking();
        int place = 0;
        for (Record record : records) {
            if (contestant.equals(record.getFisher())) {
                return new SimpleEntry<>(place, record);
            }

            place++;
        }

        throw new IllegalStateException("Record not found");
    }

    @Nonnull
    public Record recordOf(@Nonnull UUID contestant) {
        return getRanking().stream().filter(record -> contestant.equals(record.getFisher())).findFirst().orElseThrow(() -> new IllegalStateException("Record not found"));
    }

    @Nonnull
    public Record recordOf(int rankNumber) {
        if (rankNumber >= 1 && rankNumber <= getRanking().size()) {
            return getRanking().get(rankNumber - 1);
        }

        throw new IllegalArgumentException("Rank number is out of records size.");
    }

    @Nonnull
    public List<Record> top(int size) {
        return records.top(size);
    }

    public boolean willBeNewFirst(@Nonnull UUID catcher, @Nonnull Fish fish) {
        if (!getRanking().isEmpty()) {
            Record record = getRanking().get(0);
            return fish.getLength() > record.getFish().getLength() && !catcher.equals(record.getFisher());
        }

        return true;
    }

    public enum State {
        ENABLED,
        DISABLED
    }
}
