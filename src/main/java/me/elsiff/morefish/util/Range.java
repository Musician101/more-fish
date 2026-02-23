package me.elsiff.morefish.util;

import org.jspecify.annotations.NullMarked;

@NullMarked
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

    public Range<N> withMin(N min) {
        return new Range<>(min, max);
    }

    public Range<N> withMax(N max) {
        return new Range<>(min, max);
    }

    @Override
    public String toString() {
        return min + "-" + max;
    }
}
