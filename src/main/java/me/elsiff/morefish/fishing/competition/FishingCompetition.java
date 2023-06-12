package me.elsiff.morefish.fishing.competition;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import javax.annotation.Nonnull;
import me.elsiff.morefish.RecordHandler;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.hooker.MusiBoardHooker;
import org.bukkit.OfflinePlayer;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class FishingCompetition {

    @Nonnull
    private FishingCompetition.State state = State.DISABLED;

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

    public boolean containsContestant(@Nonnull UUID contestant) {
        return getRanking().stream().anyMatch(record -> contestant.equals(record.fisher()));
    }

    private MusiBoardHooker getMusiBoard() {
        return getPlugin().getMusiBoard();
    }

    public void disable() {
        this.checkStateEnabled();
        getMusiBoard().clear();
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
        if (containsContestant(record.fisher())) {
            Record oldRecord = recordOf(record.fisher());
            if (record.fish().length() > oldRecord.fish().length()) {
                getRecords().update(record);
            }
        }
        else {
            getRecords().insert(record);
        }

        getMusiBoard().update();
    }

    public int rankNumberOf(@Nonnull Record record) {
        return getRanking().indexOf(record) + 1;
    }

    @Nonnull
    public Entry<Integer, Record> rankedRecordOf(@Nonnull OfflinePlayer contestant) {
        List<Record> records = getRanking();
        int place = 0;
        for (Record record : records) {
            if (record.fisher().equals(contestant.getUniqueId())) {
                return new SimpleEntry<>(place, record);
            }

            place++;
        }

        throw new IllegalStateException("Record not found");
    }

    @Nonnull
    public Record recordOf(@Nonnull UUID contestant) {
        return getRanking().stream().filter(record -> contestant.equals(record.fisher())).findFirst().orElseThrow(() -> new IllegalStateException("Record not found"));
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
            return fish.length() > record.fish().length() && !record.fisher().equals(catcher.getUniqueId());
        }

        return true;
    }

    public enum State {
        ENABLED,
        DISABLED
    }
}
