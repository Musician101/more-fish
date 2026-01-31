package me.elsiff.morefish.bags;

import me.elsiff.morefish.item.FishItemStackUtil;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@NullMarked
public class FishBag {

    private final Map<Integer, List<ItemStack>> pages = new HashMap<>();
    private final UUID uuid;
    private int maxAllowedPages = 0;

    public FishBag(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean addFish(int page, ItemStack fish) {
        List<ItemStack> fishes = pages.getOrDefault(page, new ArrayList<>());
        if (fishes.size() < 45) {
            fishes.add(fish);
            pages.put(page, fishes);
            return true;
        }

        return false;
    }

    public void clearContraband() {
        pages.forEach((k, v) -> v.removeIf(i -> !FishItemStackUtil.isFish(i)));
    }

    public List<ItemStack> getContraband() {
        return pages.values().stream().flatMap(List::stream).filter(i -> !FishItemStackUtil.isFish(i)).collect(Collectors.toList());
    }

    public List<ItemStack> getFish(int page) {
        return pages.containsKey(page) ? pages.get(page) : new ArrayList<>();
    }

    public int getMaxAllowedPages() {
        return maxAllowedPages;
    }

    public void setMaxAllowedPages(int maxAllowedPages) {
        this.maxAllowedPages = maxAllowedPages;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void updatePage(int page, List<ItemStack> fish) {
        pages.put(page, fish);
    }
}
