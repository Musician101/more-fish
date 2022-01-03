package io.musician101.morefish.forge.shop;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.morefish.forge.announcement.ForgePlayerAnnouncement;
import io.musician101.morefish.forge.fishing.catchhandler.ForgeCatchHandler;
import io.musician101.morefish.forge.gui.MoreFishGUI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class FishShopFilterGui extends MoreFishGUI {

    public static final Map<UUID, List<FishRarity<ForgePlayerAnnouncement, TextFormatting, ForgeCatchHandler>>> filters = new HashMap<>();

    @Nonnull
    private final List<FishRarity<ForgePlayerAnnouncement, TextFormatting, ForgeCatchHandler>> selectedRarities;
    private int page = 1;

    public FishShopFilterGui(@Nonnull ServerPlayerEntity user) {
        super("Set Sale Filter(s)", user);
        this.selectedRarities = filters.getOrDefault(user.getUniqueID(), new ArrayList<>());
        updateIcons();
        IntStream.of(46, 47, 48, 50, 51, 52).forEach(this::glassPaneButton);
        setButton(49, BACK, ImmutableMap.of(ClickType.LEFT, p -> {
            filters.put(p.getUniqueID(), selectedRarities);
            new FishShopGui(p);
        }));
    }

    private void updateIcon(int slot, FishRarity<ForgePlayerAnnouncement, TextFormatting, ForgeCatchHandler> fishRarity) {
        ItemStack itemStack = ForgeIconBuilder.builder(Material.COD).name(fishRarity.getColor() + fishRarity.getDisplayName()).description((selectedRarities.contains(fishRarity) ? TextFormatting.GREEN + "S" : TextFormatting.RED + "Not s") + "elected").build();
        setButton(slot, itemStack, ImmutableMap.of(ClickType.LEFT, p -> {
            if (selectedRarities.contains(fishRarity)) {
                selectedRarities.remove(fishRarity);
            }
            else {
                selectedRarities.add(fishRarity);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(ForgeMoreFish.getInstance(), () -> updateIcon(slot, fishRarity), 2);
        }));
    }

    private void updateIcons() {
        List<FishRarity<ForgePlayerAnnouncement, TextFormatting, ForgeCatchHandler>> fishRarities = new ArrayList<>();
        FishConfig fishConfig = ForgeMoreFish.getInstance().getPluginConfig().getFishConfig();
        fishConfig.getDefaultRarity().ifPresent(fishRarities::add);
        fishConfig.getRarities().stream().filter(rarity -> rarity.getAdditionalPrice() >= 0 && !rarity.isDefault()).sorted(Comparator.reverseOrder()).forEach(fishRarities::add);
        IntStream.range(0, 45).forEach(x -> {
            try {
                FishRarity<ForgePlayerAnnouncement, TextFormatting, ForgeCatchHandler> fishRarity = fishRarities.get(x + (page - 1) * 45);
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
