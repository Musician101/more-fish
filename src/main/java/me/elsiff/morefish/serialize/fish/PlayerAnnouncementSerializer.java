package me.elsiff.morefish.serialize.fish;

import me.elsiff.morefish.fish.PlayerAnnouncement;
import me.elsiff.morefish.serialize.ConfigKey;
import me.elsiff.morefish.serialize.ConfigKey.RequiredKey;
import me.elsiff.morefish.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public class PlayerAnnouncementSerializer implements TypeSerializer<PlayerAnnouncement> {

    private static final RequiredKey<String> TYPE = ConfigKey.requiredKey("type", String.class);
    private static final RequiredKey<Float> RADIUS = ConfigKey.requiredKey("radius", Float.class);

    @Override
    public @Nullable PlayerAnnouncement deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.isNull()) {
            return null;
        }

        getPlugin().getSLF4JLogger().error(node.toString());
        PlayerAnnouncement.Type announcementType = EnumUtils.getOrThrow(TYPE.get(node), PlayerAnnouncement.Type.class, new SerializationException("type in " + node.path() + " is not a valid announcement type."));
        double radius = RADIUS.get(node);
        if (radius > 0) {
            return new PlayerAnnouncement(announcementType, radius);
        }

        throw new SerializationException("Announcement radius must be a number greater than 0.");
    }

    @Override
    public void serialize(Type type, @Nullable PlayerAnnouncement obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.node("type").set(obj.type().toString().toLowerCase());
            node.node("radius").set(obj.radius());
        }
    }

    public static class PlayerAnnouncementTypeSerializer implements TypeSerializer<PlayerAnnouncement.Type> {

        @Override
        public PlayerAnnouncement.Type deserialize(Type type, ConfigurationNode node) throws SerializationException {
            return EnumUtils.getOrThrow(TYPE.get(node), PlayerAnnouncement.Type.class, new SerializationException("type in " + node.path() + " is not a valid announcement type."));
        }

        @Override
        public void serialize(Type type, PlayerAnnouncement.@Nullable Type obj, ConfigurationNode node) throws SerializationException {
            node.set(obj == null ? null : obj.toString().toLowerCase());
        }
    }
}
