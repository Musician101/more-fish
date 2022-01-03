package io.musician101.morefish.forge.fishing.catchhandler;

import io.musician101.morefish.common.fishing.Fish;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public final class ForgeCatchFireworkSpawner implements ForgeCatchHandler {

    public void handle(@Nonnull UUID catcherID, @Nonnull Fish fish) {
        if (fish.getType().hasCatchFirework()) {
            CompoundNBT effect = new CompoundNBT();
            effect.putBoolean("Flicker", true);
            effect.putBoolean("Trail", true);
            effect.putByte("Type", (byte) 1);
            effect.putIntArray("Colors", new int[]{0, 255, 255});
            effect.putIntArray("FadeColors", new int[]{0, 0, 255});
            CompoundNBT tag = new CompoundNBT();
            tag.putByte("Flight", (byte) 1);
            ListNBT explosions = new ListNBT();
            explosions.add(tag);
            tag.put("Explosions", explosions);
            ItemStack firework = new ItemStack(Items.FIREWORK_ROCKET);
            firework.setTag(tag);
            World world = catcherID.getEntityWorld();
            Vector3d position = catcherID.getPositionVec();
            FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(world, position.x, position.y, position.z, firework);
            world.addEntity(fireworkRocket);
        }
    }
}
