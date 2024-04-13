package me.elsiff.morefish.fishing.competition;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import me.elsiff.morefish.RecordHandler;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.hooker.MusiBoardHooker;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class FishingCompetition {

    private final RecordHandler records = new RecordHandler();

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
        records.clear();
    }

    public boolean containsContestant(@NotNull UUID contestant) {
        return getRanking().stream().anyMatch(record -> contestant.equals(record.fisher()));
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

    private MusiBoardHooker getMusiBoard() {
        return getPlugin().getMusiBoard();
    }

    @NotNull
    public List<Record> getRanking() {
        return records.all();
    }

    public List<Record> getRecords() {
        return records.all();
    }

    @NotNull
    public FishingCompetition.State getState() {
        return this.state;
    }

    public boolean isDisabled() {
        return this.state == FishingCompetition.State.DISABLED;
    }

    public boolean isEnabled() {
        return this.state == FishingCompetition.State.ENABLED;
    }

    public void putRecord(@NotNull Record record) {
        checkStateEnabled();
        records.add(record);
        getMusiBoard().update();
    }

    public int rankNumberOf(@NotNull Record record) {
        return getRanking().indexOf(record) + 1;
    }

    @NotNull
    public Entry<Integer, Record> rankedRecordOf(@NotNull OfflinePlayer contestant) {
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

    @NotNull
    public Record recordOf(@NotNull UUID contestant) {
        return getRanking().stream().filter(record -> contestant.equals(record.fisher())).findFirst().orElseThrow(() -> new IllegalStateException("Record not found"));
    }

    @NotNull
    public Record recordOf(int rankNumber) {
        if (rankNumber >= 1 && rankNumber <= getRanking().size()) {
            return getRanking().get(rankNumber - 1);
        }

        throw new IllegalArgumentException("Rank number is out of records size.");
    }

    @NotNull
    public List<Record> top(int size) {
        return records.top(size);
    }

    public boolean willBeNewFirst(@NotNull OfflinePlayer catcher, @NotNull Fish fish) {
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
