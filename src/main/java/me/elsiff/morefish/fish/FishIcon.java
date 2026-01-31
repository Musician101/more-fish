package me.elsiff.morefish.fish;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import me.elsiff.morefish.item.TagKey;
import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.util.List;

import static me.elsiff.morefish.MoreFish.lang;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public class FishIcon {

    private ItemStack itemStack;

    public FishIcon(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack itemStack() {
        return itemStack;
    }

    public void itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack createItemStack(Fish fish, Player catcher) {
        ItemStack itemStack = this.itemStack.clone();
        if (!fish.type().skipItemFormat()) {
            TagResolver resolver = TagResolverUtil.catcher(catcher, fish);
            NodePath itemFormat = NodePath.path("main", "item-format");
            itemStack.setData(DataComponentTypes.CUSTOM_NAME, lang().getComponent(itemFormat.withAppendedChild("display-name"), resolver));
            List<Component> lore = lang().getComponents(itemFormat.withAppendedChild("lore"), resolver);
            ItemLore itemLore = itemStack.getData(DataComponentTypes.LORE);
            lore.addAll(lang().parseComponents(itemLore == null ? List.of() : itemLore.lines(), resolver));
            itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
        }

        itemStack.editMeta(meta -> {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            TagKey.FISH.setValue(data, fish);
        });
        return itemStack;
    }
}
