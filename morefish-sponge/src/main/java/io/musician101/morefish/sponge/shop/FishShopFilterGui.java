package io.musician101.morefish.sponge.shop;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchHandler;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import io.musician101.morefish.sponge.gui.MoreFishGUI;
import io.musician101.musicianlibrary.java.minecraft.sponge.gui.SpongeIconBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.Fishes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent.Primary;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class FishShopFilterGui extends MoreFishGUI {

    public static final Map<UUID, List<FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>>> filters = new HashMap<>();

    @Nonnull
    private final List<FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>> selectedRarities;
    private int page = 1;

    public FishShopFilterGui(@Nonnull Player user) {
        super(Text.of("Set Sale Filter(s)"), user);
        this.selectedRarities = filters.getOrDefault(user.getUniqueId(), new ArrayList<>());
        updateIcons();
        IntStream.of(46, 47, 48, 50, 51, 52).forEach(this::glassPaneButton);
        setButton(49, BACK, ImmutableMap.of(Primary.class, p -> {
            filters.put(p.getUniqueId(), selectedRarities);
            new FishShopGui(p);
        }));
    }

    private void updateIcon(int slot, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler> fishRarity) {
        ItemStack itemStack = SpongeIconBuilder.builder(ItemTypes.FISH).offer(Keys.FISH_TYPE, Fishes.COD).name(Text.of(fishRarity.getColor(), fishRarity.getDisplayName())).description(Text.of(selectedRarities.contains(fishRarity) ? Text.of(TextColors.GREEN, "S") : Text.of(TextColors.RED, "Not s"), Text.of("elected"))).build();
        setButton(slot, itemStack, ImmutableMap.of(Primary.class, p -> {
            if (selectedRarities.contains(fishRarity)) {
                selectedRarities.remove(fishRarity);
            }
            else {
                selectedRarities.add(fishRarity);
            }

            Task.builder().delayTicks(2).execute(() -> updateIcon(slot, fishRarity)).submit(SpongeMoreFish.getInstance());
        }));
    }

    private void updateIcons() {
        List<FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>> fishRarities = new ArrayList<>();
        FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>> fishConfig = SpongeMoreFish.getInstance().getConfig().getFishConfig();
        fishConfig.getDefaultRarity().ifPresent(fishRarities::add);
        fishConfig.getRarities().stream().filter(rarity -> rarity.getAdditionalPrice() >= 0 && !rarity.isDefault()).sorted(Comparator.reverseOrder()).forEach(fishRarities::add);
        IntStream.range(0, 45).forEach(x -> {
            try {
                FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler> fishRarity = fishRarities.get(x + (page - 1) * 45);
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
            setButton(45, PREVIOUS, ImmutableMap.of(Primary.class, p -> {
                page--;
                updateIcons();
            }));
        }

        if (page < maxPages) {
            setButton(53, NEXT, ImmutableMap.of(Primary.class, p -> {
                page++;
                updateIcons();
            }));
        }
        else {
            glassPaneButton(53);
        }
    }
}
