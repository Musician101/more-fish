package io.musician101.morefish.spigot.shop;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchHandler;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import io.musician101.morefish.spigot.gui.MoreFishGUI;
import io.musician101.musicianlibrary.java.minecraft.spigot.gui.SpigotIconBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class FishShopFilterGui extends MoreFishGUI {

    public static final Map<UUID, List<FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>>> filters = new HashMap<>();

    @Nonnull
    private final List<FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>> selectedRarities;
    private int page = 1;

    public FishShopFilterGui(@Nonnull Player user) {
        super("Set Sale Filter(s)", user);
        this.selectedRarities = filters.getOrDefault(user.getUniqueId(), new ArrayList<>());
        updateIcons();
        IntStream.of(46, 47, 48, 50, 51, 52).forEach(this::glassPaneButton);
        setButton(49, BACK, ImmutableMap.of(ClickType.LEFT, p -> {
            filters.put(p.getUniqueId(), selectedRarities);
            new FishShopGui(p);
        }));
    }

    private void updateIcon(int slot, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler> fishRarity) {
        ItemStack itemStack = SpigotIconBuilder.builder(Material.COD).name(fishRarity.getColor() + fishRarity.getDisplayName()).description((selectedRarities.contains(fishRarity) ? ChatColor.GREEN + "S" : ChatColor.RED + "Not s") + "elected").build();
        setButton(slot, itemStack, ImmutableMap.of(ClickType.LEFT, p -> {
            if (selectedRarities.contains(fishRarity)) {
                selectedRarities.remove(fishRarity);
            }
            else {
                selectedRarities.add(fishRarity);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotMoreFish.getInstance(), () -> updateIcon(slot, fishRarity), 2);
        }));
    }

    private void updateIcons() {
        List<FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>> fishRarities = new ArrayList<>();
        FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>> fishConfig = SpigotMoreFish.getInstance().getPluginConfig().getFishConfig();
        fishConfig.getDefaultRarity().ifPresent(fishRarities::add);
        fishConfig.getRarities().stream().filter(rarity -> rarity.getAdditionalPrice() >= 0 && !rarity.isDefault()).sorted(Comparator.reverseOrder()).forEach(fishRarities::add);
        IntStream.range(0, 45).forEach(x -> {
            try {
                FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler> fishRarity = fishRarities.get(x + (page - 1) * 45);
                updateIcon(x, fishRarity);
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        });

        int maxPages = (int) Math.floor(fishRarities.size() / 45D);
        if (page == 1) {
            glassPaneButton(45);
        }
        else {
            setButton(45, PREVIOUS, ImmutableMap.of(ClickType.LEFT, p -> {
                page--;
                updateIcons();
            }));
        }

        if (page < maxPages) {
            setButton(53, NEXT, ImmutableMap.of(ClickType.LEFT, p -> {
                page++;
                updateIcons();
            }));
        }
        else {
            glassPaneButton(53);
        }
    }
}
