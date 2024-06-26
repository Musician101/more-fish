package me.elsiff.morefish.text.tagresolver;

import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.elsiff.morefish.text.Lang.raw;
import static me.elsiff.morefish.text.Lang.replace;

public class LangTagResolver implements TagResolver {

    private final Player player;
    private final TagResolver tagResolver;

    public LangTagResolver(@NotNull TagResolver tagResolver, @Nullable Player player) {
        this.tagResolver = tagResolver;
        this.player = player;
    }

    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        if (!has(name)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        while (arguments.hasNext()) {
            sb.append(raw(arguments.pop().value()));
        }

        return Tag.selfClosingInserting(replace(sb.toString(), tagResolver, player));
    }

    @Override
    public boolean has(@NotNull String name) {
        return name.equals("mf-lang");
    }
}
