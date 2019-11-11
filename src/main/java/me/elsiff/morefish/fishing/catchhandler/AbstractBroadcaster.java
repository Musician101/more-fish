package me.elsiff.morefish.fishing.catchhandler;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.configuration.format.TextFormat;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class AbstractBroadcaster implements CatchHandler {

    @Nonnull
    public abstract PlayerAnnouncement announcement(@Nonnull Fish var1);

    private final String fishNameWithRarity(FishType fishType) {
        if (fishType.getNoDisplay()) {
            return "";
        }

        return fishType.getRarity().getDisplayName().toUpperCase() + " " + fishType.getDisplayName();
    }

    @Nonnull
    public abstract TextFormat getCatchMessageFormat();

    public void handle(@Nonnull Player catcher, @Nonnull Fish fish) {
        if (meetBroadcastCondition(catcher, fish)) {
            List<Player> receivers = fish.getType().getCatchAnnouncement().receiversOf(catcher);
            if (Config.INSTANCE.getStandard().getBoolean("messages.only-announce-fishing-rod")) {
                receivers.removeIf(player -> player.getInventory().getItemInMainHand().getType() != Material.FISHING_ROD);
            }

            String msg = getCatchMessageFormat().replace(ImmutableMap.<String, Object>builder().put("%player%", catcher.getName()).put("%length%", fish.getLength()).put("%rarity%", fish.getType().getRarity().getDisplayName().toUpperCase()).put("%rarity_color", fish.getType().getRarity().getColor()).put("%fish%", fish.getType().getName()).put("%fish_with_rarity%", fishNameWithRarity(fish.getType())).build()).output(catcher);
            receivers.forEach(player -> player.sendMessage(msg));
        }
    }

    public abstract boolean meetBroadcastCondition(@Nonnull Player var1, @Nonnull Fish var2);
}
