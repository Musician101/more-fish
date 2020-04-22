package me.elsiff.morefish.configuration.format;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.elsiff.morefish.hooker.PlaceholderApiHooker;
import org.bukkit.entity.Player;

public interface Format<T, R> {

    R output(@Nullable Player player);

    default T replace(@Nonnull Map<String, Object> pairs) {
        return replace(pairs.entrySet());
    }

    T replace(@Nonnull Collection<Entry<String, Object>> pairs);

    final class Companion {

        static PlaceholderApiHooker placeholderApiHooker;

        private Companion() {

        }

        public static void init(@Nonnull PlaceholderApiHooker placeholderApiHooker) {
            Companion.placeholderApiHooker = placeholderApiHooker;
        }

        @Nonnull
        static String tryReplacing(@Nonnull String string, @Nullable Player player) {
            if (placeholderApiHooker != null && placeholderApiHooker.hasHooked()) {
                return placeholderApiHooker.tryReplacing(string, player);
            }

            return string;
        }
    }
}
