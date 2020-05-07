package io.musician101.morefish.sponge.data;

import com.google.common.reflect.TypeToken;
import io.musician101.morefish.common.Reference;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchHandler;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.util.TypeTokens;

@SuppressWarnings("UnstableApiUsage")
public class MoreFishData extends AbstractData<MoreFishData, ImmutableMoreFishData> {

    public static final Key<Value<FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>>> FISH_TYPE = Key.builder().type(new TypeToken<Value<FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>>>() {

        @Override
        public boolean equals(@Nullable Object o) {
            return super.equals(o);
        }
    }).query(DataQuery.of("FishType")).name("FishType").id(Reference.ID + ":fish_type").build();
    public static final Key<Value<Double>> LENGTH = Key.builder().type(TypeTokens.DOUBLE_VALUE_TOKEN).query(DataQuery.of("Length")).name("Length").id(Reference.ID + ":length").build();
    @Nonnull
    private FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fishType;
    private double length;

    public MoreFishData(@Nonnull FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fishType, double length) {
        this.fishType = fishType;
        this.length = length;
        registerGettersAndSetters();
    }

    @Nonnull
    @Override
    public ImmutableMoreFishData asImmutable() {
        return new ImmutableMoreFishData(fishType, length);
    }

    @Nonnull
    @Override
    public MoreFishData copy() {
        return new MoreFishData(fishType, length);
    }

    @Nonnull
    @Override
    public Optional<MoreFishData> fill(@Nonnull DataHolder dataHolder, @Nonnull MergeFunction overlap) {
        return Optional.of(overlap.merge(this, dataHolder.get(MoreFishData.class).orElse(null)));
    }

    @Nonnull
    @Override
    public Optional<MoreFishData> from(@Nonnull DataContainer container) {
        return container.getObject(FISH_TYPE.getQuery(), fishType.getClass()).filter(fishType -> this.fishType.getClass().isInstance(fishType)).flatMap(fishType -> container.getDouble(LENGTH.getQuery()).map(length -> new MoreFishData(fishType, length)));
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Nonnull
    public Value<FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>> getFishType() {
        return Sponge.getRegistry().getValueFactory().createValue(FISH_TYPE, fishType);
    }

    @Nonnull
    public Value<Double> getLength() {
        return Sponge.getRegistry().getValueFactory().createValue(LENGTH, length);
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(FISH_TYPE, () -> this.fishType);
        registerFieldSetter(FISH_TYPE, fishType -> this.fishType = fishType);
        registerKeyValue(FISH_TYPE, this::getFishType);

        registerFieldGetter(LENGTH, () -> this.length);
        registerFieldSetter(LENGTH, length -> this.length = length);
        registerKeyValue(LENGTH, this::getLength);

    }
}
