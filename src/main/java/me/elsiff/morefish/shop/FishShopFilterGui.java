package me.elsiff.morefish.shop;

import io.musician101.musicianlibrary.java.minecraft.spigot.gui.chest.SpigotIconBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.fishing.FishRarity;
import me.elsiff.morefish.fishing.FishTypeTable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class FishShopFilterGui extends AbstractFishShopGUI {

    public static final Map<UUID, List<FishRarity>> filters = new HashMap<>();

    @Nonnull
    private final List<FishRarity> selectedRarities;

    public FishShopFilterGui(int page, @Nonnull Player user) {
        super("Set Sale Filter(s)", user);
        this.selectedRarities = filters.getOrDefault(user.getUniqueId(), new ArrayList<>());
        List<FishRarity> fishRarities = new ArrayList<>();
        FishTypeTable fishTypeTable = MoreFish.instance().getFishTypeTable();
        fishTypeTable.getDefaultRarity().ifPresent(fishRarities::add);
        fishTypeTable.getRarities().stream().filter(rarity -> rarity.getAdditionalPrice() >= 0 && !rarity.isDefault()).sorted(Comparator.reverseOrder()).forEach(fishRarities::add);
        IntStream.range(0, 45).forEach(x -> {
            try {
                FishRarity fishRarity = fishRarities.get(x + (page - 1) * 45);
                updateIcon(x, fishRarity);
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        });

        IntStream.of(45, 46, 47, 48, 50, 51, 52, 53).forEach(this::glassPaneButton);
        setButton(49, SpigotIconBuilder.of(Material.BARRIER, "Back to Shop"), Map.of(ClickType.LEFT, p -> {
            filters.put(p.getUniqueId(), selectedRarities);
            new FishShopGui(p, 1);
        }));
    }

    private void updateIcon(int slot, FishRarity fishRarity) {
        ItemStack itemStack = SpigotIconBuilder.builder(Material.COD).name(fishRarity.getColor() + fishRarity.getDisplayName()).description(List.of(selectedRarities.contains(fishRarity) ? ChatColor.GREEN + "Selected." : ChatColor.RED + "Not selected.")).build();
        setButton(slot, itemStack, Map.of(ClickType.LEFT, p -> {
            if (selectedRarities.contains(fishRarity)) {
                selectedRarities.remove(fishRarity);
            }
            else {
                selectedRarities.add(fishRarity);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), () -> updateIcon(slot, fishRarity), 2);
        }));
    }
}
