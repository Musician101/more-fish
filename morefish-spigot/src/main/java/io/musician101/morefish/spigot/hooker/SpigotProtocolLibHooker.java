package io.musician101.morefish.spigot.hooker;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import io.musician101.morefish.common.hooker.PluginHooker;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.inventory.ItemStack;

public final class SpigotProtocolLibHooker implements SpigotPluginHooker {

    private boolean hasHooked;

    @Nonnull
    public String getPluginName() {
        return "ProtocolLib";
    }

    public boolean hasHooked() {
        return this.hasHooked;
    }

    public void hook() {
        PluginHooker.checkEnabled(this);
        hasHooked = true;
    }

    public void setHasHooked(boolean var1) {
        this.hasHooked = var1;
    }

    @Nonnull
    public final ItemStack writeTexture(@Nonnull ItemStack itemStack, @Nonnull String textureValue) {
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
