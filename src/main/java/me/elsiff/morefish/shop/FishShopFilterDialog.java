package me.elsiff.morefish.shop;

import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.gui.MusiDialog;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public class FishShopFilterDialog extends MusiDialog {

    public static final Map<UUID, List<FishRarity>> FILTERS = new HashMap<>();
    private final List<FishRarity> filters;

    public FishShopFilterDialog(List<FishRarity> filters, Locale locale) {
        super(Component.translatable("morefish.main.sales-filter.label"), locale);
        this.filters = filters;
    }

    @Override
    protected List<DialogInput> inputs() {
        List<DialogInput> list = new ArrayList<>();
        getPlugin().rarities().stream().filter(r -> !r.doNotSell()).sorted(Comparator.reverseOrder()).forEach(r -> {
            Component label = Component.translatable("morefish.main.sales-filter.rarity");
            list.add(boolInput(asDialogInputId(r), label, filters.contains(r)));
        });
        return list;
    }

    private String asDialogInputId(FishRarity rarity) {
        return rarity.getKey().asString().replace(":", "__");
    }

    @Override
    protected DialogType type() {
        return DialogType.notice(backButton((view, audience) -> {
            List<FishRarity> filters = new ArrayList<>();
            getPlugin().rarities().forEach(r -> {
                Boolean bool = view.getBoolean(asDialogInputId(r));
                if (bool != null && bool) {
                    filters.add(r);
                }
            });
            FILTERS.put(((Player) audience).getUniqueId(), filters);
            new FishShopGui((Player) audience);
        }));
    }
}
