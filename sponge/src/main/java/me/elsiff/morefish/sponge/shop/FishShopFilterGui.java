package me.elsiff.morefish.sponge.shop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import me.elsiff.morefish.common.fishing.FishRarity;
import me.elsiff.morefish.sponge.fishing.SpongeFish;
import me.elsiff.morefish.sponge.fishing.SpongeFishTypeTable;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.menu.ClickTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

import static io.musician101.musigui.sponge.chest.SpongeIconUtil.customName;
import static io.musician101.musigui.sponge.chest.SpongeIconUtil.setLore;
import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class FishShopFilterGui extends AbstractFishShopGUI {

    public static final Map<UUID, List<FishRarity<SpongeFishingCompetition, SpongeFish, Item, ServerPlayer>>> FILTERS = new HashMap<>();

    @NotNull private final List<FishRarity<SpongeFishingCompetition, SpongeFish, Item, ServerPlayer>> selectedRarities;

    public FishShopFilterGui(int page, @NotNull ServerPlayer user) {
        super(text("Set Sale Filter(s)"), user);
        this.selectedRarities = FILTERS.getOrDefault(user.uniqueId(), new ArrayList<>());
        updateButtons(page);
        IntStream.of(45, 46, 47, 48, 50, 51, 52, 53).forEach(this::glassPaneButton);
    }

    private void updateButtons(int page) {
        List<FishRarity<SpongeFishingCompetition, SpongeFish, Item, ServerPlayer>> fishRarities = new ArrayList<>();
        SpongeFishTypeTable fishTypeTable = getPlugin().getFishTypeTable();
        fishTypeTable.getDefaultRarity().ifPresent(fishRarities::add);
        fishTypeTable.getRarities().stream().filter(rarity -> rarity.additionalPrice() >= 0 && !rarity.isDefault()).sorted(Comparator.reverseOrder()).forEach(fishRarities::add);
        IntStream.range(0, 45).forEach(x -> {
            try {
                FishRarity<SpongeFishingCompetition, SpongeFish, Item, ServerPlayer> fishRarity = fishRarities.get(x + (page - 1) * 45);
                updateIcon(x, fishRarity);
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        });

        setButton(49, customName(ItemStack.of(ItemTypes.BARRIER), text("Back to Shop")), ClickTypes.CLICK_LEFT, p -> {
            FILTERS.put(p.uniqueId(), selectedRarities);
            new FishShopGui(p, 1);
        });
    }

    private void updateIcon(int slot, FishRarity<SpongeFishingCompetition, SpongeFish, Item, ServerPlayer> fishRarity) {
        Component name = text(fishRarity.displayName(), fishRarity.color());
        Component lore = selectedRarities.contains(fishRarity) ? text("Selected.", GREEN) : text("Not selected.", RED);
        ItemStack itemStack = setLore(customName(ItemStack.of(ItemTypes.COD), name), lore);
        setButton(slot, itemStack, ClickTypes.CLICK_LEFT, p -> {
            if (selectedRarities.contains(fishRarity)) {
                selectedRarities.remove(fishRarity);
            }
            else {
                selectedRarities.add(fishRarity);
            }

            Sponge.asyncScheduler().submit(Task.builder().plugin(getPlugin().getPluginContainer()).execute(() -> updateIcon(slot, fishRarity)).delay(Ticks.of(2)).build());
        });
    }
}
