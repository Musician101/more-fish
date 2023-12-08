package me.elsiff.morefish.paper.fishing.catchhandler;

import java.util.List;
import java.util.Map;
import me.elsiff.morefish.common.fishing.catchhandler.AbstractBroadcaster;
import me.elsiff.morefish.paper.fishing.competition.PaperFishingCompetition;
import me.elsiff.morefish.paper.fishing.PaperFish;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;
import static me.elsiff.morefish.paper.configuration.PaperLang.lang;

public interface PaperBroadcaster extends AbstractBroadcaster<PaperFishingCompetition, PaperFish, Player> {

    @Override
    default void handle(@NotNull Player catcher, @NotNull PaperFish fish) {
        if (meetBroadcastCondition(catcher, fish)) {
            List<Player> receivers = fish.type().catchAnnouncement().receiversOf(catcher);
            if (getPlugin().getConfig().getBoolean("messages.only-announce-fishing-rod")) {
                receivers.removeIf(player -> player.getInventory().getItemInMainHand().getType() != Material.FISHING_ROD);
            }

            Component format = getCatchMessageFormat();
            Component msg = lang().replace(format, Map.of("%player%", catcher.getName(), "%length%", fish.length(), "%rarity%", fish.type().rarity().displayName().toUpperCase(), "%rarity_color%", fish.type().rarity().color(), "%fish%", fish.type().name(), "%fish_with_rarity%", fishNameWithRarity(fish.type())), catcher.getUniqueId());
            receivers.forEach(player -> player.sendMessage(msg));
        }
    }
}
