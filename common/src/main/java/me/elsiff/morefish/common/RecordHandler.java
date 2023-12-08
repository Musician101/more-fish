package me.elsiff.morefish.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.elsiff.morefish.common.fishing.competition.FishRecord;
import org.jetbrains.annotations.NotNull;

public final class RecordHandler<R extends FishRecord<?>> {

    @NotNull private final List<R> records = new ArrayList<>();

    @NotNull
    public List<R> all() {
        return records;
    }

    public void clear() {
        records.clear();
    }

    public void insert(@NotNull R record) {
        if (records.stream().anyMatch(r -> record.fisher().equals(r.fisher()))) {
            throw new IllegalArgumentException("Record must not exist in the ranking");
        }

        records.add(record);
        records.sort(Comparator.reverseOrder());
    }

    @NotNull
    public List<R> top(int size) {
        return all().subList(0, Math.min(size, all().size()));
    }

    public void update(@NotNull R record) {
        if (records.stream().noneMatch(r -> record.fisher().equals(r.fisher()))) {
            throw new IllegalArgumentException("Record must exist in the ranking");
        }

        records.removeIf(r -> r.fisher().equals(record.fisher()));
        records.sort(Comparator.reverseOrder());
    }
}
