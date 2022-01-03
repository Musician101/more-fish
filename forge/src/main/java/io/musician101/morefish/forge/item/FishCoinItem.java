package io.musician101.morefish.forge.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class FishCoinItem extends Item {

    public static FishCoinItem FISH_COIN_ITEM = new FishCoinItem();
    public static final ItemGroup FISH_COIN_GROUP = new ItemGroup(629, "fish_coin") {

        @Override
        public ItemStack createIcon() {
            return new ItemStack(FISH_COIN_ITEM);
        }
    };

    public FishCoinItem() {
        super(new Properties().group(FISH_COIN_GROUP));
    }
}
