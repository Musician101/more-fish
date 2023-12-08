package me.elsiff.morefish.sponge.fishing;

import java.util.UUID;
import me.elsiff.morefish.common.fishing.FishBag;
import me.elsiff.morefish.sponge.item.FishItemStackConverter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.item.inventory.ItemStack;

public class SpongeFishBag extends FishBag<ItemStack> {

    public SpongeFishBag(@NotNull UUID uuid) {
        super(uuid);
    }

    @Override
    protected boolean isContraband(@NotNull ItemStack itemStack) {
        return !FishItemStackConverter.isFish(itemStack);
    }
}
