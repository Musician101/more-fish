package me.elsiff.morefish.fishing.competition;

import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import me.elsiff.morefish.fishing.fishrecords.FishRecordKeeper;
import me.elsiff.morefish.hooker.MusiBoardHooker;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class FishingCompetition extends FishRecordKeeper {

    @NotNull
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

    public boolean containsContestant(@NotNull UUID contestant) {
        return getRecords().stream().anyMatch(record -> contestant.equals(record.fisher()));
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
    public FishingCompetition.State getState() {
        return this.state;
    }

    public boolean isDisabled() {
        return this.state == FishingCompetition.State.DISABLED;
    }

    public boolean isEnabled() {
        return this.state == FishingCompetition.State.ENABLED;
    }

    @Override
    public void add(@NotNull FishRecord record) {
        checkStateEnabled();
        Optional<FishRecord> optional = getRecord(record.fisher());
        if (optional.isPresent()) {
            optional.filter(r -> record.getLength() >= r.getLength()).ifPresent(r -> {
                records.remove(optional.get());
                records.add(record);
            });
        }
        else {
            records.add(record);
        }

        getMusiBoard().update();
    }

    public int rankNumberOf(@NotNull FishRecord record) {
        return getRecords().indexOf(record) + 1;
    }

    @NotNull
    public Entry<Integer, FishRecord> rankedRecordOf(@NotNull OfflinePlayer contestant) {
        List<FishRecord> records = getRecords();
        int place = 0;
        for (FishRecord record : records) {
            if (record.fisher().equals(contestant.getUniqueId())) {
                return new SimpleEntry<>(place, record);
            }

            place++;
        }

        throw new IllegalStateException("Record not found");
    }

    private Optional<FishRecord> getRecord(@NotNull UUID contestant) {
        return records.stream().filter(record -> contestant.equals(record.fisher())).findFirst();
    }

    @NotNull
    public FishRecord recordOf(@NotNull UUID contestant) {
        return getRecord(contestant).orElseThrow(() -> new IllegalStateException("Record not found"));
    }

    @NotNull
    public FishRecord recordOf(int rankNumber) {
        if (rankNumber >= 1 && rankNumber <= getRecords().size()) {
            return getRecords().get(rankNumber - 1);
        }

        throw new IllegalArgumentException("Rank number is out of records size.");
    }

    public boolean willBeNewFirst(@NotNull OfflinePlayer catcher, @NotNull Fish fish) {
        if (!getRecords().isEmpty()) {
            FishRecord record = getRecords().get(0);
            return fish.length() > record.getLength() && !record.fisher().equals(catcher.getUniqueId());
        }

        return true;
    }

    public enum State {
        ENABLED,
        DISABLED
    }
}
