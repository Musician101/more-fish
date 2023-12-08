package me.elsiff.morefish.paper.hooker;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class SkullNbtHandler {

    @NotNull
    public ItemStack writeTexture(@NotNull ItemStack itemStack, @NotNull String textureValue) {
        ItemStack editingStack = MinecraftReflection.getBukkitItemStack(itemStack);
        if (MinecraftReflection.isCraftItemStack(itemStack)) {
            editingStack = itemStack;
        }

        NbtCompound tag = (NbtCompound) NbtFactory.fromItemTag(editingStack);
        NbtCompound skullOwner = NbtFactory.ofCompound("SkullOwner");
        NbtCompound properties = NbtFactory.ofCompound("Properties");
        NbtCompound compound = NbtFactory.ofCompound("");
        compound.put("Value", textureValue);
        NbtList<NbtCompound> textures = NbtFactory.ofList("textures", compound);
        properties.put(textures);
        skullOwner.put("Id", UUID.randomUUID().toString());
        tag.put(skullOwner);
        NbtFactory.setItemTag(editingStack, tag);
        return editingStack;
    }
}
