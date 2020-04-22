package me.elsiff.morefish.configuration.format;

import java.util.Collection;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.elsiff.morefish.configuration.ColorCodeTranslatingExtension;
import org.bukkit.entity.Player;

public final class TextFormat implements Format<TextFormat, String> {

    private String string;

    public TextFormat(@Nonnull String string) {
        this.string = string;
    }

    @Nonnull
    public String output() {
        return output(null);
    }

    @Nonnull
    public String output(@Nullable Player player) {
        return Format.Companion.tryReplacing(ColorCodeTranslatingExtension.translated(this.string), player);
    }

    @Nonnull
    public TextFormat replace(@Nonnull Collection<Entry<String, Object>> pairs) {
        pairs.forEach(pair -> string = string.replace(pair.getKey(), pair.getValue().toString()));
        return this;
    }
}
