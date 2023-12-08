package me.elsiff.morefish.paper.fishing;

import java.util.UUID;
import me.elsiff.morefish.common.fishing.FishBag;
import me.elsiff.morefish.paper.item.FishItemStackConverter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PaperFishBag extends FishBag<ItemStack> {

    public PaperFishBag(@NotNull UUID uuid) {
        super(uuid);
    }

    @Override
    protected boolean isContraband(@NotNull ItemStack itemStack) {
        return !FishItemStackConverter.isFish(itemStack);
    }
}
