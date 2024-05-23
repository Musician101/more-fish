package me.elsiff.morefish.fishing.competition;

import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import me.elsiff.morefish.fishing.fishrecords.FishRecordKeeper;
import me.elsiff.morefish.hooker.MusiBoardHooker;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class FishingCompetition extends FishRecordKeeper {

    private boolean enabled = false;

    public void disable() {
        enabled = false;
        getMusiBoard().clear();
    }

    public void enable() {
        enabled = true;
    }

    private MusiBoardHooker getMusiBoard() {
        return getPlugin().getMusiBoard();
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void add(@NotNull FishRecord record) {
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
            List<FishRecord> records = getRecords();
            records.sort(SortType.LENGTH.reversed());
            FishRecord record = records.getFirst();
            return fish.length() > record.getLength() && !record.fisher().equals(catcher.getUniqueId());
        }

        return true;
    }
}
