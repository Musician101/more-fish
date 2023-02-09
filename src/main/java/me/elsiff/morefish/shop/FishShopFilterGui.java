package me.elsiff.morefish.shop;

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

import static io.musician101.musigui.spigot.chest.SpigotIconUtil.customName;
import static io.musician101.musigui.spigot.chest.SpigotIconUtil.setLore;

public class FishShopFilterGui extends AbstractFishShopGUI {

    public static final Map<UUID, List<FishRarity>> FILTERS = new HashMap<>();

    @Nonnull
    private final List<FishRarity> selectedRarities;

    public FishShopFilterGui(int page, @Nonnull Player user) {
        super("Set Sale Filter(s)", user);
        this.selectedRarities = FILTERS.getOrDefault(user.getUniqueId(), new ArrayList<>());
        updateButtons(page);
        IntStream.of(45, 46, 47, 48, 50, 51, 52, 53).forEach(this::glassPaneButton);
    }

    private void updateButtons(int page) {
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

        setButton(49, customName(new ItemStack(Material.BARRIER), "Back to Shop"), ClickType.LEFT, p -> {
            FILTERS.put(p.getUniqueId(), selectedRarities);
            new FishShopGui(p, 1);
        });
    }

    private void updateIcon(int slot, FishRarity fishRarity) {
        String name = fishRarity.getColor() + fishRarity.getDisplayName();
        String lore = selectedRarities.contains(fishRarity) ? ChatColor.GREEN + "Selected." : ChatColor.RED + "Not selected.";
        ItemStack itemStack = setLore(customName(new ItemStack(Material.COD), name), lore);
        setButton(slot, itemStack, ClickType.LEFT, p -> {
            if (selectedRarities.contains(fishRarity)) {
                selectedRarities.remove(fishRarity);
            }
            else {
                selectedRarities.add(fishRarity);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), () -> updateIcon(slot, fishRarity), 2);
        });
    }
}
