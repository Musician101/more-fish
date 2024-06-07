package me.elsiff.morefish.text.tagresolver;

import me.elsiff.morefish.records.FishRecord;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FishTagResolver extends FisRecordTagResolver {

    public FishTagResolver(@Nullable UUID uuid) {
        super(uuid);
    }

    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        if (!has(name)) {
            return null;
        }

        return recordOf().map(FishRecord::getFishName).map(ctx::deserialize).map(Tag::selfClosingInserting).orElse(null);
    }

    @Override
    public boolean has(@NotNull String name) {
        return name.equals("fish");
    }
}
