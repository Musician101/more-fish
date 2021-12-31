package me.elsiff.morefish.fishing.catchhandler;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class AbstractBroadcaster implements CatchHandler {

    private String fishNameWithRarity(FishType fishType) {
        String s = fishType.getDisplayName();
        if (fishType.getNoDisplay()) {
            return s;
        }

        return fishType.getRarity().getDisplayName().toUpperCase() + " " + s;
    }

    @Nonnull
    protected abstract String getCatchMessageFormat();

    public void handle(@Nonnull Player catcher, @Nonnull Fish fish) {
        if (meetBroadcastCondition(catcher, fish)) {
            List<Player> receivers = fish.getType().getCatchAnnouncement().receiversOf(catcher);
            if (MoreFish.instance().getConfig().getBoolean("messages.only-announce-fishing-rod")) {
                receivers.removeIf(player -> player.getInventory().getItemInMainHand().getType() != Material.FISHING_ROD);
            }

            String msg = Lang.replace(getCatchMessageFormat(), Map.of("%player%", catcher.getName(), "%length%", fish.getLength(), "%rarity%", fish.getType().getRarity().getDisplayName().toUpperCase(), "%rarity_color%", fish.getType().getRarity().getColor(), "%fish%", fish.getType().getName(), "%fish_with_rarity%", fishNameWithRarity(fish.getType())), catcher);
            receivers.forEach(player -> player.sendMessage(msg));
        }
    }

    protected abstract boolean meetBroadcastCondition(@Nonnull Player var1, @Nonnull Fish var2);
}
