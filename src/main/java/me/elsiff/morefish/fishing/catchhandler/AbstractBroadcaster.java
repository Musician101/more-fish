package me.elsiff.morefish.fishing.catchhandler;

import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.elsiff.morefish.text.Lang.fishLength;
import static me.elsiff.morefish.text.Lang.fishName;
import static me.elsiff.morefish.text.Lang.fishRarity;
import static me.elsiff.morefish.text.Lang.fishRarityColor;
import static me.elsiff.morefish.text.Lang.playerName;
import static me.elsiff.morefish.text.Lang.replace;
import static me.elsiff.morefish.text.Lang.tagResolver;

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
            List<TagResolver> tagResolvers = List.of(playerName(catcher), fishLength(fish), fishRarity(fish), fishRarityColor(fish), fishName(fish), tagResolver("fish_with_rarity", fishNameWithRarity(fish.type())));
            Component msg = replace(getCatchMessageFormat(), tagResolvers, catcher);
            receivers.forEach(player -> player.sendMessage(msg));
        }
    }

    protected abstract boolean meetBroadcastCondition(@NotNull Player var1, @NotNull Fish var2);
}
