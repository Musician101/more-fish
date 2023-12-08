package me.elsiff.morefish.sponge.fishing.catchhandler;

import java.util.List;
import java.util.Map;
import me.elsiff.morefish.common.fishing.catchhandler.AbstractBroadcaster;
import me.elsiff.morefish.sponge.fishing.SpongeFish;
import me.elsiff.morefish.sponge.fishing.competition.SpongeFishingCompetition;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.lang;

public interface SpongeBroadcaster extends AbstractBroadcaster<SpongeFishingCompetition, SpongeFish, ServerPlayer> {

    @Override
    default void handle(@NotNull ServerPlayer catcher, @NotNull SpongeFish fish) {
        if (meetBroadcastCondition(catcher, fish)) {
            List<ServerPlayer> receivers = fish.type().catchAnnouncement().receiversOf(catcher);
            if (getPlugin().getConfig().node("messages.only-announce-fishing-rod").getBoolean()) {
                receivers.removeIf(player -> {
                    ItemStack fishingRod = player.itemInHand(HandTypes.MAIN_HAND);
                    return !fishingRod.type().equals(ItemTypes.FISHING_ROD.get());
                });
            }

            Component format = getCatchMessageFormat();
            Component msg = lang().replace(format, Map.of("%player%", catcher.name(), "%length%", fish.length(), "%rarity%", fish.type().rarity().displayName().toUpperCase(), "%rarity_color%", fish.type().rarity().color(), "%fish%", fish.type().name(), "%fish_with_rarity%", fishNameWithRarity(fish.type())), catcher.uniqueId());
            receivers.forEach(player -> player.sendMessage(msg));
        }
    }
}
