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
        int var2 = number % 100;
        if (11 <= var2) {
            if (13 >= var2) {
                return "th";
            }
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
