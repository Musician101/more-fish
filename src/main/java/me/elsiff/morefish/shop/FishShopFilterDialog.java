package me.elsiff.morefish.shop;

import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishTypeTable;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
@SuppressWarnings("UnstableAPIUsage")
public class FishShopFilterDialog extends MusiDialog {

    public static final Map<UUID, List<FishRarity>> FILTERS = new HashMap<>();
    private final List<FishRarity> filters;

    public FishShopFilterDialog(List<FishRarity> filters) {
        super(lang().getComponent("main", "sales-filter", "label"));
        this.filters = filters;
    }

    private FishTypeTable fishTypeTable() {
        return getPlugin().getFishTypeTable();
    }

    @Override
    protected List<DialogInput> inputs() {
        List<DialogInput> list = new ArrayList<>();
        fishTypeTable().getRarities().stream().filter(r -> !r.doNotSell()).sorted(Comparator.reverseOrder()).forEach(r -> {
            Component label = lang().getComponent(r, "main", "sales-filter", "rarity");
            list.add(boolInput(r.name(), label, filters.contains(r)));
        });
        return list;
    }

    @Override
    protected DialogType type() {
        return DialogType.notice(backButton((view, audience) -> {
            List<FishRarity> filters = new ArrayList<>();
            fishTypeTable().getRarities().forEach(r -> {
                Boolean bool = view.getBoolean(r.name());
                if (bool != null && bool) {
                    filters.add(r);
                }
            });
            FILTERS.put(((Player) audience).getUniqueId(), filters);
            new FishShopGui((Player) audience);
        }));
    }
}
