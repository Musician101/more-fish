package me.elsiff.morefish.fishing.fishrecords;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class FishingRecordKeeper {

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
        return records.subList(0, Math.min(size, records.size()));
    }
}
