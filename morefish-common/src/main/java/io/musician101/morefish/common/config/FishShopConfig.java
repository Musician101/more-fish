package io.musician101.morefish.common.config;

import io.musician101.morefish.common.fishing.Fish;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;

public final class FishShopConfig<F extends Fish<?, ?, ?, ?, ?>, P, T> implements ConfigModule {

    @Nonnull
    private final Consumer<P> guiOpener;
    @Nonnull
    private final BiConsumer<P, Collection<F>> sellHandler;
    @Nonnull
    private final Function<Object, T> textMapper;
    protected boolean enabled = false;
    protected double multiplier = 0.1;
    protected boolean roundDecimalPoints = true;
    @Nonnull
    protected T signCreation;
    @Nonnull
    protected T signTitle;

    public FishShopConfig(@Nonnull T signCreation, @Nonnull T signTitle, @Nonnull Function<Object, T> textMapper, @Nonnull Consumer<P> guiOpener, @Nonnull BiConsumer<P, Collection<F>> fishSeller) {
        this.signCreation = signCreation;
        this.signTitle = signTitle;
        this.textMapper = textMapper;
        this.guiOpener = guiOpener;
        this.sellHandler = fishSeller;
    }

    public final double getPriceMultiplier() {
        return multiplier;
    }

    @Nonnull
    public final T getSignCreation() {
        return signCreation;
    }

    @Nonnull
    public final T getSignTitle() {
        return signTitle;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    @Override
    public final void load(@Nonnull ConfigurationNode node) {
        if (node.isVirtual()) {
            return;
        }

        enabled = node.getNode("enable").getBoolean(enabled);
        multiplier = node.getNode("multiplier").getDouble(multiplier);
        roundDecimalPoints = node.getNode("round-decimal-points").getBoolean(roundDecimalPoints);
        ConfigurationNode sign = node.getNode("sign");
        if (sign.isVirtual()) {
            return;
        }

        signTitle = sign.getNode("title").getValue(textMapper, signTitle);
        signCreation = sign.getNode("creation").getValue(textMapper, signCreation);
    }

    public void openGuiTo(@Nonnull P player) {
        guiOpener.accept(player);
    }

    public final double priceOf(@Nonnull F fish) {
        double rarityPrice = fish.getType().getRarity().getAdditionalPrice();
        double price = multiplier * fish.getLength() + rarityPrice;
        return roundDecimalPoints ? Math.floor(price) : price;
    }

    public final boolean roundDecimalPoints() {
        return roundDecimalPoints;
    }

    public final void sell(@Nonnull P player, @Nonnull Collection<F> fish) {
        sellHandler.accept(player, fish);
    }
}
