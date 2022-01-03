package io.musician101.morefish.forge.item;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.morefish.forge.config.format.ForgeTextFormat;
import io.musician101.morefish.forge.config.format.ForgeTextListFormat;
import io.musician101.morefish.forge.fishing.catchhandler.ForgeCatchHandler;
import io.musician101.morefish.forge.fishing.condition.ForgeFishCondition;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextComponent.Serializer;
import net.minecraftforge.common.util.Constants.NBT;

public final class FishItemStackConverter {

    private final String fishLengthKey = "fishLength";
    private final String fishTypeKey = "fishType";

    @Nonnull
    public final ItemStack createItemStack(@Nonnull Fish fish, @Nonnull UUID catcher) {
        ItemStack itemStack = fish.getType().getIcon().copy();
        if (!fish.getType().hasNotFishItemFormat()) {
            Map<String, ITextComponent> replacement = getFormatReplacementMap(fish, catcher);
            CompoundNBT tag = itemStack.getOrCreateTag();
            CompoundNBT display = tag.getCompound("display");
            display.putString("Name", Serializer.toJson(new ForgeTextFormat(getFishConfig().getDisplayNameFormat()).replace(replacement).output(catcher)));
            ListNBT lore = tag.getList("Lore", NBT.TAG_STRING);
            lore.addAll(new ForgeTextListFormat(getFishConfig().getLoreFormat()).replace(replacement).output(catcher).stream().map(Serializer::toJson).map(StringNBT::valueOf).collect(Collectors.toList()));
            display.put("Lore", lore);
            tag.put("display", display);
            tag.putString(fishTypeKey, fish.getType().getName());
            tag.putDouble(fishLengthKey, fish.getLength());
            itemStack.setTag(tag);
        }

        return itemStack;
    }

    @Nonnull
    public final Fish fish(@Nonnull ItemStack itemStack) {
        CompoundNBT tag = itemStack.getOrCreateTag();
        if (!tag.contains(fishTypeKey, NBT.TAG_STRING)) {
            throw new IllegalArgumentException("Item meta must have fish type tag");
        }

        if (!tag.contains(fishLengthKey, NBT.TAG_DOUBLE)) {
            throw new IllegalArgumentException("Item meta must have fish length tag");
        }

        String typeName = tag.getString(fishTypeKey);
        FishType<ForgeFishCondition, ForgeCatchHandler, ItemStack> type = getFishConfig().getTypes().stream().filter(it -> it.getName().equals(typeName)).findFirst().orElseThrow(() -> new IllegalStateException("Fish type doesn't exist"));
        String length = tag.getDouble(fishLengthKey);
        return new Fish(type, length);
    }

    private FishConfig getFishConfig() {
        return ForgeMoreFish.getInstance().getPluginConfig().getFishConfig();
    }

    private Map<String, ITextComponent> getFormatReplacementMap(Fish fish, UUID catcher) {
        return ImmutableMap.of("%player%", catcher.getName(), "%rarity%", fish.getType().getRarity().getName().toUpperCase(), "%rarity_color%", fish.getType().getRarity().getColor().toString(), "%length%", fish.getLength(), "%fish%", fish.getType().getDisplayName());
    }

    public final boolean isFish(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        CompoundNBT tag = itemStack.getOrCreateTag();
        return tag.contains("fishType", NBT.TAG_STRING) && tag.contains("fishLength", NBT.TAG_DOUBLE);
    }
}
