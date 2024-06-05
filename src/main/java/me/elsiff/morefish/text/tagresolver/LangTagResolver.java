package me.elsiff.morefish.text.tagresolver;

import me.elsiff.morefish.text.Lang;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LangTagResolver implements TagResolver {

    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        if (!has(name)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        while (arguments.hasNext()) {
            sb.append(Lang.raw(arguments.pop().value()));
        }

        return Tag.selfClosingInserting(ctx.deserialize(sb.toString()));
    }

    @Override
    public boolean has(@NotNull String name) {
        return name.equals("mf-lang");
    }
}
