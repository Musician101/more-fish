package me.elsiff.morefish.configuration.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.fishing.FishRarity;
import me.elsiff.morefish.fishing.catchhandler.CatchCommandExecutor;
import me.elsiff.morefish.fishing.catchhandler.CatchHandler;
import org.bukkit.configuration.ConfigurationSection;

public final class FishRaritySetLoader implements CustomLoader<Set<FishRarity>> {

    private final ChatColorLoader chatColorLoader;
    private final PlayerAnnouncementLoader playerAnnouncementLoader;

    public FishRaritySetLoader(@Nonnull ChatColorLoader chatColorLoader, @Nonnull PlayerAnnouncementLoader playerAnnouncementLoader) {
        this.chatColorLoader = chatColorLoader;
        this.playerAnnouncementLoader = playerAnnouncementLoader;
    }

    @Nonnull
    public Set<FishRarity> loadFrom(@Nonnull ConfigurationSection section, @Nonnull String path) {
        return section.getKeys(false).stream().map(section::getConfigurationSection).map(cs -> {
            List<CatchHandler> catchHandlers = new ArrayList<>();
            if (cs.contains("commands")) {
                catchHandlers.add(new CatchCommandExecutor(cs.getStringList("commands")));
            }

            return new FishRarity(cs.getName(), cs.getString("display-name"), cs.getBoolean("default", false), cs.getDouble("chance", 0D) / 100D, chatColorLoader.loadFrom(cs, "color"), catchHandlers, playerAnnouncementLoader.loadIfExists(cs, "catch-announce").orElse(Config.INSTANCE.getDefaultCatchAnnouncement()), cs.getBoolean("skip-item-format", false), cs.getBoolean("no-display", false), cs.getBoolean("firework", false), cs.getDouble("additional-price", 0D));
        }).collect(Collectors.toSet());
    }
}
