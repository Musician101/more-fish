package io.musician101.morefish.spigot.fishing.catchhandler;

import io.musician101.morefish.common.fishing.catchhandler.CatchBroadcaster;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class AbstractSpigotBroadcaster extends CatchBroadcaster<SpigotTextFormat, String> {

    private static Player getPlayer(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }

    @Nonnull
    @Override
    protected final String getCatcherName(@Nonnull UUID uuid) {
        return getPlayer(uuid).getName();
    }

    @Override
    protected final boolean hasFishingRod(@Nonnull UUID uuid) {
        return getPlayer(uuid).getInventory().getItemInMainHand().getType() != Material.FISHING_ROD;
    }

    @Override
    protected final boolean onlyAnnounceFishingRod() {
        return SpigotMoreFish.getInstance().getPluginConfig().getMessagesConfig().onlyAnnounceFishingRod();
    }

    @Override
    protected final void sendMessage(@Nonnull UUID uuid, @Nonnull String msg) {
        getPlayer(uuid).sendMessage(msg);
    }
}
