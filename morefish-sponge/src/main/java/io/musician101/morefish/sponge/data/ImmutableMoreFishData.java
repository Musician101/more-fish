package io.musician101.morefish.sponge.data;

import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchHandler;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColor;

public class ImmutableMoreFishData extends AbstractImmutableData<ImmutableMoreFishData, MoreFishData> {

    @Nonnull
    private final FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fishType;
    private final double length;

    public ImmutableMoreFishData(@Nonnull FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fishType, double length) {
        this.fishType = fishType;
        this.length = length;
    }

    @Nonnull
    @Override
    public MoreFishData asMutable() {
        return new MoreFishData(fishType, length);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Nonnull
    public ImmutableValue<FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>> getFishType() {
        return Sponge.getRegistry().getValueFactory().createValue(MoreFishData.FISH_TYPE, fishType).asImmutable();
    }

    @Nonnull
    public ImmutableValue<Double> getLength() {
        return Sponge.getRegistry().getValueFactory().createValue(MoreFishData.LENGTH, length).asImmutable();
    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(MoreFishData.FISH_TYPE, () -> this.fishType);
        registerKeyValue(MoreFishData.FISH_TYPE, this::getFishType);

        registerFieldGetter(MoreFishData.LENGTH, () -> this.length);
        registerKeyValue(MoreFishData.LENGTH, this::getLength);
    }
}
