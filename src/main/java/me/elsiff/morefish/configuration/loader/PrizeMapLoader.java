package me.elsiff.morefish.configuration.loader;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import me.elsiff.morefish.fishing.competition.Prize;
import org.bukkit.configuration.ConfigurationSection;

public final class PrizeMapLoader implements CustomLoader<Map<Integer, Prize>> {

    @Nonnull
    public Map<Integer, Prize> loadFrom(@Nonnull ConfigurationSection section, @Nonnull String path) {
        ConfigurationSection cs = section.getConfigurationSection(path);
        return cs.getKeys(false).stream().map(key -> new SimpleEntry<>(Integer.parseInt(key) - 1, new Prize(cs.getStringList(key)))).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}
