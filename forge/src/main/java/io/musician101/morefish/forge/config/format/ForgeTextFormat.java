package io.musician101.morefish.forge.config.format;

import io.musician101.morefish.common.config.format.TextFormat;
import io.musician101.morefish.forge.util.TextParser;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;

public final class ForgeTextFormat extends TextFormat<ForgeTextFormat, ITextComponent> implements ForgeFormat<ForgeTextFormat, ITextComponent> {

    public ForgeTextFormat(@Nonnull String string) {
        super(string);
    }

    @Nonnull
    @Override
    public ITextComponent output(@Nullable UUID player) {
        return new TextParser(string).getOutput();
    }
}
