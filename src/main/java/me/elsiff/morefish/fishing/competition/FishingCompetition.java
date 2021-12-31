package me.elsiff.morefish.fishing.competition;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import javax.annotation.Nonnull;
import me.elsiff.morefish.RecordHandler;
import me.elsiff.morefish.fishing.Fish;
import org.bukkit.OfflinePlayer;

public final class FishingCompetition {

    @Nonnull
    private FishingCompetition.State state = State.DISABLED;

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
        getRecords().clear();
    }

    public boolean containsContestant(@Nonnull UUID contestant) {
        return getRanking().stream().anyMatch(record -> contestant.equals(record.getFisher()));
    }

    public void disable() {
        this.checkStateEnabled();
        this.state = FishingCompetition.State.DISABLED;
    }

    public void enable() {
        this.checkStateDisabled();
        this.state = FishingCompetition.State.ENABLED;
    }

    @Nonnull
    public List<Record> getRanking() {
        return this.getRecords().all();
    }

    private RecordHandler getRecords() {
        return new RecordHandler();
    }

    @Nonnull
    public FishingCompetition.State getState() {
        return this.state;
    }

    public boolean isDisabled() {
        return this.state == FishingCompetition.State.DISABLED;
    }

    public boolean isEnabled() {
        return this.state == FishingCompetition.State.ENABLED;
    }

    public void putRecord(@Nonnull Record record) {
        checkStateEnabled();
        if (containsContestant(record.getFisher())) {
            Record oldRecord = recordOf(record.getFisher());
            if (record.getFish().getLength() > oldRecord.getFish().getLength()) {
                getRecords().update(record);
            }
        }
        else {
            getRecords().insert(record);
        }

    }

    public int rankNumberOf(@Nonnull Record record) {
        return getRanking().indexOf(record) + 1;
    }

    @Nonnull
    public Entry<Integer, Record> rankedRecordOf(@Nonnull OfflinePlayer contestant) {
        List<Record> records = getRanking();
        int place = 0;
        for (Record record : records) {
            if (record.getFisher().equals(contestant.getUniqueId())) {
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
        return getRecords().top(size);
    }

    public boolean willBeNewFirst(@Nonnull OfflinePlayer catcher, @Nonnull Fish fish) {
        if (!getRanking().isEmpty()) {
            Record record = getRanking().get(0);
            return fish.getLength() > record.getFish().getLength() && !record.getFisher().equals(catcher.getUniqueId());
        }

        return true;
    }

    public enum State {
        ENABLED,
        DISABLED
    }
}
