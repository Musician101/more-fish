package me.elsiff.morefish.common.fishing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FishBags<F extends FishBag<I>, I> {

    @NotNull protected final List<F> bags = new ArrayList<>();

    public boolean addFish(@NotNull UUID player, @NotNull I itemStack) {
        F fishBag = getFishBag(player);
        for (int i = 1; i < getMaxAllowedPages(player) + 1; i++) {
            if (fishBag.addFish(i, itemStack)) {
                return true;
            }
        }

        return false;
    }

    @NotNull
    public List<I> getFish(@NotNull UUID player, int page) {
        return getFishBag(player).getFish(page);
    }

    @NotNull
    public F getFishBag(@NotNull UUID player) {
        return bags.stream().filter(fb -> fb.getUUID().equals(player)).findFirst().orElseGet(() -> {
            F fb = newFishBag(player);
            bags.add(fb);
            return fb;
        });
    }

    public int getMaxAllowedPages(@NotNull UUID player) {
        return getFishBag(player).getMaxAllowedPages();
    }

    public abstract void load();

    protected abstract F newFishBag(@NotNull UUID player);

    public abstract void save();

    public void setMaxAllowedPages(@NotNull UUID player, int maxAllowedPages) {
        F fishBag = getFishBag(player);
        if (!bags.contains(fishBag)) {
            bags.add(fishBag);
        }

        fishBag.setMaxAllowedPages(maxAllowedPages);
    }

    public void update(@NotNull UUID player, @Nullable I[] contents, int page) {
        F fishBag = getFishBag(player);
        if (!bags.contains(fishBag)) {
            bags.add(fishBag);
        }

        fishBag.updatePage(page, List.of(contents));
    }
}
