package me.elsiff.morefish.fishing.catchhandler;

import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static me.elsiff.morefish.text.Lang.replace;

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
            Component msg = replace(getCatchMessageFormat(), Map.of("%player%", catcher.getName(), "%length%", fish.length(), "%rarity%", fish.type().rarity().displayName().toUpperCase(), "%rarity_color%", fish.type().rarity().color(), "%fish%", fish.type().name(), "%fish_with_rarity%", fishNameWithRarity(fish.type())), catcher);
            receivers.forEach(player -> player.sendMessage(msg));
        }
    }

    protected abstract boolean meetBroadcastCondition(@NotNull Player var1, @NotNull Fish var2);
}
