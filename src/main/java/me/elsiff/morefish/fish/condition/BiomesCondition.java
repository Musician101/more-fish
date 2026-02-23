package me.elsiff.morefish.fish.condition;

import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class BiomesCondition extends FishCondition<List<Biome>> {

    public BiomesCondition(List<Biome> value) {
        super(value);
    }

    public boolean check(Item caught, Player fisher) {
        Location location = caught.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return value.isEmpty() || value.contains(caught.getWorld().getBiome(x, y, z));
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            return TagResolverUtil.fromList(value, arguments, ctx, b -> Tag.selfClosingInserting(Component.translatable(b.translationKey())));
        }

        return null;
    }

    @Override
    public boolean has(String name) {
        return name.equals("biome");
    }
}
