package me.elsiff.morefish.fishing.catchhandler;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static me.elsiff.morefish.MoreFish.getPlugin;

public abstract class AbstractBroadcaster implements CatchHandler {

    private String fishNameWithRarity(FishType fishType) {
        String s = fishType.displayName();
        if (fishType.noDisplay()) {
            return s;
        }

        return fishType.rarity().displayName().toUpperCase() + " " + s;
    }

    @Nonnull
    protected abstract Component getCatchMessageFormat();

    public void handle(@Nonnull Player catcher, @Nonnull Fish fish) {
        if (meetBroadcastCondition(catcher, fish)) {
            List<Player> receivers = fish.type().catchAnnouncement().receiversOf(catcher);
            if (getPlugin().getConfig().getBoolean("messages.only-announce-fishing-rod")) {
                receivers.removeIf(player -> player.getInventory().getItemInMainHand().getType() != Material.FISHING_ROD);
            }

            Component format = getCatchMessageFormat();
            Component msg = Lang.replace(format, Map.of("%player%", catcher.getName(), "%length%", fish.length(), "%rarity%", fish.type().rarity().displayName().toUpperCase(), "%rarity_color%", fish.type().rarity().color(), "%fish%", fish.type().name(), "%fish_with_rarity%", fishNameWithRarity(fish.type())), catcher);
            receivers.forEach(player -> player.sendMessage(msg));
        }
    }

    protected abstract boolean meetBroadcastCondition(@Nonnull Player var1, @Nonnull Fish var2);
}
