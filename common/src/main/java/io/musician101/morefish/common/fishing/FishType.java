package io.musician101.morefish.common.fishing;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.fishing.catchhandler.CatchCommandExecutor;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public record FishType(@Nonnull String name, @Nonnull FishRarity rarity, @Nonnull String displayName, double lengthMin,
                       double lengthMax, @Nonnull ConfigurationNode icon, @Nonnull List<CatchHandler> catchHandlers,
                       @Nonnull PlayerAnnouncement catchAnnouncement, @Nonnull List<FishCondition> conditions,
                       boolean hasNotFishItemFormat, boolean noDisplay, boolean hasCatchFirework) {

    public static FishType deserialize(@Nonnull FishRarity fishRarity, @Nonnull ConfigurationNode node) throws SerializationException {
        String name = node.key().toString();
        String displayName = node.node("display-name").getString();
        if (displayName == null) {
            throw new SerializationException("Fish Rarity display name can not be null");
        }

        double lengthMin = node.node("length-min").getDouble();
        double lengthMax = node.node("length-max").getDouble();
        ConfigurationNode icon = node.node("icon");

        List<CatchHandler> catchHandlers = new ArrayList<>(fishRarity.getCatchHandlers());
        catchHandlers.add(node.node("commands").get(CatchCommandExecutor.class));
        PlayerAnnouncement catchAnnouncement = node.node("catch-announce").get(PlayerAnnouncement.class, fishRarity.getCatchAnnouncement());
        List<FishCondition> conditions = node.node("conditions").getList(FishCondition.class, new ArrayList<>());
        boolean hasNotFishItemFormat = node.node("skip-item-format").getBoolean(fishRarity.hasNotFishItemFormat());
        boolean noDisplay = node.node("no-display").getBoolean(fishRarity.getNoDisplay());
        boolean hasCatchFirework = fishRarity.hasCatchFirework();
        return new FishType(name, fishRarity, displayName, lengthMin, lengthMax, icon, catchHandlers, catchAnnouncement, conditions, hasNotFishItemFormat, noDisplay, hasCatchFirework);
    }

    private double clamp(double value, double min, double max) {
        double var7 = Math.min(value, max);
        return Math.max(var7, min);
    }

    private double floorToTwoDecimalPlaces(double value) {
        double var3 = value * (double) 10;
        return Math.floor(var3) / (double) 10;
    }

    @Nonnull
    public Fish generateFish() {
        if (lengthMin > lengthMax) {
            throw new IllegalStateException("Max-length must not be smaller than min-length");
        }

        double rawLength = lengthMin + new Random().nextDouble() * (lengthMax - lengthMin);
        double length = clamp(floorToTwoDecimalPlaces(rawLength), lengthMin, lengthMax);
        return new Fish(this, length);
    }

    @Nonnull
    public PlayerAnnouncement getCatchAnnouncement() {
        return this.catchAnnouncement;
    }

    @Nonnull
    public List<CatchHandler> getCatchHandlers() {
        return this.catchHandlers;
    }

    @Nonnull
    public List<FishCondition> getConditions() {
        return this.conditions;
    }

    @Nonnull
    public String getDisplayName() {
        return this.displayName;
    }

    @Nonnull
    public ConfigurationNode getIcon() {
        return this.icon;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public FishRarity getRarity() {
        return this.rarity;
    }
}
