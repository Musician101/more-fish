package me.elsiff.morefish.dao;

import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;

public final class DaoFactory {

    public static final DaoFactory INSTANCE = new DaoFactory();
    private static MoreFish moreFish;

    private DaoFactory() {
    }

    @Nonnull
    public final YamlRecordDao getRecords() {
        return new YamlRecordDao(moreFish, moreFish.getFishTypeTable());
    }

    public final void init(@Nonnull MoreFish moreFish) {
        DaoFactory.moreFish = moreFish;
    }
}
