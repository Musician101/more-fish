package me.elsiff.morefish.fishing.fishrecords;

import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class FishRecordKeeper {

    @NotNull
    protected final List<FishRecord> records = new ArrayList<>();

    public void clear() {
        records.clear();
    }

    public void clearRecordHolder(@NotNull UUID holder) {
        records.removeIf(record -> holder.equals(record.fisher()));
    }

    @NotNull
    public List<FishRecord> getRecords() {
        return new ArrayList<>(records);
    }

    public void add(@NotNull FishRecord record) {
        records.add(record);
    }

    @NotNull
    public List<FishRecord> top(int size) {
        return top(size, SortType.LENGTH.sorter().reversed());
    }

    @NotNull
    public List<FishRecord> top(int size, @NotNull Comparator<FishRecord> sorter) {
        List<FishRecord> records = new ArrayList<>(this.records);
        records.sort(sorter);
        return records.subList(0, Math.min(size, records.size()));
    }

    public boolean contains(@NotNull UUID uuid) {
        return records.stream().anyMatch(r -> r.fisher().equals(uuid));
    }

    @NotNull
    public Map.Entry<Integer, FishRecord> rankedRecordOf(@NotNull OfflinePlayer contestant) {
        return rankedRecordOf(contestant, SortType.LENGTH.sorter().reversed());
    }

    @NotNull
    public Map.Entry<Integer, FishRecord> rankedRecordOf(@NotNull OfflinePlayer contestant, Comparator<FishRecord> sorter) {
        List<FishRecord> records = new ArrayList<>(getRecords());
        records.sort(sorter);
        int place = 0;
        for (FishRecord record : records) {
            if (record.fisher().equals(contestant.getUniqueId())) {
                return new SimpleEntry<>(place, record);
            }

            place++;
        }

        throw new IllegalStateException("Record not found");
    }
}
