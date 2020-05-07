package io.musician101.morefish.sponge.shop;

import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchHandler;
import io.musician101.morefish.sponge.fishing.competition.SpongePrize;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import java.util.List;
import javax.annotation.Nonnull;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent.Secondary;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.Location;

public final class FishShopSignListener {

    private Config<FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>>, LangConfig<SpongeTextFormat, SpongeTextListFormat, Text>, MessagesConfig<SpongePlayerAnnouncement, BossBarColor>, FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text>, SpongePrize> getConfig() {
        return SpongeMoreFish.getInstance().getConfig();
    }

    private FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text> getFishShopConfig() {
        return getConfig().getFishShopConfig();
    }

    private LangConfig<SpongeTextFormat, SpongeTextListFormat, Text> getLangConfig() {
        return getConfig().getLangConfig();
    }

    private Text getShopSignTitle() {
        return getFishShopConfig().getSignTitle();
    }

    @Listener
    public final void onPlayerInteract(@Nonnull Secondary event, @First Player player, @Getter("getTargetBlock") BlockSnapshot block) {
        block.getLocation().flatMap(Location::getTileEntity).filter(Sign.class::isInstance).map(Sign.class::cast).map(Sign::lines).filter(l -> !l.isEmpty() && getShopSignTitle().equals(l.get(0))).ifPresent(l -> {
            if (getFishShopConfig().isEnabled()) {
                SpongeMoreFish.getInstance().getConfig().getFishShopConfig().openGuiTo(player);
                return;
            }

            player.sendMessage(getLangConfig().text("shop-disabled"));
        });
    }

    @Listener
    public final void onSignChange(@Nonnull ChangeSignEvent event, @First Player player, @Getter("getTargetTile") Sign sign, @Getter("getText") SignData signData) {
        List<Text> lines = signData.lines().get();
        if (lines.get(0).equals(getFishShopConfig().getSignCreation()) || lines.get(0).equals(getShopSignTitle())) {
            if (player.hasPermission("morefish.admin")) {
                lines.set(0, getShopSignTitle());
                signData.setElements(lines);
                sign.offer(signData);
                player.sendMessage(getLangConfig().text("created-sign-shop"));
                return;
            }

            player.sendMessage(getLangConfig().text("no-permission"));
        }
    }
}
