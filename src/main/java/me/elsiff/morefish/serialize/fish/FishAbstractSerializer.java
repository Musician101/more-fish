package me.elsiff.morefish.serialize.fish;

import io.leangen.geantyref.TypeFactory;
import me.elsiff.morefish.fish.FishAbstract;
import me.elsiff.morefish.fish.PlayerAnnouncement;
import me.elsiff.morefish.fish.condition.FishConditions;
import me.elsiff.morefish.serialize.ConfigKey;
import me.elsiff.morefish.serialize.ConfigKey.NonRequiredKey;
import me.elsiff.morefish.serialize.ConfigKey.RequiredKey;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.BiFunction;

@NullMarked
public abstract class FishAbstractSerializer<F extends FishAbstract<F>> implements TypeSerializer<F> {

    private static final NonRequiredKey<PlayerAnnouncement> ANNOUNCEMENT = ConfigKey.nonRequiredKey("announcement", PlayerAnnouncement.class, PlayerAnnouncement.DEFAULT);
    private static final NonRequiredKey<List<String>> COMMANDS = ConfigKey.nonRequiredKey("commands", TypeFactory.parameterizedClass(List.class, String.class), List.of());
    private static final NonRequiredKey<FishConditions> CONDITIONS = ConfigKey.nonRequiredKey("conditions", FishConditions.class, new FishConditions());
    private static final RequiredKey<String> DISPLAY_NAME = ConfigKey.requiredKey("display-name", String.class);
    private static final NonRequiredKey<Boolean> DO_NOT_SELL = ConfigKey.nonRequiredKey("do-not-sell", Boolean.class, false);
    private static final NonRequiredKey<Boolean> FIREWORK = ConfigKey.nonRequiredKey("firework", Boolean.class, false);
    private static final RequiredKey<NamespacedKey> ID = ConfigKey.requiredKey("id", NamespacedKey.class);
    private static final NonRequiredKey<Boolean> NO_DISPLAY = ConfigKey.nonRequiredKey("no-display", Boolean.class, false);
    private static final NonRequiredKey<Float> PRICE_MULTIPLIER = ConfigKey.nonRequiredKey("priceMultiplier", Float.class, 1F);
    private static final NonRequiredKey<Boolean> SKIP_ITEM_FORMAT = ConfigKey.nonRequiredKey("skip-item-format", Boolean.class, false);

    protected F deserialize(ConfigurationNode node, BiFunction<NamespacedKey, String, F> construct) throws SerializationException {
        NamespacedKey key = ID.get(node);
        String displayName = DISPLAY_NAME.get(node);
        F fishAbstract = construct.apply(key, displayName);
        fishAbstract.announcement(ANNOUNCEMENT.get(node));
        fishAbstract.commands(COMMANDS.get(node));
        fishAbstract.conditions(CONDITIONS.get(node));
        fishAbstract.doNotSell(DO_NOT_SELL.get(node));
        fishAbstract.firework(FIREWORK.get(node));
        fishAbstract.noDisplay(NO_DISPLAY.get(node));
        fishAbstract.priceMultiplier(PRICE_MULTIPLIER.get(node));
        fishAbstract.skipItemFormat(SKIP_ITEM_FORMAT.get(node));
        return fishAbstract;
    }

    @Override
    public void serialize(Type type, @Nullable F obj, ConfigurationNode node) throws SerializationException {
        if (obj != null) {
            ANNOUNCEMENT.set(node, obj.announcement());
            COMMANDS.set(node, obj.commands());
            CONDITIONS.set(node, obj.conditions());
            DISPLAY_NAME.set(node, obj.displayName());
            DO_NOT_SELL.set(node, obj.doNotSell());
            FIREWORK.set(node, obj.firework());
            ID.set(node, obj.getKey());
            NO_DISPLAY.set(node, obj.noDisplay());
            SKIP_ITEM_FORMAT.set(node, obj.skipItemFormat());
            PRICE_MULTIPLIER.set(node, obj.priceMultiplier());
        }
    }
}
