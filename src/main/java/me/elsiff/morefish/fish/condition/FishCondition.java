package me.elsiff.morefish.fish.condition;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class FishCondition<V> implements TagResolver {

    protected final V value;

    protected FishCondition(V value) {
        this.value = value;
    }

    public V value() {
        return value;
    }

    @NullMarked
public abstract boolean check(Item caught, Player fisher);
}
