package me.elsiff.morefish.fish.condition;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.VirtualComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class FishCondition<V> implements ComponentLike, TagResolver {

    protected final V value;

    protected FishCondition(V value) {
        this.value = value;
    }

    public V value() {
        return value;
    }

    @NullMarked
    public abstract boolean check(Item caught, Player fisher);

    @Override
    public Component asComponent() {
        return (VirtualComponent) Argument.tagResolver(this);
    }
}
