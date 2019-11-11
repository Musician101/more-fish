package me.elsiff.morefish.configuration;

import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;
import me.elsiff.morefish.announcement.PlayerAnnouncement;
import me.elsiff.morefish.configuration.loader.ChatColorLoader;
import me.elsiff.morefish.configuration.loader.CustomItemStackLoader;
import me.elsiff.morefish.configuration.loader.EnchantmentMapLoader;
import me.elsiff.morefish.configuration.loader.FishConditionSetLoader;
import me.elsiff.morefish.configuration.loader.FishRaritySetLoader;
import me.elsiff.morefish.configuration.loader.FishTypeMapLoader;
import me.elsiff.morefish.configuration.loader.LocalTimeListLoader;
import me.elsiff.morefish.configuration.loader.PlayerAnnouncementLoader;
import me.elsiff.morefish.configuration.loader.PrizeMapLoader;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public final class Config {

    public static final Config INSTANCE = new Config();
    @Nonnull
    private static final ChatColorLoader chatColorLoader = new ChatColorLoader();
    @Nonnull
    private static final EnchantmentMapLoader enchantmentMapLoader = new EnchantmentMapLoader();
    @Nonnull
    private static final CustomItemStackLoader customItemStackLoader = new CustomItemStackLoader(enchantmentMapLoader);
    @Nonnull
    private static final YamlConfiguration fish = new YamlConfiguration();
    @Nonnull
    private static final FishConditionSetLoader fishConditionSetLoader = new FishConditionSetLoader();
    @Nonnull
    private static final YamlConfiguration lang = new YamlConfiguration();
    @Nonnull
    private static final LocalTimeListLoader localTimeListLoader = new LocalTimeListLoader();
    @Nonnull
    private static final PlayerAnnouncementLoader playerAnnouncementLoader = new PlayerAnnouncementLoader();
    @Nonnull
    private static final FishRaritySetLoader fishRaritySetLoader = new FishRaritySetLoader(chatColorLoader, playerAnnouncementLoader);
    @Nonnull
    private static final FishTypeMapLoader fishTypeMapLoader = new FishTypeMapLoader(fishRaritySetLoader, customItemStackLoader, fishConditionSetLoader, playerAnnouncementLoader);
    @Nonnull
    private static final PrizeMapLoader prizeMapLoader = new PrizeMapLoader();
    @Nonnull
    private static final YamlConfiguration standard = new YamlConfiguration();
    private static final Map<YamlConfiguration, Integer> configurationVersionMap = ImmutableMap.of(standard, 300, fish, 300, lang, 211);

    private Config() {

    }

    @Nonnull
    public final ChatColorLoader getChatColorLoader() {
        return chatColorLoader;
    }

    @Nonnull
    public final CustomItemStackLoader getCustomItemStackLoader() {
        return customItemStackLoader;
    }

    @Nonnull
    public final PlayerAnnouncement getDefaultCatchAnnouncement() {
        return playerAnnouncementLoader.loadFrom(standard.getConfigurationSection("messages"), "announce-catch");
    }

    @Nonnull
    public final EnchantmentMapLoader getEnchantmentMapLoader() {
        return enchantmentMapLoader;
    }

    @Nonnull
    public final YamlConfiguration getFish() {
        return fish;
    }

    @Nonnull
    public final FishConditionSetLoader getFishConditionSetLoader() {
        return fishConditionSetLoader;
    }

    @Nonnull
    public final FishRaritySetLoader getFishRaritySetLoader() {
        return fishRaritySetLoader;
    }

    @Nonnull
    public final FishTypeMapLoader getFishTypeMapLoader() {
        return fishTypeMapLoader;
    }

    @Nonnull
    public final YamlConfiguration getLang() {
        return lang;
    }

    @Nonnull
    public final LocalTimeListLoader getLocalTimeListLoader() {
        return localTimeListLoader;
    }

    @Nonnull
    public final PlayerAnnouncement getNewFirstAnnouncement() {
        return playerAnnouncementLoader.loadFrom(standard.getConfigurationSection("messages"), "announce-new-1st");
    }

    @Nonnull
    public final PlayerAnnouncementLoader getPlayerAnnouncementLoader() {
        return playerAnnouncementLoader;
    }

    @Nonnull
    public final PrizeMapLoader getPrizeMapLoader() {
        return prizeMapLoader;
    }

    @Nonnull
    public final YamlConfiguration getStandard() {
        return standard;
    }

    public final void load(@Nonnull Plugin plugin) {
        String locale = standard.getString("general.locale");
        standard.set("file-name", "config.yml");
        String fishFile = "fish_" + locale + ".yml";
        try {
            fish.load(new File(plugin.getDataFolder(), fishFile));
            fish.set("file-name", fishFile);
        }
        catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Failed to load " + fishFile);
        }

        String langFile = "lang_" + locale + ".yml";
        try {
            lang.load(new File(plugin.getDataFolder(), langFile));
            standard.set("file-name", langFile);
        }
        catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Failed to load " + fishFile);
        }

        configurationVersionMap.forEach((yaml, requiredVersion) -> {
            if (yaml.getInt("version") < requiredVersion) {
                String msg = Lang.INSTANCE.format("old-file").replace(Collections.singletonList(new SimpleEntry<>("%s", yaml.getString("file-name")))).output();
                plugin.getServer().getConsoleSender().sendMessage(msg);
            }
        });
    }
}
