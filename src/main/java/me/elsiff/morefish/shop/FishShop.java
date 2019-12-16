package me.elsiff.morefish.shop;

import java.util.Collection;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.hooker.VaultHooker;
import me.elsiff.morefish.item.FishItemStackConverter;
import me.elsiff.morefish.util.OneTickScheduler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class FishShop {

    private final FishItemStackConverter converter;
    private final OneTickScheduler oneTickScheduler;
    private final VaultHooker vault;

    public FishShop(@Nonnull OneTickScheduler oneTickScheduler, @Nonnull FishItemStackConverter converter, @Nonnull VaultHooker vault) {
        super();
        this.oneTickScheduler = oneTickScheduler;
        this.converter = converter;
        this.vault = vault;
    }

    private Economy getEconomy() {
        if (!vault.hasHooked()) {
            throw new IllegalStateException("Vault must be hooked for fish shop feature");
        }

        if (!vault.hasEconomy()) {
            throw new IllegalStateException("Vault doesn't have economy plugin");
        }

        if (!vault.getEconomy().isEnabled()) {
            throw new IllegalStateException("Economy must be enabled");
        }

        return vault.getEconomy();
    }

    public final boolean getEnabled() {
        return getShopConfig().getBoolean("enable");
    }

    private double getPriceMultiplier() {
        return getShopConfig().getDouble("multiplier");
    }

    private boolean getRoundDecimalPoints() {
        return getShopConfig().getBoolean("round-decimal-points");
    }

    private ConfigurationSection getShopConfig() {
        return Config.INSTANCE.getStandard().getConfigurationSection("fish-shop");
    }

    public final void openGuiTo(@Nonnull Player player) {
        new FishShopGui(this, converter, oneTickScheduler, player, 1);
    }

    public final double priceOf(@Nonnull Fish fish) {
        double rarityPrice = fish.getType().getRarity().getAdditionalPrice();
        double price = this.getPriceMultiplier() * fish.getLength() + rarityPrice;
        return getRoundDecimalPoints() ? Math.floor(price) : price;
    }

    public final void sell(@Nonnull Player player, @Nonnull Fish fish) {
        getEconomy().depositPlayer(player, priceOf(fish));
    }

    public final void sell(@Nonnull Player player, @Nonnull Collection<Fish> fish) {
        getEconomy().depositPlayer(player, fish.stream().mapToDouble(this::priceOf).sum());
    }
}
