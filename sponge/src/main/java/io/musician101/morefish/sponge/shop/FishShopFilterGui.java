package io.musician101.morefish.sponge.shop;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.gui.MoreFishGUI;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.chest.SpongeIconBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.menu.ClickTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

public class FishShopFilterGui extends MoreFishGUI {

    public static final Map<UUID, List<FishRarity>> filters = new HashMap<>();

    @Nonnull
    private final List<FishRarity> selectedRarities;
    private int page = 1;

    public FishShopFilterGui(@Nonnull ServerPlayer player) {
        super(Component.text("Set Sale Filter(s)"), player, true);
        this.selectedRarities = filters.getOrDefault(player.uniqueId(), new ArrayList<>());
        updateIcons();
        IntStream.of(46, 47, 48, 50, 51, 52).forEach(this::glassPaneButton);
        setButton(49, BACK, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
            filters.put(p.uniqueId(), selectedRarities);
            new FishShopGui(p);
        }));
    }

    private void updateIcon(int slot, FishRarity fishRarity) {
        Component name = LegacyComponentSerializer.legacyAmpersand().deserialize(fishRarity.getColor() + fishRarity.getDisplayName());
        ItemStack itemStack = SpongeIconBuilder.builder(ItemTypes.COD).name(name).description(Component.join(Component.text(), selectedRarities.contains(fishRarity) ? Component.text("S", NamedTextColor.GREEN) : Component.text("Not s", NamedTextColor.RED), Component.text("elected"))).build();
        setButton(slot, itemStack, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
            if (selectedRarities.contains(fishRarity)) {
                selectedRarities.remove(fishRarity);
            }
            else {
                selectedRarities.add(fishRarity);
            }

            Sponge.asyncScheduler().submit(Task.builder().delay(Ticks.of(2)).execute(() -> updateIcon(slot, fishRarity)).plugin(SpongeMoreFish.getInstance().getPluginContainer()).build());
        }));
    }

    private void updateIcons() {
        List<FishRarity> fishRarities = new ArrayList<>();
        FishConfig fishConfig = SpongeMoreFish.getInstance().getConfig().getFishConfig();
        fishConfig.getDefaultRarity().ifPresent(fishRarities::add);
        fishConfig.getRarities().stream().filter(rarity -> rarity.getAdditionalPrice() >= 0 && !rarity.isDefault()).sorted(Comparator.reverseOrder()).forEach(fishRarities::add);
        IntStream.range(0, 45).forEach(x -> {
            try {
                FishRarity fishRarity = fishRarities.get(x + (page - 1) * 45);
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
            setButton(45, PREVIOUS, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
                page--;
                updateIcons();
            }));
        }

        if (page < maxPages) {
            setButton(53, NEXT, ImmutableMap.of(ClickTypes.CLICK_LEFT.get(), p -> {
                page++;
                updateIcons();
            }));
        }
        else {
            glassPaneButton(53);
        }
    }
}
