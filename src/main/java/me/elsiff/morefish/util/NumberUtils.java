package me.elsiff.morefish.util;

import javax.annotation.Nonnull;

public final class NumberUtils {

    private NumberUtils() {
    }

    @Nonnull
    public static String ordinalOf(int number) {
        String suffix = "th";
        switch (number % 100) {
            case 11:
            case 12:
            case 13:
                suffix =  "th";
        }

        switch (number % 10) {
            case 1:
                suffix = "st";
                break;
            case 2:
                suffix =  "nd";
                break;
            case 3:
                suffix = "rd";
        }

        return number + suffix;
    }
}
