package me.elsiff.morefish.fishing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.inventory.ItemStack;

public class FishBag {

    private final Map<Integer, List<ItemStack>> pages = new HashMap<>();
    @Nonnull
    private final UUID uuid;
    private int maxAllowedPages = 0;

    public FishBag(@Nonnull UUID uuid) {
        this.uuid = uuid;
    }

    public boolean addFish(int page, @Nonnull ItemStack fish) {
        List<ItemStack> fishes = pages.getOrDefault(page, new ArrayList<>());
        if (fishes.size() < 45) {
            fishes.add(fish);
            pages.put(page, fishes);
            return true;
        }

        return false;
    }

    @Nonnull
    public List<ItemStack> getFish(int page) {
        return pages.containsKey(page) ? pages.get(page) : new ArrayList<>();
    }

    public int getMaxAllowedPages() {
        return maxAllowedPages;
    }

    public void setMaxAllowedPages(int maxAllowedPages) {
        this.maxAllowedPages = maxAllowedPages;
    }

    @Nonnull
    public UUID getUUID() {
        return uuid;
    }

    public void updatePage(int page, @Nonnull List<ItemStack> fish) {
        pages.put(page, fish);
    }
}
