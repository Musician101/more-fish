package me.elsiff.morefish.gui;

import io.musician101.musigui.paper.chest.PaperChestGUI;
import io.musician101.musigui.paper.chest.PaperIconUtil;
import me.elsiff.morefish.MoreFish;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.util.List;
import java.util.function.Consumer;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
public class MoreFishGUI extends PaperChestGUI<MoreFish> {

    protected static final NodePath GUI_PATH = NodePath.path("gui");

    protected MoreFishGUI(Player player, Component name, int size) {
        super(player, name, size, getPlugin(), false);
    }

    protected void previousPageButton(int page, Consumer<Player> action) {
        if (page > 1) {
            NodePath previousPage = GUI_PATH.withAppendedChild("previous-page");
            Component name = lang().getComponent(previousPage.withAppendedChild("name"));
            List<Component> lore = lang().getComponents(previousPage.withAppendedChild("lore"));
            setButton(5, 3, createIcon(Material.ARROW, name, lore), ClickType.LEFT, action);
        }
    }

    protected void nextPageButton(int page, int pages, Consumer<Player> action) {
        if (page < pages) {
            NodePath nextPage = GUI_PATH.withAppendedChild("next-page");
            Component name = lang().getComponent(nextPage.withAppendedChild("name"));
            List<Component> lore = lang().getComponents(nextPage.withAppendedChild("lore"));
            setButton(5, 5, createIcon(Material.ARROW, name, lore), ClickType.LEFT, action);
        }
    }

    protected void addItem(int slot, Material material) {
        addItem(slot, createIcon(material));
    }

    protected void addItems(List<Integer> slots, Material material) {
        addItems(slots, createIcon(material));
    }

    protected void setBackButton(int row, int column, Consumer<Player> action) {
        NodePath back = GUI_PATH.withAppendedChild("back");
        Component name = lang().getComponent(back.withAppendedChild("name"));
        List<Component> lore = lang().getComponents(back.withAppendedChild("lore"));
        ItemStack icon = createIcon(Material.MANGROVE_DOOR, name, lore);
        setButton(row, column, icon, ClickType.LEFT, action);
    }

    protected ItemStack createIcon(Material material) {
        return createIcon(material, Component.empty());
    }

    protected ItemStack createIcon(Material material, Component name) {
        return PaperIconUtil.builder(material).name(name).build();
    }

    protected ItemStack createIcon(Material material, Component name, Component lore) {
        return createIcon(material, name, List.of(lore));
    }

    protected ItemStack createIcon(Material material, Component name, List<Component> lore) {
        return PaperIconUtil.builder(material).name(name).lore(lore).build();
    }
}
