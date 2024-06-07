package me.elsiff.morefish.bags;

import me.elsiff.morefish.item.FishItemStackConverter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FishBag {

    private final Map<Integer, List<ItemStack>> pages = new HashMap<>();
    @NotNull
    private final UUID uuid;
    private int maxAllowedPages = 0;

    public FishBag(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public boolean addFish(int page, @NotNull ItemStack fish) {
        List<ItemStack> fishes = pages.getOrDefault(page, new ArrayList<>());
        if (fishes.size() < 45) {
            fishes.add(fish);
            pages.put(page, fishes);
            return true;
        }

        return false;
    }

    public void clearContraband() {
        pages.forEach((k, v) -> v.removeIf(i -> !FishItemStackConverter.isFish(i)));
    }

    @NotNull
    public List<ItemStack> getContraband() {
        return pages.values().stream().flatMap(List::stream).filter(i -> !FishItemStackConverter.isFish(i)).collect(Collectors.toList());
    }

    @NotNull
    public List<ItemStack> getFish(int page) {
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

    public void updatePage(int page, @NotNull List<ItemStack> fish) {
        pages.put(page, fish);
    }
}
