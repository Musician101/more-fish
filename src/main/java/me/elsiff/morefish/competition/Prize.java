package me.elsiff.morefish.competition;

import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.NodePath;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
public record Prize(List<String> commands) {

    public void giveTo(OfflinePlayer player, Plugin plugin) {
        String name = player.getName();
        if (!player.isOnline()) {
            NodePath path = NodePath.path("main", "prize-warning");
            TagResolver resolver = TagResolverUtil.playerNameResolver(player);
            plugin.getComponentLogger().warn(lang().getComponent(path, resolver));
            return;
        }

        Server server = plugin.getServer();
        commands.forEach(command -> Bukkit.getGlobalRegionScheduler().run(getPlugin(), task -> server.dispatchCommand(server.getConsoleSender(), command.replace("@p", name == null ? "null" : name))));
    }
}
