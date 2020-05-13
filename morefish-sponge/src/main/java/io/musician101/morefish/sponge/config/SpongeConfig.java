package io.musician101.morefish.sponge.config;

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
import io.musician101.morefish.sponge.SpongeMoreFish;
import io.musician101.morefish.sponge.announcement.SpongePlayerAnnouncement;
import io.musician101.morefish.sponge.config.format.SpongeTextFormat;
import io.musician101.morefish.sponge.config.format.SpongeTextListFormat;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchCommandExecutor;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchFireworkSpawner;
import io.musician101.morefish.sponge.fishing.catchhandler.SpongeCatchHandler;
import io.musician101.morefish.sponge.fishing.competition.SpongePrize;
import io.musician101.morefish.sponge.fishing.condition.SpongeFishCondition;
import io.musician101.morefish.sponge.hooker.SpongeEconomyHooker;
import io.musician101.morefish.sponge.shop.FishShopGui;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.Types;
import org.slf4j.Logger;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

@SuppressWarnings("ConstantConditions")
public final class SpongeConfig {

    private SpongeConfig() {

    }

    public static Config<FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>>, LangConfig<SpongeTextFormat, SpongeTextListFormat, Text>, MessagesConfig<SpongePlayerAnnouncement, BossBarColor>, FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text>, SpongePrize> defaultConfig() {
        return new Config<>(new File("config/" + Reference.ID + "/config.conf"), () -> SpongeMoreFish.getInstance().getPluginContainer().getAsset("config.conf").ifPresent(asset -> {
            try {
                asset.copyToDirectory(Paths.get("config", Reference.ID));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }), defaultFishShopConfig(), defaultMessagesConfig(), defaultFishConfig(), defaultLangConfig(), ConfigurateLoader.HOCON, SpongePrize::new, (current, latest) -> {
            if (current < latest) {
                SpongeMoreFish plugin = SpongeMoreFish.getInstance();
                Text msg = plugin.getConfig().getLangConfig().format("old-file").replace(Collections.singletonMap("%s", "config.conf")).output();
                plugin.getLogger().warn(msg.toPlain());
            }
        });
    }

    private static FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>> defaultFishConfig() {
        return new FishConfig<>(new File("config/" + Reference.ID + "/locale/fish_" + SpongeMoreFish.getInstance().getConfig().getLocale() + ".conf"), () -> {
            SpongeMoreFish plugin = SpongeMoreFish.getInstance();
            String locale = plugin.getConfig().getLocale();
            String path = "locale/fish_" + locale + ".conf";
            Asset asset = plugin.getPluginContainer().getAsset(path).orElseThrow(() -> new IllegalStateException(locale + " is not a supported locale."));
            try {
                asset.copyToDirectory(Paths.get("config", Reference.ID, "locale"));
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return new File("config/" + Reference.ID, path);
        }, ConfigurateLoader.HOCON, rarities -> rarities.entrySet().stream().map(e -> {
            ConfigurationNode cn = e.getValue();
            List<SpongeCatchHandler> catchHandlers = new ArrayList<>();
            if (cn.getNode("commands").isVirtual()) {
                catchHandlers.add(new SpongeCatchCommandExecutor(cn.getNode("commands").getList(Object::toString)));
            }
            else if (cn.getNode("firework").getBoolean(false)) {
                catchHandlers.add(new SpongeCatchFireworkSpawner());
            }

            String name = e.getKey().toString();
            String displayName = cn.getString("display-name");
            boolean isDefault = cn.getNode("default").getBoolean(false);
            double chance = cn.getNode("chance").getDouble(0) / 100;
            TextColor color = Sponge.getRegistry().getType(TextColor.class, cn.getNode("color").getString().toUpperCase()).orElseThrow(() -> new IllegalArgumentException("Invalid color for " + name));
            Config<FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>>, LangConfig<SpongeTextFormat, SpongeTextListFormat, Text>, MessagesConfig<SpongePlayerAnnouncement, BossBarColor>, FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text>, SpongePrize> config = SpongeMoreFish.getInstance().getConfig();
            SpongePlayerAnnouncement playerAnnouncement = config.getMessagesConfig().getAnnounceCatch();
            boolean skipItemFormat = cn.getNode("skip-item-format").getBoolean(false);
            boolean noDisplay = cn.getNode("no-display").getBoolean(false);
            boolean firework = cn.getNode("firework").getBoolean(false);
            double additionalPrice = cn.getNode("additional-price").getDouble(0D);
            return new FishRarity<>(name, displayName, isDefault, chance, color, catchHandlers, playerAnnouncement, skipItemFormat, noDisplay, firework, additionalPrice);
        }).collect(Collectors.toList()), (rarity, e) -> {
            ConfigurationNode type = e.getValue();
            List<SpongeCatchHandler> catchHandlers = new ArrayList<>(rarity.getCatchHandlers());
            ConfigurationNode commands = type.getNode("commands");
            if (commands.isVirtual()) {
                catchHandlers.add(new SpongeCatchCommandExecutor(commands.getList(Object::toString)));
            }
            else if (type.getNode("firework").getBoolean(false)) {
                catchHandlers.add(new SpongeCatchFireworkSpawner());
            }

            String name = e.getKey().toString();
            String displayName = type.getNode("display-name").getString();
            double minLength = type.getNode("length-min").getDouble();
            double maxLength = type.getNode("length-max").getDouble();
            ConfigurationNode iconCN = type.getNode("icon");
            String itemID = iconCN.getNode("id").getString();
            GameRegistry registry = Sponge.getRegistry();
            ItemType itemType = registry.getType(ItemType.class, itemID).orElseThrow(() -> new IllegalArgumentException(itemID + " is not a valid item ID."));
            int amount = iconCN.getNode("amount").getInt(1);
            List<Text> lore = iconCN.getNode("lore").getList(o -> TextSerializers.FORMATTING_CODE.deserialize(Types.asString(o)));
            List<Enchantment> enchantments = iconCN.getList(o -> {
                String[] tokens = Types.asString(o).split("\\|");
                return Enchantment.of(registry.getType(EnchantmentType.class, tokens[0]).orElseThrow(() -> new IllegalArgumentException(tokens[0] + " is not a valid enchantment ID.")), Integer.parseInt(tokens[1]));
            });

            ItemStack.Builder icon = ItemStack.builder().itemType(itemType).quantity(amount).add(Keys.ITEM_LORE, lore).add(Keys.ITEM_ENCHANTMENTS, enchantments).add(Keys.UNBREAKABLE, iconCN.getNode("unbreakable").getBoolean(false)).add(Keys.ITEM_DURABILITY, iconCN.getNode("durability").getInt(0));
            ConfigurationNode skullUUID = iconCN.getNode("skull-uuid");
            if (!skullUUID.isVirtual()) {
                try {
                    icon.add(Keys.REPRESENTED_PLAYER, Sponge.getServer().getGameProfileManager().get(UUID.fromString(skullUUID.getString()), false).get());
                }
                catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                }
            }

            SpongePlayerAnnouncement playerAnnouncement = type.getNode("catch-announce").getValue(o -> {
                double configuredValue = Types.asDouble(o);
                switch ((int) configuredValue) {
                    case -2:
                        return SpongePlayerAnnouncement.empty();
                    case -1:
                        return SpongePlayerAnnouncement.serverBroadcast();
                    case 0:
                        return SpongePlayerAnnouncement.base();
                    default:
                        return SpongePlayerAnnouncement.ranged(configuredValue);
                }
            }, rarity.getCatchAnnouncement());
            List<SpongeFishCondition> conditions = type.getNode("conditions").getList(o -> {
                List<String> tokens = new ArrayList<>(Arrays.asList(Types.asString(o).split("\\|")));
                String id = tokens.get(0);
                tokens.remove(0);
                String[] args = tokens.toArray(new String[0]);
                return SpongeMoreFish.getInstance().getFishConditionManager().getFishCondition(id, args).orElseThrow(() -> new IllegalStateException("There's no fish condition whose id is " + id));
            });
            boolean skipItemFormat = type.getNode("skip-item-format").getBoolean(rarity.hasNotFishItemFormat());
            boolean noDisplay = type.getNode("no-display").getBoolean(rarity.getNoDisplay());
            boolean firework = type.getNode("firework").getBoolean(rarity.hasCatchFirework());
            double additionalPrice = rarity.getAdditionalPrice() + type.getNode("additional-price").getDouble(0);
            FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack> fishType = new FishType<>(name, rarity, displayName, minLength, maxLength, icon.build(), catchHandlers, playerAnnouncement, conditions, skipItemFormat, noDisplay, firework, additionalPrice);
            return new SimpleEntry<>(rarity, fishType);
        }, (current, latest) -> {
            SpongeMoreFish plugin = SpongeMoreFish.getInstance();
            Logger logger = plugin.getLogger();
            Config<FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>>, LangConfig<SpongeTextFormat, SpongeTextListFormat, Text>, MessagesConfig<SpongePlayerAnnouncement, BossBarColor>, FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text>, SpongePrize> config = plugin.getConfig();
            logger.info("Loaded " + config.getFishConfig().getRarities().size() + " rarities and " + config.getFishConfig().getTypes().size() + " fish types");
            if (current < latest) {
                Text msg = config.getLangConfig().format("old-file").replace(ImmutableMap.of("%s", "locale/fish_" + config.getLocale() + ".conf")).output();
                plugin.getLogger().warn(msg.toPlain());
            }
        });
    }

    private static FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text> defaultFishShopConfig() {
        return new FishShopConfig<>(Text.of("[FishShop]"), Text.of(TextColors.AQUA, TextStyles.BOLD, Text.of("[FishShop]")), o -> TextSerializers.FORMATTING_CODE.deserialize(Types.asString(o)), FishShopGui::new, (player, fish) -> {
            SpongeEconomyHooker economyHooker = SpongeMoreFish.getInstance().getEconomy();
            if (!economyHooker.hasHooked()) {
                throw new IllegalStateException("Vault must be hooked for fish shop feature");
            }

            if (!economyHooker.hasEconomy()) {
                throw new IllegalStateException("Vault doesn't have economy plugin");
            }

            EconomyService economy = economyHooker.getEconomy();
            economy.getOrCreateAccount(player.getUniqueId()).ifPresent(account -> account.deposit(economy.getDefaultCurrency(), BigDecimal.valueOf(fish.stream().mapToDouble(SpongeMoreFish.getInstance().getConfig().getFishShopConfig()::priceOf).sum()), Cause.of(EventContext.builder().add(EventContextKeys.PLAYER, player).add(EventContextKeys.PLUGIN, SpongeMoreFish.getInstance().getPluginContainer()).build(), player)));
        });
    }

    private static LangConfig<SpongeTextFormat, SpongeTextListFormat, Text> defaultLangConfig() {
        return new LangConfig<>(new File("config", Reference.ID + "/locale/lang_" + SpongeMoreFish.getInstance().getConfig().getLocale() + ".conf"), () -> {
            SpongeMoreFish plugin = SpongeMoreFish.getInstance();
            String locale = plugin.getConfig().getLocale();
            String path = "locale/lang_" + locale + ".conf";
            Asset asset = plugin.getPluginContainer().getAsset(path).orElseThrow(() -> new IllegalStateException(locale + " is not a supported locale."));
            try {
                asset.copyToDirectory(Paths.get("config", Reference.ID, "locale"));
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return new File("config/" + Reference.ID, path);
        }, SimpleConfigurationNode.root(), ConfigurateLoader.HOCON, SpongeTextFormat::new, SpongeTextListFormat::new, TextSerializers.FORMATTING_CODE::deserialize, second -> {
            LangConfig<SpongeTextFormat, SpongeTextListFormat, Text> langConfig = SpongeMoreFish.getInstance().getConfig().getLangConfig();
            Text.Builder builder = Text.builder();
            Duration duration = Duration.ofSeconds(second);
            if (duration.toMinutes() > 0L) {
                builder.append(Text.of(duration.toMinutes())).append(langConfig.text("time-format-minutes")).append(Text.of(" "));
            }

            builder.append(Text.of(duration.getSeconds() % (long) 60)).append(langConfig.text("time-format-seconds"));
            return builder.build();
        }, (current, latest) -> {
            SpongeMoreFish plugin = SpongeMoreFish.getInstance();
            Logger logger = plugin.getLogger();
            Config<FishConfig<SpongeFishCondition, Item, Player, FishRarity<SpongePlayerAnnouncement, TextColor, SpongeCatchHandler>, FishType<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>>, LangConfig<SpongeTextFormat, SpongeTextListFormat, Text>, MessagesConfig<SpongePlayerAnnouncement, BossBarColor>, FishShopConfig<Fish<SpongePlayerAnnouncement, TextColor, SpongeFishCondition, SpongeCatchHandler, ItemStack>, Player, Text>, SpongePrize> config = plugin.getConfig();
            if (current < latest) {
                Text msg = config.getLangConfig().format("old-file").replace(ImmutableMap.of("%s", "locale/fish_" + config.getLocale() + ".conf")).output();
                logger.warn(msg.toPlain());
            }
        });
    }

    private static MessagesConfig<SpongePlayerAnnouncement, BossBarColor> defaultMessagesConfig() {
        return new MessagesConfig<>(SpongePlayerAnnouncement.serverBroadcast(), SpongePlayerAnnouncement.serverBroadcast(), BossBarColors.BLUE, o -> {
            double configuredValue = Types.asDouble(o);
            switch ((int) configuredValue) {
                case -2:
                    return SpongePlayerAnnouncement.empty();
                case -1:
                    return SpongePlayerAnnouncement.serverBroadcast();
                case 0:
                    return SpongePlayerAnnouncement.base();
                default:
                    return SpongePlayerAnnouncement.ranged(configuredValue);
            }
        }, o -> Sponge.getRegistry().getType(BossBarColor.class, Types.asString(o)).orElse(BossBarColors.BLUE));
    }
}
