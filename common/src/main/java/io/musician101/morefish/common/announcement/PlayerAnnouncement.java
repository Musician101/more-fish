package io.musician101.morefish.common.announcement;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

public interface PlayerAnnouncement {

    @Nonnull
    List<UUID> receiversOf(@Nonnull UUID catcher);

    record Serializer(@Nonnull PlayerAnnouncement serverAnnouncement,
                      @Nonnull Function<Double, RangedAnnouncement> rangedAnnouncementGetter) implements TypeSerializer<PlayerAnnouncement> {

        @Override
        public PlayerAnnouncement deserialize(Type type, ConfigurationNode node) {
            if (!type.equals(PlayerAnnouncement.class)) {
                return null;
            }

            double configuredValue = node.getDouble();
            return switch ((int) configuredValue) {
                case -2 -> new NoAnnouncement();
                case -1 -> serverAnnouncement;
                case 0 -> new BaseOnlyAnnouncement();
                default -> rangedAnnouncementGetter.apply(configuredValue);
            };
        }

        @Override
        public void serialize(Type type, @Nullable PlayerAnnouncement obj, ConfigurationNode node) {

        }
    }
}
