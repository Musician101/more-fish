package me.elsiff.morefish.configuration;

import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
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
    private final ChatColorLoader chatColorLoader = new ChatColorLoader();
    @Nonnull
    private final EnchantmentMapLoader enchantmentMapLoader = new EnchantmentMapLoader();
    @Nonnull
    private final CustomItemStackLoader customItemStackLoader = new CustomItemStackLoader(enchantmentMapLoader);
    @Nonnull
    private final YamlConfiguration fish = new YamlConfiguration();
    @Nonnull
    private final FishConditionSetLoader fishConditionSetLoader = new FishConditionSetLoader();
    @Nonnull
    private final YamlConfiguration lang = new YamlConfiguration();
    @Nonnull
    private final LocalTimeListLoader localTimeListLoader = new LocalTimeListLoader();
    @Nonnull
    private final PlayerAnnouncementLoader playerAnnouncementLoader = new PlayerAnnouncementLoader();
    @Nonnull
    private final FishRaritySetLoader fishRaritySetLoader = new FishRaritySetLoader(chatColorLoader, playerAnnouncementLoader);
    @Nonnull
    private final FishTypeMapLoader fishTypeMapLoader = new FishTypeMapLoader(fishRaritySetLoader, customItemStackLoader, fishConditionSetLoader, playerAnnouncementLoader);
    @Nonnull
    private final PrizeMapLoader prizeMapLoader = new PrizeMapLoader();
    @Nonnull
    private final YamlConfiguration rewards = new YamlConfiguration();
    @Nonnull
    private YamlConfiguration standard = (YamlConfiguration) MoreFish.instance().getConfig();
    @Nonnull
    private final Map<YamlConfiguration, Integer> configurationVersionMap = ImmutableMap.of(standard, 300, fish, 300, lang, 211);

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
    public YamlConfiguration getRewards() {
        return rewards;
    }

    @Nonnull
    public final YamlConfiguration getStandard() {
        return standard;
    }

    public final void load(@Nonnull Plugin plugin) {
        plugin.reloadConfig();
        standard = (YamlConfiguration) plugin.getConfig();
        String locale = standard.getString("general.locale");
        standard.set("file-name", "config.yml");
        String fishFile = "locale/fish_" + locale + ".yml";
        try {
            plugin.saveResource(fishFile, false);
            fish.load(new File(plugin.getDataFolder(), fishFile));
            fish.set("file-name", fishFile);
        }
        catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Failed to load " + fishFile);
        }

        String langFile = "locale/lang_" + locale + ".yml";
        try {
            plugin.saveResource(langFile, false);
            lang.load(new File(plugin.getDataFolder(), langFile));
            standard.set("file-name", langFile);
        }
        catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Failed to load " + langFile);
        }

        configurationVersionMap.forEach((yaml, requiredVersion) -> {
            if (yaml.getInt("version") < requiredVersion) {
                String msg = Lang.INSTANCE.format("old-file").replace(ImmutableMap.of("%s", yaml.getString("file-name"))).output();
                plugin.getServer().getConsoleSender().sendMessage(msg);
            }
        });
    }
}
