package me.elsiff.morefish.util;

import javax.annotation.Nonnull;

public class NumberUtils {

    private NumberUtils() {

    }

    @Nonnull
    public static String ordinalOf(int number) {
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

    public record Range<N extends Number>(N min, N max) {

        public boolean containsDouble(double value) {
            return value >= min.doubleValue() && value <= max.doubleValue();
        }

        public boolean containsInteger(int value) {
            return value >= min.intValue() && value <= max.intValue();
        }

        public boolean containsLong(long value) {
            return value >= min.longValue() && value <= max.longValue();
        }
    }
}
