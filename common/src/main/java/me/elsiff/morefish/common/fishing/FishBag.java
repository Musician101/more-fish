package me.elsiff.morefish.common.fishing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public abstract class FishBag<I> {

    private final Map<Integer, List<I>> pages = new HashMap<>();
    @NotNull private final UUID uuid;
    private int maxAllowedPages = 0;

    public FishBag(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public boolean addFish(int page, @NotNull I fish) {
        List<I> fishes = pages.getOrDefault(page, new ArrayList<>());
        if (fishes.size() < 45) {
            fishes.add(fish);
            pages.put(page, fishes);
            return true;
        }

        return false;
    }

    public void clearContraband() {
        pages.forEach((k, v) -> v.removeIf(this::isContraband));
    }

    @NotNull
    public List<I> getContraband() {
        return pages.values().stream().flatMap(List::stream).filter(this::isContraband).collect(Collectors.toList());
    }

    @NotNull
    public List<I> getFish(int page) {
        return pages.containsKey(page) ? pages.get(page) : new ArrayList<>();
    }

    public int getMaxAllowedPages() {
        return maxAllowedPages;
    }

    public void setMaxAllowedPages(int maxAllowedPages) {
        this.maxAllowedPages = maxAllowedPages;
    }

    @NotNull
    public UUID getUUID() {
        return uuid;
    }

    protected abstract boolean isContraband(@NotNull I itemStack);

    public void updatePage(int page, @NotNull List<I> fish) {
        pages.put(page, fish);
    }
}
