package me.elsiff.morefish.text.tagresolver;

import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TopFishTagResolver extends TopTagResolver {

    @Nullable
    @Override
    public Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        return resolve(name, arguments, ctx, FishRecord::getFishName);
    }

    @Override
    public boolean has(@NotNull String name) {
        return name.equals("top-fish");
    }
}
