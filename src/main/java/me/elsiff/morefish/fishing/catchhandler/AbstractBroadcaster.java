package me.elsiff.morefish.fishing.catchhandler;

import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishType;
import me.elsiff.morefish.text.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractBroadcaster implements CatchHandler {

    private String fishNameWithRarity(FishType fishType) {
        String s = fishType.displayName();
        if (fishType.noDisplay()) {
            return s;
        }

        return fishType.rarity().displayName().toUpperCase() + " " + s;
    }

    @NotNull
    protected abstract String getCatchMessageFormat();

    public void handle(@NotNull Player catcher, @NotNull Fish fish) {
        if (meetBroadcastCondition(catcher, fish)) {
            List<Player> receivers = fish.type().catchAnnouncement().receiversOf(catcher);
            TagResolver tagResolver = TagResolver.resolver(Lang.playerName(catcher), Lang.fishLength(fish), Lang.fishRarity(fish), Lang.fishRarityColor(fish), Lang.fishName(fish), Lang.tagResolver("fish-with-rarity", fishNameWithRarity(fish.type())));
            Component msg = Lang.replace(getCatchMessageFormat(), tagResolver, catcher);
            receivers.forEach(player -> player.sendMessage(msg));
        }
    }

    protected abstract boolean meetBroadcastCondition(@NotNull Player catcher, @NotNull Fish fish);
}
