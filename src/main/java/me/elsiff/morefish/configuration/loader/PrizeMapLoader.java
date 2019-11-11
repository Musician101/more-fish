package me.elsiff.morefish.configuration.loader;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.Prize;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.configuration.ConfigurationSection;

public final class PrizeMapLoader implements CustomLoader<Map<IntRange, Prize>> {

    private final IntRange intRangeFrom(String string) {
        String[] tokens = string.split("~");
        int start = Integer.parseInt(tokens[0]);
        int end = Integer.MAX_VALUE;
        if (tokens.length > 2) {
            if (!tokens[1].isEmpty()) {
                end = Integer.parseInt(tokens[1]);
            }
        }

        return new IntRange(start, end);
    }

    @Nonnull
    public Map<IntRange, Prize> loadFrom(@Nonnull ConfigurationSection section, @Nonnull String path) {
        ConfigurationSection cs = section.getConfigurationSection(path);
        return cs.getKeys(false).stream().map(key -> {
            IntRange range = intRangeFrom(key);
            List<String> commands = cs.getStringList(key);
            return new SimpleEntry<>(range, new Prize(commands));
        }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}
