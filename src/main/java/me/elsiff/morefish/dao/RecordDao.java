package me.elsiff.morefish.dao;

import java.util.List;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.Record;

public interface RecordDao {

    @Nonnull
    List<Record> all();

    void clear();

    void insert(@Nonnull Record var1);

    @Nonnull
    List<Record> top(int var1);

    void update(@Nonnull Record var1);
}
