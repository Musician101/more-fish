package io.musician101.morefish.spigot.shop;

import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchHandler;
import io.musician101.morefish.spigot.fishing.competition.SpigotPrize;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import javax.annotation.Nonnull;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

@TraitName("fishshop")
public final class FishShopKeeperTrait extends Trait {

    public FishShopKeeperTrait() {
        super("fishshop");
    }

    @EventHandler
    public final void onClickNpc(@Nonnull NPCRightClickEvent event) {
        Config<FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>>, LangConfig<SpigotTextFormat, SpigotTextListFormat, String>, MessagesConfig<SpigotPlayerAnnouncement, BarColor>, FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String>, SpigotPrize> config = SpigotMoreFish.getInstance().getPluginConfig();
        LangConfig<SpigotTextFormat, SpigotTextListFormat, String> langConfig = config.getLangConfig();
        FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String> fishShopConfig = config.getFishShopConfig();
        boolean enabled = config.getFishShopConfig().isEnabled();
        if (event.getNPC() == npc && SpigotMoreFish.getInstance().isEnabled()) {
            if (enabled) {
                fishShopConfig.openGuiTo(event.getClicker());
            }
        }
        else {
            event.getClicker().sendMessage(langConfig.text("shop-disabled"));
        }

        if (npc.equals(event.getNPC()) && SpigotMoreFish.getInstance().isEnabled()) {
            if (enabled) {
                fishShopConfig.openGuiTo(event.getClicker());
            }
            else {
                event.getClicker().sendMessage(langConfig.text("shop-disabled"));
            }
        }
    }
}
