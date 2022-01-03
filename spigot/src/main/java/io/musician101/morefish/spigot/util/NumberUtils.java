package io.musician101.morefish.spigot.util;

import javax.annotation.Nonnull;

public final class NumberUtils {

    private NumberUtils() {
    }

    @Nonnull
    public static String ordinalOf(int number) {
        String suffix = "th";
        switch (number % 100) {
            case 11, 12, 13 -> suffix = "th";
        }

        suffix = switch (number % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> suffix;
        };

        return number + suffix;
    }
}
