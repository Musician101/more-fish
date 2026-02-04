package me.elsiff.morefish.fish.registry;

import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.LuckOfTheSeaModifier;
import me.elsiff.morefish.serialize.fish.FishRaritySerializer;
import me.elsiff.morefish.serialize.fish.LuckOfTheSeaModifierSerializer;
import me.elsiff.morefish.serialize.fish.LuckOfTheSeaModifierSerializer.ModifierTypeSerializer;
import me.elsiff.morefish.serialize.fish.TextColorSerializer;
import net.kyori.adventure.text.format.TextColor;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.TypeSerializerCollection.Builder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public final class FishRarities extends FishAbstracts<FishRarity> {

    public FishRarities() {
        super("Rarity", "Rarities", FishRarity.class);
    }

    @Override
    protected Path dir() {
        return getPlugin().getDataPath().resolve("fish/rarity");
    }

    @Override
    protected void serializers(Builder builder) {
        builder.register(FishRarity.class, new FishRaritySerializer())
                .register(TextColor.class, new TextColorSerializer())
                .register(LuckOfTheSeaModifier.class, new LuckOfTheSeaModifierSerializer())
                .register(LuckOfTheSeaModifier.Type.class, new ModifierTypeSerializer());
    }

    @Override
    public void delete(FishRarity value) throws IOException {
        if (getPlugin().types().get(value).isEmpty()) {
            super.delete(value);
        }

        throw new IOException(value.getKey() + " could not be deleted as it still has types associated with it.");
    }

    public FishRarity pickRandomRarity(int luckOfTheSeasLevel, Random random) {
        int weightSum = values.stream().mapToInt(r -> r.modifiedWeight(luckOfTheSeasLevel)).sum();
        values.sort(FishRarity::compareTo);
        int randomVal = random.nextInt(weightSum);
        for (FishRarity rarity : values) {
            randomVal -= rarity.modifiedWeight(luckOfTheSeasLevel);
            if (randomVal < 0) {
                return rarity;
            }
        }

        throw new IllegalStateException("This is bad. We somehow generated a number greater than the sum of all weights.");
    }
}
