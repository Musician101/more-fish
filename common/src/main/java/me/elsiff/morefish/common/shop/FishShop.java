package me.elsiff.morefish.common.shop;

import me.elsiff.morefish.common.fishing.Fish;
import org.jetbrains.annotations.NotNull;

public abstract class FishShop {

    public abstract boolean getEnabled();

    protected abstract double getPriceMultiplier();

    protected abstract boolean getRoundDecimalPoints();

    public double priceOf(@NotNull Fish<?> fish) {
        double rarityPrice = fish.type().additionalPrice();
        double price = this.getPriceMultiplier() * fish.length() + rarityPrice;
        return getRoundDecimalPoints() ? Math.floor(price) : price;
    }
}
