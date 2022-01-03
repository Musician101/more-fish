package io.musician101.morefish.common.config;

import io.musician101.morefish.common.fishing.Fish;
import javax.annotation.Nonnull;
import org.spongepowered.configurate.ConfigurationNode;

public record FishShopConfig(boolean enabled, double multiplier, boolean roundDecimalPoints,
                             @Nonnull ConfigurationNode signCreation, @Nonnull ConfigurationNode signTitle) {

    public static FishShopConfig deserialize(@Nonnull ConfigurationNode node) {
        boolean enabled = node.node("enable").getBoolean(false);
        double multiplier = node.node("multiplier").getDouble(0.1);
        boolean roundDecimalPoints = node.node("round-decimal-points").getBoolean(true);
        ConfigurationNode sign = node.node("sign");
        ConfigurationNode signTitle = sign.node("title");
        ConfigurationNode signCreation = sign.node("creation");
        return new FishShopConfig(enabled, multiplier, roundDecimalPoints, signTitle, signCreation);
    }

    @Nonnull
    public ConfigurationNode getSignCreation() {
        return signCreation;
    }

    @Nonnull
    public ConfigurationNode getSignTitle() {
        return signTitle;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double priceOf(@Nonnull Fish fish) {
        double rarityPrice = fish.getType().getRarity().getAdditionalPrice();
        double price = multiplier * fish.getLength() + rarityPrice;
        return roundDecimalPoints ? Math.floor(price) : price;
    }
}
