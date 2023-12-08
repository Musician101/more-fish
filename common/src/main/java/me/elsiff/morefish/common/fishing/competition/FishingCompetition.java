package me.elsiff.morefish.common.fishing.competition;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import me.elsiff.morefish.common.RecordHandler;
import me.elsiff.morefish.common.fishing.Fish;
import org.jetbrains.annotations.NotNull;

public abstract class FishingCompetition<F extends Fish<?>> {

    @NotNull private FishingCompetition.State state = State.DISABLED;

    private void checkStateDisabled() {
        if (state != State.DISABLED) {
            throw new IllegalStateException("Fishing competition isn't disabled");
        }
    }

    private void checkStateEnabled() {
        if (state != State.ENABLED) {
            throw new IllegalStateException("Fishing competition isn't enabled");
        }
    }

    public void clearRecords() {
        getRecords().clear();
    }

    public boolean containsContestant(@NotNull UUID contestant) {
        return getRanking().stream().anyMatch(record -> contestant.equals(record.fisher()));
    }

    public void disable() {
        this.checkStateEnabled();
        this.state = State.DISABLED;
    }

    public void enable() {
        this.checkStateDisabled();
        this.state = State.ENABLED;
    }

    @NotNull
    public List<FishRecord<F>> getRanking() {
        return this.getRecords().all();
    }

    private RecordHandler<FishRecord<F>> getRecords() {
        return new RecordHandler<>();
    }

    @NotNull
    public FishingCompetition.State getState() {
        return this.state;
    }

    public boolean isDisabled() {
        return this.state == State.DISABLED;
    }

    public boolean isEnabled() {
        return this.state == State.ENABLED;
    }

    public void putRecord(@NotNull FishRecord<F> record) {
        checkStateEnabled();
        if (containsContestant(record.fisher())) {
            FishRecord<F> oldRecord = recordOf(record.fisher());
            if (record.fish().length() > oldRecord.fish().length()) {
                getRecords().update(record);
            }
        }
        else {
            getRecords().insert(record);
        }
    }

    public int rankNumberOf(@NotNull FishRecord<F> record) {
        return getRanking().indexOf(record) + 1;
    }

    @NotNull
    public Entry<Integer, FishRecord<F>> rankedRecordOf(@NotNull UUID contestant) {
        List<FishRecord<F>> records = getRanking();
        int place = 0;
        for (FishRecord<F> record : records) {
            if (record.fisher().equals(contestant)) {
                return new SimpleEntry<>(place, record);
            }

            place++;
        }

        throw new IllegalStateException("Record not found");
    }

    @NotNull
    public FishRecord<F> recordOf(@NotNull UUID contestant) {
        return getRanking().stream().filter(record -> contestant.equals(record.fisher())).findFirst().orElseThrow(() -> new IllegalStateException("Record not found"));
    }

    @NotNull
    public FishRecord<F> recordOf(int rankNumber) {
        if (rankNumber >= 1 && rankNumber <= getRanking().size()) {
            return getRanking().get(rankNumber - 1);
        }

        throw new IllegalArgumentException("Rank number is out of records size.");
    }

    @NotNull
    public List<FishRecord<F>> top(int size) {
        return getRecords().top(size);
    }

    public boolean willBeNewFirst(@NotNull UUID catcher, @NotNull F fish) {
        if (!getRanking().isEmpty()) {
            FishRecord<F> record = getRanking().get(0);
            return fish.length() > record.fish().length() && !record.fisher().equals(catcher);
        }

        return true;
    }

    public enum State {
        ENABLED,
        DISABLED
    }
}
