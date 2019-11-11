package me.elsiff.morefish.configuration;

import java.time.Duration;
import java.util.List;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.format.TextFormat;
import me.elsiff.morefish.configuration.format.TextListFormat;
import org.bukkit.configuration.file.YamlConfiguration;

public final class Lang {

    public static final char ALTERNATE_COLOR_CODE = '&';
    public static final Lang INSTANCE = new Lang();
    private static final YamlConfiguration langConfig = Config.INSTANCE.getLang();

    private Lang() {

    }

    @Nonnull
    public final TextFormat format(@Nonnull String id) {
        return new TextFormat(langConfig.getString(id));
    }

    @Nonnull
    public final TextListFormat formats(@Nonnull String id) {
        return new TextListFormat(langConfig.getStringList(id));
    }

    @Nonnull
    public final String text(@Nonnull String id) {
        return ColorCodeTranslatingExtension.translated(langConfig.getString(id));
    }

    @Nonnull
    public final List<String> texts(@Nonnull String id) {
        return ColorCodeTranslatingExtension.translated(langConfig.getStringList(id));
    }

    @Nonnull
    public final String time(long second) {
        StringBuilder builder = new StringBuilder();
        Duration duration = Duration.ofSeconds(second);
        if (duration.toMinutes() > 0L) {
            builder.append(duration.toMinutes()).append(this.text("time-format-minutes")).append(" ");
        }

        builder.append(duration.getSeconds() % (long) 60).append(this.text("time-format-seconds"));
        return builder.toString();
    }
}
