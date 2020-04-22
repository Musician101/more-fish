package me.elsiff.morefish.configuration.format;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.elsiff.morefish.configuration.ColorCodeTranslatingExtension;
import org.bukkit.entity.Player;

public final class TextListFormat implements Format<TextListFormat, List<String>> {

    private List<String> strings;

    public TextListFormat(@Nonnull List<String> strings) {
        super();
        this.strings = strings;
    }

    @Nonnull
    public List<String> output(@Nullable Player player) {
        return ColorCodeTranslatingExtension.translated(strings.stream().map(string -> Format.Companion.tryReplacing(string, player)).collect(Collectors.toList()));
    }

    @Override
    public TextListFormat replace(@Nonnull Collection<Entry<String, Object>> pairs) {
        pairs.forEach(pair -> strings = strings.stream().map(string -> string.replace(pair.getKey(), pair.getValue().toString())).collect(Collectors.toList()));
        return this;
    }
}
