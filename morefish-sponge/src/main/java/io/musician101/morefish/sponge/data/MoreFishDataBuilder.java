package io.musician101.morefish.sponge.data;

import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchHandler;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColor;

public class MoreFishDataBuilder implements DataManipulatorBuilder<MoreFishData, ImmutableMoreFishData> {

    private FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fishType;
    private double length;

    @Nonnull
    @Override
    public Optional<MoreFishData> build(@Nonnull DataView container) throws InvalidDataException {
        return container.getObject(MoreFishData.FISH_TYPE.getQuery(), FishType.class).flatMap(fishType -> container.getDouble(MoreFishData.LENGTH.getQuery()).map(length -> new MoreFishData(fishType, length)));
    }

    @Nonnull
    @Override
    public MoreFishData create() {
        return new MoreFishData(fishType, length);
    }

    @Nonnull
    @Override
    public Optional<MoreFishData> createFrom(@Nonnull DataHolder dataHolder) {
        return dataHolder.get(MoreFishData.FISH_TYPE).flatMap(fishType -> dataHolder.get(MoreFishData.LENGTH).map(length -> new MoreFishData(fishType, length)));
    }

    @Nonnull
    @Override
    public MoreFishDataBuilder from(@Nonnull MoreFishData value) {
        fishType = value.getFishType().get();
        length = value.getLength().get();
        return this;
    }

    @Nonnull
    public MoreFishDataBuilder length(double length) {
        this.length = length;
        return this;
    }

    @Nonnull
    public MoreFishDataBuilder length(@Nonnull FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fishType) {
        this.fishType = fishType;
        return this;
    }

    @Nonnull
    @Override
    public MoreFishDataBuilder reset() {
        fishType = null;
        length = 0;
        return this;
    }
}
