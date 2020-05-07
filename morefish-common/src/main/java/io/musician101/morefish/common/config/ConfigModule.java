package io.musician101.morefish.common.config;

import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;

public interface ConfigModule {

    void load(@Nonnull ConfigurationNode node);
}
