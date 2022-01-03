package io.musician101.morefish.forge.util;

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
                suffix = "th";
        }

        switch (number % 10) {
            case 1:
                suffix = "st";
                break;
            case 2:
                suffix = "nd";
                break;
            case 3:
                suffix = "rd";
        }

        return number + suffix;
    }

    public static class DoubleRange {

        private final double max;
        private final double min;

        public DoubleRange(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public boolean containsDouble(double value) {
            return value >= min && value <= max;
        }
    }

    public static class IntRange {

        private final int max;
        private final int min;

        public IntRange(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public boolean containsInteger(int value) {
            return value >= min && value <= max;
        }
    }
}
