package me.elsiff.morefish.util;

import javax.annotation.Nonnull;

public final class NumberUtils {

    private NumberUtils() {
    }

    @Nonnull
    public static String ordinalOf(int number) {
        return number + ordinalSuffixOf(number);
    }

    @Nonnull
    public static String ordinalSuffixOf(int number) {
        switch (number % 100) {
            case 11:
            case 12:
            case 13:
                return "th";
        }

        switch (number % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

}
