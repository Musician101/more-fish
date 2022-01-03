package io.musician101.morefish.common.fishing;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.fishing.catchhandler.CatchCommandExecutor;
import io.musician101.morefish.common.fishing.catchhandler.CatchFireworkSpawner;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public record FishRarity(@Nonnull String name, @Nonnull String displayName, boolean isDefault, double probability,
                         @Nonnull String color, @Nonnull List<CatchHandler> catchHandlers,
                         @Nonnull PlayerAnnouncement catchAnnouncement, boolean hasNotFishItemFormat, boolean noDisplay,
                         boolean hasCatchFirework, double additionalPrice) implements Comparable<FishRarity> {

    public static FishRarity deserialize(@Nonnull ConfigurationNode node, PlayerAnnouncement catchAnnouncement) throws SerializationException {
        String name = node.key().toString();
        String displayName = node.node("display-name").getString();
        if (displayName == null) {
            throw new SerializationException("Fish Rarity display name can not be null");
        }

        boolean isDefault = node.node("default").getBoolean();
        double probability = node.node("chance").getDouble() / 100;
        String color = node.node("color").getString();
        if (color == null) {
            throw new SerializationException("Fish Rarity color can not be null");
        }

        List<CatchHandler> catchHandlers = new ArrayList<>();
        catchHandlers.add(node.node("commands").get(CatchCommandExecutor.class));
        CatchFireworkSpawner fireworkSpawner = node.node("firework").get(CatchFireworkSpawner.class);
        if (fireworkSpawner != null) {
            catchHandlers.add(fireworkSpawner);
        }

        boolean hasNotFishItemFormat = node.node("skip-item-format").getBoolean();
        boolean noDisplay = node.node("no-display").getBoolean();
        boolean hasCatchFirework = fireworkSpawner != null;
        double additionalPrice = node.node("additional-price").getDouble();
        return new FishRarity(name, displayName, isDefault, probability, color, catchHandlers, catchAnnouncement, hasNotFishItemFormat, noDisplay, hasCatchFirework, additionalPrice);
    }

    @Override
    public int compareTo(@Nonnull FishRarity o) {
        if (isDefault) {
            return 0;
        }

        return Double.compare(this.probability, o.probability);
    }

    public final double getAdditionalPrice() {
        return this.additionalPrice;
    }

    @Nonnull
    public final PlayerAnnouncement getCatchAnnouncement() {
        return this.catchAnnouncement;
    }

    @Nonnull
    public final List<CatchHandler> getCatchHandlers() {
        return this.catchHandlers;
    }

    @Nonnull
    public final String getColor() {
        return this.color;
    }

    @Nonnull
    public final String getDisplayName() {
        return this.displayName;
    }

    @Nonnull
    public final String getName() {
        return this.name;
    }

    public final boolean getNoDisplay() {
        return this.noDisplay;
    }

    public final double getProbability() {
        return this.probability;
    }
}
