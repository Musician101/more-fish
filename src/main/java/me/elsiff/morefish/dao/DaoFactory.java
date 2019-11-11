package me.elsiff.morefish.dao;

import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.dao.yaml.YamlRecordDao;

public final class DaoFactory {

    public static final DaoFactory INSTANCE = new DaoFactory();
    private static MoreFish moreFish;

    private DaoFactory() {
    }

    @Nonnull
    public final RecordDao getRecords() {
        return new YamlRecordDao(moreFish, moreFish.getFishTypeTable());
    }

    public final void init(@Nonnull MoreFish moreFish) {
        DaoFactory.moreFish = moreFish;
    }
}
