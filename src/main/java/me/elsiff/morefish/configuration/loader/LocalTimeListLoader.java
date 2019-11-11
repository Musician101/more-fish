package me.elsiff.morefish.configuration.loader;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.bukkit.configuration.ConfigurationSection;

public final class LocalTimeListLoader implements CustomLoader<List<LocalTime>> {

    private final DateTimeFormatter formatter;

    public LocalTimeListLoader() {
        this.formatter = DateTimeFormatter.ofPattern("HH:mm");
    }

    @Nonnull
    public List<LocalTime> loadFrom(@Nonnull ConfigurationSection section, @Nonnull String path) {
        return section.getStringList(path).stream().map(string -> LocalTime.parse(string, formatter)).collect(Collectors.toList());
    }
}
