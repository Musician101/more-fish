package io.musician101.morefish.spigot.shop;

import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import javax.annotation.Nonnull;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;

@TraitName("fishshop")
public final class FishShopKeeperTrait extends Trait {

    public FishShopKeeperTrait() {
        super("fishshop");
    }

    @EventHandler
    public void onClickNpc(@Nonnull NPCRightClickEvent event) {
        Config<SpigotTextFormat, SpigotTextListFormat, String> config = SpigotMoreFish.getInstance().getPluginConfig();
        LangConfig<SpigotTextFormat, SpigotTextListFormat, String> langConfig = config.getLangConfig();
        boolean enabled = config.getFishShopConfig().isEnabled();
        if (event.getNPC() == npc && SpigotMoreFish.getInstance().isEnabled()) {
            if (enabled) {
                new FishShopGui(event.getClicker());
            }
        }
        else {
            event.getClicker().sendMessage(langConfig.text("shop-disabled"));
        }

        if (npc.equals(event.getNPC()) && SpigotMoreFish.getInstance().isEnabled()) {
            if (enabled) {
                new FishShopGui(event.getClicker());
            }
            else {
                event.getClicker().sendMessage(langConfig.text("shop-disabled"));
            }
        }
    }
}
