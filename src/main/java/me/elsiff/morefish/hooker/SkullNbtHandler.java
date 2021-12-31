package me.elsiff.morefish.hooker;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.inventory.ItemStack;

public final class SkullNbtHandler {

    @Nonnull
    public ItemStack writeTexture(@Nonnull ItemStack itemStack, @Nonnull String textureValue) {
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
