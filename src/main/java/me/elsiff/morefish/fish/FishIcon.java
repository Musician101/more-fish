package me.elsiff.morefish.fish;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import me.elsiff.morefish.item.TagKey;
import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
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
            ComponentLike argument = Argument.tagResolver(resolver);
            itemStack.setData(DataComponentTypes.CUSTOM_NAME, translate("morefish.main.item-format.display-name", argument, catcher.locale()));
            List<Component> lore = new ArrayList<>();
            lore.add(translate("morefish.main.item-format.lore.length", argument, catcher.locale()));
            lore.add(translate("morefish.main.item-format.lore.catcher", argument, catcher.locale()));
            ItemLore itemLore = itemStack.getData(DataComponentTypes.LORE);
            lore.addAll(parseLore(itemLore == null ? List.of() : itemLore.lines(), resolver));
            itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
        }

        itemStack.editMeta(meta -> {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            TagKey.FISH.setValue(data, fish);
        });
        return itemStack;
    }

    private List<Component> parseLore(List<Component> lore, TagResolver resolver) {
        return lore.stream().map(PlainTextComponentSerializer.plainText()::serialize).map(s -> MiniMessage.miniMessage().deserialize(s, resolver)).collect(Collectors.toList());
    }

    private Component translate(String key, ComponentLike argument, Locale locale) {
        return GlobalTranslator.render(Component.translatable(key, argument), locale);
    }
}
