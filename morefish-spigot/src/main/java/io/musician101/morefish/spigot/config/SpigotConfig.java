package io.musician101.morefish.spigot.config;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.ConfigurateLoader;
import io.musician101.morefish.common.Reference;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.fishing.Fish;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.common.hooker.PluginHooker;
import io.musician101.morefish.spigot.SpigotMoreFish;
import io.musician101.morefish.spigot.announcement.SpigotPlayerAnnouncement;
import io.musician101.morefish.spigot.config.format.SpigotTextFormat;
import io.musician101.morefish.spigot.config.format.SpigotTextListFormat;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchCommandExecutor;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchFireworkSpawner;
import io.musician101.morefish.spigot.fishing.catchhandler.SpigotCatchHandler;
import io.musician101.morefish.spigot.fishing.competition.SpigotPrize;
import io.musician101.morefish.spigot.fishing.condition.SpigotFishCondition;
import io.musician101.morefish.spigot.hooker.SpigotProtocolLibHooker;
import io.musician101.morefish.spigot.hooker.SpigotVaultHooker;
import io.musician101.morefish.spigot.shop.FishShopGui;
import java.io.File;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.milkbowl.vault.economy.Economy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.Types;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

@SuppressWarnings("ConstantConditions")
public final class SpigotConfig {

    private SpigotConfig() {

    }

    public static Config<FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>>, LangConfig<SpigotTextFormat, SpigotTextListFormat, String>, MessagesConfig<SpigotPlayerAnnouncement, BarColor>, FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String>, SpigotPrize> defaultConfig() {
        return new Config<>(new File(SpigotMoreFish.getInstance().getDataFolder(), "config.yml"), () -> SpigotMoreFish.getInstance().saveDefaultConfig(), defaultFishShopConfig(), defaultMessagesConfig(), defaultFishConfig(), defaultLangConfig(), ConfigurateLoader.YAML, SpigotPrize::new, (current, latest) -> {
            if (current < latest) {
                SpigotMoreFish plugin = SpigotMoreFish.getInstance();
                String msg = plugin.getPluginConfig().getLangConfig().format("old-file").replace(Collections.singletonMap("%s", "config.yml")).output();
                plugin.getLogger().warning(msg);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private static FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>> defaultFishConfig() {
        return new FishConfig<>(new File("config/" + Reference.ID + "/locale/fish_" + SpigotMoreFish.getInstance().getPluginConfig().getLocale() + ".yml"), () -> {
            SpigotMoreFish plugin = SpigotMoreFish.getInstance();
            String path = "locale/fish_" + plugin.getPluginConfig().getLocale() + ".yml";
            plugin.saveResource(path, false);
            return new File(plugin.getDataFolder(), path);
        }, ConfigurateLoader.YAML, rarities -> rarities.entrySet().stream().map(e -> {
            ConfigurationNode cn = e.getValue();
            List<SpigotCatchHandler> catchHandlers = new ArrayList<>();
            if (cn.getNode("commands").isVirtual()) {
                catchHandlers.add(new SpigotCatchCommandExecutor(cn.getNode("commands").getList(Object::toString)));
            }
            else if (cn.getNode("firework").getBoolean(false)) {
                catchHandlers.add(new SpigotCatchFireworkSpawner());
            }

            String name = e.getKey().toString();
            String displayName = cn.getString("display-name");
            boolean isDefault = cn.getNode("default").getBoolean(false);
            double chance = cn.getNode("chance").getDouble(0) / 100;
            ChatColor color = ChatColor.valueOf(cn.getNode("color").getString().toUpperCase());
            Config<FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>>, LangConfig<SpigotTextFormat, SpigotTextListFormat, String>, MessagesConfig<SpigotPlayerAnnouncement, BarColor>, FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String>, SpigotPrize> config = SpigotMoreFish.getInstance().getPluginConfig();
            SpigotPlayerAnnouncement playerAnnouncement = config.getMessagesConfig().getAnnounceCatch();
            boolean skipItemFormat = cn.getNode("skip-item-format").getBoolean(false);
            boolean noDisplay = cn.getNode("no-display").getBoolean(false);
            boolean firework = cn.getNode("firework").getBoolean(false);
            double additionalPrice = cn.getNode("additional-price").getDouble(0D);
            return new FishRarity<>(name, displayName, isDefault, chance, color, catchHandlers, playerAnnouncement, skipItemFormat, noDisplay, firework, additionalPrice);
        }).collect(Collectors.toList()), (rarity, e) -> {
            ConfigurationNode type = e.getValue();
            List<SpigotCatchHandler> catchHandlers = new ArrayList<>(rarity.getCatchHandlers());
            ConfigurationNode commands = type.getNode("commands");
            if (commands.isVirtual()) {
                catchHandlers.add(new SpigotCatchCommandExecutor(commands.getList(Object::toString)));
            }
            else if (type.getNode("firework").getBoolean(false)) {
                catchHandlers.add(new SpigotCatchFireworkSpawner());
            }

            String name = e.getKey().toString();
            String displayName = type.getNode("display-name").getString();
            double minLength = type.getNode("length-min").getDouble();
            double maxLength = type.getNode("length-max").getDouble();
            ConfigurationNode iconCN = type.getNode("icon");
            String itemID = iconCN.getNode("id").getString();
            Material itemType = Material.matchMaterial(itemID);
            int amount = iconCN.getNode("amount").getInt(1);
            List<String> lore = iconCN.getNode("lore").getList(o -> ChatColor.translateAlternateColorCodes('&', Types.asString(o)));
            Map<Enchantment, Integer> enchantments = iconCN.getList(o -> {
                String[] tokens = Types.asString(o).split("\\|");
                String[] namespaceKey = tokens[0].split(":");
                return new SimpleEntry<>(Enchantment.getByKey(new NamespacedKey(namespaceKey[0], namespaceKey[1])), Integer.parseInt(tokens[1]));
            }).stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            ItemStack icon = new ItemStack(itemType, amount);
            icon.addUnsafeEnchantments(enchantments);
            ItemMeta meta = icon.getItemMeta();
            meta.setLore(lore);
            meta.setUnbreakable(iconCN.getNode("unbreakable").getBoolean(false));
            if (meta instanceof Damageable) {
                ((Damageable) meta).setDamage(iconCN.getNode("durability").getInt(0));
            }
            ConfigurationNode skullUUID = iconCN.getNode("skull-uuid");
            if (!skullUUID.isVirtual() && meta instanceof SkullMeta) {
                ((SkullMeta) meta).setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(skullUUID.getString())));
            }

            icon.setItemMeta(meta);
            SpigotProtocolLibHooker protocolLib = SpigotMoreFish.getInstance().getProtocolLib();
            ConfigurationNode skullTexture = iconCN.getNode("skull-texture");
            if (!skullTexture.isVirtual()) {
                PluginHooker.checkHooked(protocolLib);
                icon = protocolLib.writeTexture(icon, skullTexture.getString());
            }

            SpigotPlayerAnnouncement playerAnnouncement = type.getNode("catch-announce").getValue(o -> {
                double configuredValue = Types.asDouble(o);
                switch ((int) configuredValue) {
                    case -2:
                        return SpigotPlayerAnnouncement.empty();
                    case -1:
                        return SpigotPlayerAnnouncement.serverBroadcast();
                    case 0:
                        return SpigotPlayerAnnouncement.base();
                    default:
                        return SpigotPlayerAnnouncement.ranged(configuredValue);
                }
            }, rarity.getCatchAnnouncement());
            List<SpigotFishCondition> conditions = type.getNode("conditions").getList(o -> {
                List<String> tokens = new ArrayList<>(Arrays.asList(Types.asString(o).split("\\|")));
                String id = tokens.get(0);
                tokens.remove(0);
                String[] args = tokens.toArray(new String[0]);
                return SpigotMoreFish.getInstance().getFishConditionManager().getFishCondition(id, args).orElseThrow(() -> new IllegalStateException("There's no fish condition whose id is " + id));
            });
            boolean skipItemFormat = type.getNode("skip-item-format").getBoolean(rarity.hasNotFishItemFormat());
            boolean noDisplay = type.getNode("no-display").getBoolean(rarity.getNoDisplay());
            boolean firework = type.getNode("firework").getBoolean(rarity.hasCatchFirework());
            double additionalPrice = rarity.getAdditionalPrice() + type.getNode("additional-price").getDouble(0);
            FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack> fishType = new FishType<>(name, rarity, displayName, minLength, maxLength, icon, catchHandlers, playerAnnouncement, conditions, skipItemFormat, noDisplay, firework, additionalPrice);
            return new SimpleEntry<>(rarity, fishType);
        }, (current, latest) -> {
            SpigotMoreFish plugin = SpigotMoreFish.getInstance();
            Logger logger = plugin.getLogger();
            Config<FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>>, LangConfig<SpigotTextFormat, SpigotTextListFormat, String>, MessagesConfig<SpigotPlayerAnnouncement, BarColor>, FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String>, SpigotPrize> config = plugin.getPluginConfig();
            logger.info("Loaded " + config.getFishConfig().getRarities().size() + " rarities and " + config.getFishConfig().getTypes().size() + " fish types");
            if (current < latest) {
                String msg = config.getLangConfig().format("old-file").replace(ImmutableMap.of("%s", "locale/fish_" + config.getLocale() + ".yml")).output();
                plugin.getLogger().warning(msg);
            }
        });
    }

    private static FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String> defaultFishShopConfig() {
        return new FishShopConfig<>("[FishShop]", ChatColor.AQUA + "" + ChatColor.BOLD + "[FishShop]", o -> ChatColor.translateAlternateColorCodes('$', Types.asString(o)), FishShopGui::new, (player, fish) -> {
            SpigotVaultHooker vault = SpigotMoreFish.getInstance().getVault();
            if (!vault.hasHooked()) {
                throw new IllegalStateException("Vault must be hooked for fish shop feature");
            }

            if (!vault.hasEconomy()) {
                throw new IllegalStateException("Vault doesn't have economy plugin");
            }

            Economy economy = vault.getEconomy();
            if (economy == null || !economy.isEnabled()) {
                throw new IllegalStateException("Economy must be enabled");
            }

            economy.depositPlayer(player, fish.stream().mapToDouble(SpigotMoreFish.getInstance().getPluginConfig().getFishShopConfig()::priceOf).sum());
        });
    }

    private static LangConfig<SpigotTextFormat, SpigotTextListFormat, String> defaultLangConfig() {
        return new LangConfig<>(new File(SpigotMoreFish.getInstance().getDataFolder(), "locale/lang_" + SpigotMoreFish.getInstance().getPluginConfig().getLocale() + ".yml"), () -> {
            SpigotMoreFish plugin = SpigotMoreFish.getInstance();
            String path = "locale/lang_" + plugin.getPluginConfig().getLocale() + ".yml";
            plugin.saveResource(path, false);
            return new File(plugin.getDataFolder(), path);
        }, SimpleConfigurationNode.root(), ConfigurateLoader.YAML, SpigotTextFormat::new, SpigotTextListFormat::new, s -> ChatColor.translateAlternateColorCodes('&', s), second -> {
            LangConfig<SpigotTextFormat, SpigotTextListFormat, String> langConfig = SpigotMoreFish.getInstance().getPluginConfig().getLangConfig();
            StringBuilder builder = new StringBuilder();
            Duration duration = Duration.ofSeconds(second);
            if (duration.toMinutes() > 0L) {
                builder.append(duration.toMinutes()).append(langConfig.text("time-format-minutes")).append(" ");
            }

            builder.append(duration.getSeconds() % (long) 60).append(langConfig.text("time-format-seconds"));
            return builder.toString();
        }, (current, latest) -> {
            SpigotMoreFish plugin = SpigotMoreFish.getInstance();
            Config<FishConfig<SpigotFishCondition, Item, Player, FishRarity<SpigotPlayerAnnouncement, ChatColor, SpigotCatchHandler>, FishType<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>>, LangConfig<SpigotTextFormat, SpigotTextListFormat, String>, MessagesConfig<SpigotPlayerAnnouncement, BarColor>, FishShopConfig<Fish<SpigotPlayerAnnouncement, ChatColor, SpigotFishCondition, SpigotCatchHandler, ItemStack>, Player, String>, SpigotPrize> config = SpigotMoreFish.getInstance().getPluginConfig();
            if (current < latest) {
                String msg = config.getLangConfig().format("old-file").replace(ImmutableMap.of("%s", "locale/fish_" + config.getLocale() + ".yml")).output();
                plugin.getLogger().warning(msg);
            }
        });
    }

    private static MessagesConfig<SpigotPlayerAnnouncement, BarColor> defaultMessagesConfig() {
        return new MessagesConfig<>(SpigotPlayerAnnouncement.serverBroadcast(), SpigotPlayerAnnouncement.serverBroadcast(), BarColor.BLUE, o -> {
            double configuredValue = Types.asDouble(o);
            switch ((int) configuredValue) {
                case -2:
                    return SpigotPlayerAnnouncement.empty();
                case -1:
                    return SpigotPlayerAnnouncement.serverBroadcast();
                case 0:
                    return SpigotPlayerAnnouncement.base();
                default:
                    return SpigotPlayerAnnouncement.ranged(configuredValue);
            }
        }, o -> BarColor.valueOf(Types.asString(o)));
    }
}
