package me.elsiff.morefish.text.tagresolver;

import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TopPlayerTagResolver extends TopTagResolver {

    @Nullable
    @Override
    public Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        return resolve(name, arguments, record -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(record.fisher());
            String playerName = offlinePlayer.getName();
            return playerName == null ? offlinePlayer.getUniqueId().toString() : playerName;
        });
    }

    @Override
    public boolean has(@NotNull String name) {
        return name.equals("top-player");
    }
}
