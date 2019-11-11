package me.elsiff.morefish.shop;

import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.Lang;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;

@TraitName("fishshop")
public final class FishShopKeeperTrait extends Trait {

    private static FishShop fishShop;

    public FishShopKeeperTrait() {
        super("fishshop");
    }

    public static void init(@Nonnull FishShop fishShop) {
        FishShopKeeperTrait.fishShop = fishShop;
    }

    @EventHandler
    public final void onClickNpc(@Nonnull NPCRightClickEvent event) {
        if (event.getNPC() == npc && MoreFish.instance().isEnabled()) {
            if (Config.INSTANCE.getStandard().getBoolean("fish-shop.enable")) {
                fishShop.openGuiTo(event.getClicker());
            }
        }
        else {
            event.getClicker().sendMessage(Lang.INSTANCE.text("shop-disabled"));
        }

        if (npc.equals(event.getNPC()) && MoreFish.instance().isEnabled()) {
            if (Config.INSTANCE.getStandard().getBoolean("fish-shop")) {
                fishShop.openGuiTo(event.getClicker());
            }
            else {
                event.getClicker().sendMessage(Lang.INSTANCE.text("shop-disabled"));
            }
        }
    }
}
