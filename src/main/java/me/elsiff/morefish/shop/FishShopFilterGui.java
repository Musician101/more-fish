package me.elsiff.morefish.shop;

import java.util.ArrayList;
import java.util.Collections;
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
import me.elsiff.morefish.gui.GUIButton;
import me.elsiff.morefish.item.FishItemStackConverter;
import me.elsiff.morefish.util.ItemUtil;
import me.elsiff.morefish.util.OneTickScheduler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class FishShopFilterGui extends AbstractFishShopGUI {

    public static final Map<UUID, List<FishRarity>> filters = new HashMap<>();

    @Nonnull
    private final List<FishRarity> selectedRarities;

    public FishShopFilterGui(int page, @Nonnull FishShop shop, @Nonnull FishItemStackConverter converter, @Nonnull OneTickScheduler oneTickScheduler, @Nonnull Player user) {
        super("Set Sale Filter(s)", shop, converter, oneTickScheduler, user);
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
        setButton(new GUIButton(49, ClickType.LEFT, ItemUtil.named(Material.BARRIER, "Back to Shop"), p -> {
            filters.put(p.getUniqueId(), selectedRarities);
            new FishShopGui(shop, converter, oneTickScheduler, p, 1);
        }));
    }

    private void updateIcon(int slot, FishRarity fishRarity) {
        ItemStack itemStack = ItemUtil.named(Material.COD, fishRarity.getColor() + fishRarity.getDisplayName());
        if (selectedRarities.contains(fishRarity)) {
            ItemUtil.setLore(itemStack, Collections.singletonList(ChatColor.GREEN + "Selected."));
        }
        else {
            ItemUtil.setLore(itemStack, Collections.singletonList(ChatColor.RED + "Not selected."));
        }

        setButton(new GUIButton(slot, ClickType.LEFT, itemStack, p -> {
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
