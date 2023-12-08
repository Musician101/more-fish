package me.elsiff.morefish.paper.shop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import me.elsiff.morefish.common.fishing.FishRarity;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import me.elsiff.morefish.paper.fishing.PaperFish;
import me.elsiff.morefish.paper.fishing.PaperFishTypeTable;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.musician101.musigui.paper.chest.PaperIconUtil.customName;
import static io.musician101.musigui.paper.chest.PaperIconUtil.setLore;
import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class FishShopFilterGui extends AbstractFishShopGUI {

    public static final Map<UUID, List<FishRarity<PaperFishingCompetition, PaperFish, Item, Player>>> FILTERS = new HashMap<>();

    @NotNull private final List<FishRarity<PaperFishingCompetition, PaperFish, Item, Player>> selectedRarities;

    public FishShopFilterGui(int page, @NotNull Player user) {
        super(text("Set Sale Filter(s)"), user);
        this.selectedRarities = FILTERS.getOrDefault(user.getUniqueId(), new ArrayList<>());
        updateButtons(page);
        IntStream.of(45, 46, 47, 48, 50, 51, 52, 53).forEach(this::glassPaneButton);
    }

    private void updateButtons(int page) {
        List<FishRarity<PaperFishingCompetition, PaperFish, Item, Player>> fishRarities = new ArrayList<>();
        PaperFishTypeTable fishTypeTable = getPlugin().getFishTypeTable();
        fishTypeTable.getDefaultRarity().ifPresent(fishRarities::add);
        fishTypeTable.getRarities().stream().filter(rarity -> rarity.additionalPrice() >= 0 && !rarity.isDefault()).sorted(Comparator.reverseOrder()).forEach(fishRarities::add);
        IntStream.range(0, 45).forEach(x -> {
            try {
                FishRarity<PaperFishingCompetition, PaperFish, Item, Player> fishRarity = fishRarities.get(x + (page - 1) * 45);
                updateIcon(x, fishRarity);
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        });

        setButton(49, customName(new ItemStack(Material.BARRIER), text("Back to Shop")), ClickType.LEFT, p -> {
            FILTERS.put(p.getUniqueId(), selectedRarities);
            new FishShopGui(p, 1);
        });
    }

    private void updateIcon(int slot, FishRarity<PaperFishingCompetition, PaperFish, Item, Player> fishRarity) {
        Component name = text(fishRarity.displayName(), fishRarity.color());
        Component lore = selectedRarities.contains(fishRarity) ? text("Selected.", GREEN) : text("Not selected.", RED);
        ItemStack itemStack = setLore(customName(new ItemStack(Material.COD), name), lore);
        setButton(slot, itemStack, ClickType.LEFT, p -> {
            if (selectedRarities.contains(fishRarity)) {
                selectedRarities.remove(fishRarity);
            }
            else {
                selectedRarities.add(fishRarity);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> updateIcon(slot, fishRarity), 2);
        });
    }
}
