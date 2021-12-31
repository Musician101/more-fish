package me.elsiff.morefish.util;

import javax.annotation.Nonnull;

public interface NumberUtils {

    @Nonnull
    static String ordinalOf(int number) {
        String suffix;
        switch (number % 10) {
            case 1 -> suffix = "st";
            case 2 -> suffix = "nd";
            case 3 -> suffix = "rd";
            default -> suffix = "th";
        }

        switch (number % 100) {
            case 11, 12, 13 -> suffix = "th";
        }

        return number + suffix;
    }
}
