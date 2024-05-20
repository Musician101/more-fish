package me.elsiff.morefish.shop;

import me.elsiff.morefish.fishing.FishRarity;
import me.elsiff.morefish.fishing.FishTypeTable;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static io.musician101.musigui.paper.chest.PaperIconUtil.customName;
import static io.musician101.musigui.paper.chest.PaperIconUtil.setLore;
import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.replace;

public class FishShopFilterGui extends AbstractFishShopGUI {

    public static final Map<UUID, List<FishRarity>> FILTERS = new HashMap<>();

    @NotNull
    private final List<FishRarity> selectedRarities;

    public FishShopFilterGui(int page, @NotNull Player user) {
        super(replace("<mf-lang:sales-filter-title>"), user);
        this.selectedRarities = FILTERS.getOrDefault(user.getUniqueId(), new ArrayList<>());
        updateButtons(page);
        IntStream.of(45, 46, 47, 48, 50, 51, 52, 53).forEach(this::glassPaneButton);
    }

    private void updateButtons(int page) {
        List<FishRarity> fishRarities = new ArrayList<>();
        FishTypeTable fishTypeTable = getPlugin().getFishTypeTable();
        fishTypeTable.getDefaultRarity().ifPresent(fishRarities::add);
        fishTypeTable.getRarities().stream().filter(rarity -> rarity.additionalPrice() >= 0 && !rarity.isDefault()).sorted(Comparator.reverseOrder()).forEach(fishRarities::add);
        IntStream.range(0, 45).forEach(x -> {
            try {
                FishRarity fishRarity = fishRarities.get(x + (page - 1) * 45);
                updateIcon(x, fishRarity);
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        });

        setButton(49, customName(new ItemStack(Material.BARRIER), replace("<mf-lang:sales-filter-back-button>")), ClickType.LEFT, p -> {
            FILTERS.put(p.getUniqueId(), selectedRarities);
            new FishShopGui(p, 1);
        });
    }

    //TODO remove underscores from all tag resolvers
    private void updateIcon(int slot, FishRarity fishRarity) {
        Component name = replace("<mf-lang:sales-filter-name>");
        Component lore = replace("<mf-lang:sales-filter-" + (selectedRarities.contains(fishRarity) ? "" : "not-") + "selected");
        ItemStack itemStack = setLore(customName(new ItemStack(Material.COD), name), lore);
        setButton(slot, itemStack, ClickType.LEFT, p -> {
            if (selectedRarities.contains(fishRarity)) {
                selectedRarities.remove(fishRarity);
            }
            else {
                selectedRarities.add(fishRarity);
            }

            player.getScheduler().runDelayed(getPlugin(), task -> updateIcon(slot, fishRarity), null, 2);
        });
    }
}
