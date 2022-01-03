package io.musician101.morefish.forge.config.format;

import io.musician101.morefish.common.config.format.TextListFormat;
import io.musician101.morefish.forge.util.TextParser;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;

public final class ForgeTextListFormat extends TextListFormat<ServerPlayerEntity, ForgeTextListFormat, ITextComponent> implements ForgeFormat<ForgeTextListFormat, List<ITextComponent>> {

    public ForgeTextListFormat(@Nonnull List<Object> strings) {
        super(strings);
    }

    @Nonnull
    @Override
    public List<ITextComponent> output(@Nullable UUID player) {
        return translated(strings).stream().map(TextParser::new).map(TextParser::getOutput).collect(Collectors.toList());
    }
}
