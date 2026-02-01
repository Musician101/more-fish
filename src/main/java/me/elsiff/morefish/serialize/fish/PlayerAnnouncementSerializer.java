package me.elsiff.morefish.serialize.fish;

import me.elsiff.morefish.fish.PlayerAnnouncement;
import me.elsiff.morefish.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public class PlayerAnnouncementSerializer implements TypeSerializer<PlayerAnnouncement> {

    @Override
    public PlayerAnnouncement deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String string = node.node("type").getString();
        PlayerAnnouncement.Type announcementType = EnumUtils.get(string, PlayerAnnouncement.Type.class, PlayerAnnouncement.Type.NONE);
        double radius = node.node("radius").getDouble(0.1);
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
}
