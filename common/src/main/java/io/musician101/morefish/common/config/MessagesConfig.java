package io.musician101.morefish.common.config;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import java.lang.reflect.Type;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public record MessagesConfig(@Nonnull PlayerAnnouncement announceCatch, @Nonnull PlayerAnnouncement announceNew1st,
                             boolean onlyAnnounceFishingRod, boolean broadcastStart, boolean broadcastStop,
                             boolean showTopOnEnding, @Nonnull String contestBarColor, int topNumber) {

    public boolean broadcastOnStart() {
        return broadcastStart;
    }

    public boolean broadcastOnStop() {
        return broadcastStop;
    }

    @Nonnull
    public PlayerAnnouncement getAnnounceCatch() {
        return announceCatch;
    }

    @Nonnull
    public PlayerAnnouncement getAnnounceNew1st() {
        return announceNew1st;
    }

    @Nonnull
    public String getContestBarColor() {
        return contestBarColor;
    }

    public int getTopNumber() {
        return topNumber;
    }

    public record Serializer(
            @Nonnull PlayerAnnouncement defaultAnnouncement) implements TypeSerializer<MessagesConfig> {

        @Override
        public MessagesConfig deserialize(Type type, ConfigurationNode node) throws SerializationException {
            if (!type.equals(MessagesConfig.class)) {
                PlayerAnnouncement announceCatch = node.node("announce-catch").get(PlayerAnnouncement.class, defaultAnnouncement);
                PlayerAnnouncement announceNew1st = node.node("announce-new-1st").get(PlayerAnnouncement.class, defaultAnnouncement);
                boolean onlyAnnounceFishingRod = node.node("only-announce-fishing-rod").getBoolean();
                boolean broadcastStart = node.node("broadcast-start").getBoolean(true);
                boolean broadcastStop = node.node("broadcast-stop").getBoolean(true);
                boolean showTopOnEnding = node.node("show-top-on-ending").getBoolean(true);
                String contestBarColor = node.node("contest-bar-color").getString("blue");
                int topNumber = node.node("top-number").getInt(3);
                return new MessagesConfig(announceCatch, announceNew1st, onlyAnnounceFishingRod, broadcastStart, broadcastStop, showTopOnEnding, contestBarColor, topNumber);
            }

            return null;
        }

        @Override
        public void serialize(Type type, @Nullable MessagesConfig obj, ConfigurationNode node) {

        }
    }
}
