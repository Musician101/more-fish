package me.elsiff.morefish.fish.condition;

import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class RainingCondition extends FishCondition<Boolean> {

    public RainingCondition(boolean value) {
        super(value);
    }

    public boolean check(Item caught, Player fisher) {
        return caught.getWorld().hasStorm() == this.value;
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            return TagResolverUtil.booleanTag(name, value, arguments, ctx);
        }

        return null;
    }

    @Override
    public boolean has(String name) {
        return name.equals("raining");
    }
}
