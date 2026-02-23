package me.elsiff.morefish.gui;

import com.google.common.collect.Lists;
import io.musician101.musigui.paper.chest.PaperChestGUI;
import io.musician101.musigui.paper.chest.PaperIconUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import me.elsiff.morefish.MoreFish;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public abstract class MoreFishGUI extends PaperChestGUI<MoreFish> {

    protected MoreFishGUI(Player player, Component name, int size) {
        super(player, name, size, getPlugin(), false);
    }

    protected void previousPageButton(int page, Consumer<Player> action) {
        if (page > 1) {
            TranslatableComponent name = Component.translatable("morefish.gui.previous-page.name");
            TranslatableComponent lore = Component.translatable("morefish.gui.previous-page.lore");
            setButton(5, 3, createIcon(Material.ARROW, name, lore), ClickType.LEFT, action);
        }
    }

    protected void nextPageButton(int page, int pages, Consumer<Player> action) {
        if (page < pages) {
            TranslatableComponent name = Component.translatable("morefish.gui.next-page.name");
            TranslatableComponent lore = Component.translatable("morefish.gui.next-page.lore");
            setButton(5, 5, createIcon(Material.ARROW, name, lore), ClickType.LEFT, action);
        }
    }

    protected void addGlassPane(int slot) {
        addItem(slot, PaperIconUtil.builder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).name(Component.empty()).build());
    }

    protected ItemStack createIcon(Material material, TranslatableComponent name) {
        return createIcon(material, name, List.of());
    }

    protected ItemStack createIcon(Material material, TranslatableComponent name, TranslatableComponent lore) {
        return createIcon(material, name, List.of(lore));
    }

    protected ItemStack createIcon(Material material, TranslatableComponent name, List<TranslatableComponent> lore) {
        return PaperIconUtil.builder(material)
                .setData(DataComponentTypes.CUSTOM_NAME, translate(name))
                .setData(DataComponentTypes.LORE, ItemLore.lore(Lists.transform(lore, this::translate)))
                .build();
    }

    private Component translate(TranslatableComponent translatableComponent) {
        return GlobalTranslator.render(translatableComponent, player.locale());
    }
}
