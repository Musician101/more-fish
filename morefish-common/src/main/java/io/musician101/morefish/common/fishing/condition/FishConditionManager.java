package io.musician101.morefish.common.fishing.condition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;

public class FishConditionManager<C extends FishCondition<?, ?>> {

    private final Map<String, Function<String[], C>> hookers = new HashMap<>();

    public void registerFishCondition(String id, Function<String[], C> hooker) {
        if (hookers.containsKey(id)) {
            throw new IllegalArgumentException(id + " is already registered.");
        }

        hookers.put(id, hooker);
    }

    @Nonnull
    public Optional<C> getFishCondition(@Nonnull String id, @Nonnull String... args) {
        return Optional.ofNullable(hookers.get(id)).map(f -> f.apply(args));
    }
}
