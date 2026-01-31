package me.elsiff.morefish.serialize.fish;

import me.elsiff.morefish.fish.FishAbstract;
import me.elsiff.morefish.fish.PlayerAnnouncement;
import me.elsiff.morefish.fish.condition.FishConditions;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.BiFunction;

@NullMarked
public abstract class FishAbstractSerializer<F extends FishAbstract<F>> implements TypeSerializer<F> {

    protected F deserialize(ConfigurationNode node, BiFunction<String, String, F> construct) throws SerializationException {
        String name = node.node("name").getString();
        if (name == null) {
            throw new SerializationException("name is missing from " + node.path());
        }

        String displayName = node.node("display-name").getString();
        if (displayName == null) {
            throw new SerializationException("display-name is missing from " + name);
        }

        F fishAbstract = construct.apply(name, displayName);
        fishAbstract.priceMultiplier(node.node("price-multiplier").getFloat(1));
        fishAbstract.announcement(node.node("announcement").get(PlayerAnnouncement.class, new PlayerAnnouncement(PlayerAnnouncement.Type.SERVER, 0.1)));
        fishAbstract.commands(node.node("commands").getList(String.class, List.of()));
        fishAbstract.conditions(node.node("conditions").get(FishConditions.class, new FishConditions()));
        fishAbstract.firework(node.node("firework").getBoolean());
        fishAbstract.noDisplay(node.node("no-display").getBoolean());
        fishAbstract.skipItemFormat(node.node("skip-item-format").getBoolean());
        fishAbstract.doNotSell(node.node("do-not-sell").getBoolean());
        return fishAbstract;
    }

    @Override
    public void serialize(Type type, @Nullable F obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            node.node("price-multiplier").set(obj.priceMultiplier());
            node.node("announcement").set(obj.announcement());
            node.node("commands").set(obj.commands());
            node.node("conditions").set(obj.conditions());
            node.node("display-name").set(obj.displayName());
            node.node("firework").set(obj.firework());
            node.node("name").set(obj.name());
            node.node("no-display").set(obj.noDisplay());
            node.node("skip-item-format").set(obj.skipItemFormat());
            node.node("do-not-sell").set(obj.doNotSell());
        }
    }
}
