package me.elsiff.morefish.fish.registry;

import me.elsiff.morefish.fish.FishIcon;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.serialize.fish.FishIconSerializer;
import me.elsiff.morefish.serialize.fish.FishTypeSerializer;
import me.elsiff.morefish.serialize.fish.ItemStackSerializer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.serialize.TypeSerializerCollection.Builder;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public final class FishTypes extends FishAbstracts<FishType> {

    FishTypes() {
        super("Type", "Types", FishType.class);
    }

    @Override
    protected Path dir() {
        return getPlugin().getDataPath().resolve("fish/type");
    }

    @Override
    protected void serializers(Builder builder) {
        builder.register(FishType.class, new FishTypeSerializer())
                .register(FishIcon.class, new FishIconSerializer())
                .register(ItemStack.class, new ItemStackSerializer());
    }

    public List<FishType> get(FishRarity rarity) {
        return values.stream().filter(type -> rarity.equals(type.rarity())).toList();
    }

    public FishType pickRandomType(Item caught, Player fisher, int luckOfTheSeaLevel, Random random) {
        FishRarity rarity = getPlugin().getFishTypeTable().rarities().pickRandomRarity(luckOfTheSeaLevel, random);
        List<FishType> types = values.stream().filter(type -> rarity.equals(type.rarity()) && checkConditions(type, caught, fisher)).toList();
        return types.get(random.nextInt(types.size()));
    }

    private boolean checkConditions(FishType fishType, Item item, Player player) {
        return fishType.conditions().check(item, player) && fishType.rarity().conditions().check(item, player);
    }
}
