package io.musician101.morefish.forge.config;

import com.google.common.collect.ImmutableMap;
import io.musician101.morefish.common.Reference;
import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.config.FishConfig;
import io.musician101.morefish.common.config.FishShopConfig;
import io.musician101.morefish.common.config.LangConfig;
import io.musician101.morefish.common.config.MessagesConfig;
import io.musician101.morefish.common.config.format.TextFormat;
import io.musician101.morefish.common.config.format.TextListFormat;
import io.musician101.morefish.common.fishing.FishRarity;
import io.musician101.morefish.common.fishing.FishType;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import io.musician101.morefish.common.fishing.competition.FishingCompetition.State;
import io.musician101.morefish.common.fishing.competition.Prize;
import io.musician101.morefish.common.fishing.condition.FishCondition;
import io.musician101.morefish.forge.ForgeMoreFish;
import io.musician101.morefish.forge.announcement.ForgePlayerAnnouncement;
import io.musician101.morefish.forge.config.format.ForgeTextFormat;
import io.musician101.morefish.forge.fishing.catchhandler.ForgeCatchCommandExecutor;
import io.musician101.morefish.forge.fishing.catchhandler.ForgeCatchFireworkSpawner;
import io.musician101.morefish.forge.fishing.catchhandler.ForgeCatchHandler;
import io.musician101.morefish.forge.fishing.competition.ForgePrize;
import io.musician101.morefish.forge.fishing.condition.ForgeBiomeCondition;
import io.musician101.morefish.forge.fishing.condition.ForgeCompetitionCondition;
import io.musician101.morefish.forge.fishing.condition.ForgeEnchantmentCondition;
import io.musician101.morefish.forge.fishing.condition.ForgeFishCondition;
import io.musician101.morefish.forge.fishing.condition.ForgeLocationYCondition;
import io.musician101.morefish.forge.fishing.condition.ForgePotionEffectCondition;
import io.musician101.morefish.forge.fishing.condition.ForgeRainingCondition;
import io.musician101.morefish.forge.fishing.condition.ForgeThunderingCondition;
import io.musician101.morefish.forge.fishing.condition.ForgeTimeCondition;
import io.musician101.morefish.forge.fishing.condition.ForgeTimeCondition.TimeState;
import io.musician101.morefish.forge.fishing.condition.ForgeXPLevelCondition;
import io.musician101.morefish.forge.util.NumberUtils.DoubleRange;
import io.musician101.morefish.forge.util.TextParser;
import io.musician101.musicianlibrary.java.configurate.ConfigurateLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.BossInfo.Color;
import net.minecraftforge.registries.ForgeRegistries;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.Types;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.CallbackI.F;
import org.spongepowered.configurate.ConfigurationNode;

@SuppressWarnings("ConstantConditions")
public final class ForgeConfig {

    private ForgeConfig() {

    }

    public static Config defaultConfig() {
        return new Config<>(Paths.get("config", Reference.ID, "config.toml").toFile(), () -> {
            try {
                Path file = Paths.get("config", Reference.ID, "config.toml");
                if (!Files.exists(file)) {
                    Files.copy(ForgeMoreFish.class.getResourceAsStream("config.toml"), file);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }, defaultFishShopConfig(), defaultMessagesConfig(), defaultFishConfig(), defaultLangConfig(), ConfigurateLoader.YAML, ForgePrize::new, (current, latest) -> {
            if (current < latest) {
                ForgeMoreFish plugin = ForgeMoreFish.getInstance();
                ITextComponent msg = plugin.getPluginConfig().getLangConfig().format("old-file").replace(Collections.singletonMap("%s", "config.toml")).output();
                plugin.getLogger().warn(msg.getString());
            }
        });
    }

    private static FishConfig defaultFishConfig() {
        return new FishConfig(Paths.get("config", Reference.ID, "locale", "fish_" + ForgeMoreFish.getInstance().getPluginConfig().getLocale() + ".toml").toFile(), rarities -> rarities.entrySet().stream().map(e -> {
            ConfigurationNode cn = e.getValue();
            List<ForgeCatchHandler> catchHandlers = new ArrayList<>();
            if (cn.node("commands").empty()) {
                catchHandlers.add(new ForgeCatchCommandExecutor(cn.node("commands").getList(String.class, new ArrayList<>())));
            }
            else if (cn.node("firework").getBoolean(false)) {
                catchHandlers.add(new ForgeCatchFireworkSpawner());
            }

            String name = e.getKey().toString();
            String displayName = cn.getString("display-name");
            boolean isDefault = cn.node("default").getBoolean(false);
            double chance = cn.node("chance").getDouble(0) / 100;
            TextFormatting color = TextFormatting.valueOf(cn.node("color").getString().toUpperCase());
            Config config = ForgeMoreFish.getInstance().getPluginConfig();
            PlayerAnnouncement playerAnnouncement = config.getMessagesConfig().getAnnounceCatch();
            boolean skipItemFormat = cn.node("skip-item-format").getBoolean(false);
            boolean noDisplay = cn.node("no-display").getBoolean(false);
            boolean firework = cn.node("firework").getBoolean(false);
            double additionalPrice = cn.node("additional-price").getDouble(0D);
            return new FishRarity<>(name, displayName, isDefault, chance, color, catchHandlers, playerAnnouncement, skipItemFormat, noDisplay, firework, additionalPrice);
        }).collect(Collectors.toList()), (rarity, e) -> {
            ConfigurationNode type = e.getValue();
            List<CatchHandler> catchHandlers = new ArrayList<>(rarity.getCatchHandlers());
            ConfigurationNode commands = type.node("commands");
            if (commands.empty()) {
                catchHandlers.add(new ForgeCatchCommandExecutor(commands.getList(String.class, new ArrayList<>())));
            }
            else if (type.node("firework").getBoolean(false)) {
                catchHandlers.add(new ForgeCatchFireworkSpawner());
            }

            String name = e.getKey().toString();
            ITextComponent displayName = type.node("display-name").getString();
            double minLength = type.node("length-min").getDouble();
            double maxLength = type.node("length-max").getDouble();
            ConfigurationNode iconCN = type.node("icon");
            String itemID = iconCN.node("id").getString();
            Item itemType = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemID));
            int amount = iconCN.node("amount").getInt(1);
            List<String> lore = iconCN.node("lore").getList(o -> new TextParser(Types.asString(o)).getOutputAsString());
            Map<Enchantment, Integer> enchantments = iconCN.getList(Objects::toString).stream().map(s -> s.split("\\|")).map(tokens -> new SimpleEntry<>(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(tokens[0])), Integer.parseInt(tokens[1]))).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            ItemStack icon = new ItemStack(itemType, amount);
            enchantments.forEach(icon::addEnchantment);
            CompoundNBT tag = icon.getOrCreateTag();
            ListNBT list = new ListNBT();
            lore.stream().map(StringNBT::valueOf).forEach(list::add);
            tag.put("lore", list);
            tag.putBoolean("unbreakable", iconCN.node("unbreakable").getBoolean(false));
            icon.setDamage(iconCN.node("durability").getInt(0));
            ConfigurationNode skullUUID = iconCN.node("skull-uuid");
            if (!skullUUID.empty()) {
                CompoundNBT skullOwner = tag.getCompound("SkullOwner");
                skullOwner.putUniqueId("Id", UUID.fromString(skullUUID.getString()));
                tag.put("SkullOwner", skullOwner);
            }

            ConfigurationNode skullTexture = iconCN.node("skull-texture");
            if (!skullTexture.empty()) {
                CompoundNBT skullOwner = tag.getCompound("SkullOwner");
                CompoundNBT properties = skullOwner.getCompound("Properties");
                CompoundNBT compound = new CompoundNBT();
                compound.putString("Value", skullTexture.getString());
                ListNBT textures = new ListNBT();
                textures.add(compound);
                properties.put("textures", textures);
                skullOwner.putUniqueId("id", UUID.randomUUID());
                tag.put("SkullOwner", skullOwner);
            }

            icon.setTag(tag);
            ForgePlayerAnnouncement playerAnnouncement = type.node("catch-announce").getValue(o -> {
                double configuredValue = Types.asDouble(o);
                switch ((int) configuredValue) {
                    case -2:
                        return ForgePlayerAnnouncement.empty();
                    case -1:
                        return ForgePlayerAnnouncement.serverBroadcast();
                    case 0:
                        return ForgePlayerAnnouncement.base();
                    default:
                        return ForgePlayerAnnouncement.ranged(configuredValue);
                }
            }, rarity.getCatchAnnouncement());
            List<FishCondition> conditions = type.node("conditions").getList(o -> {
                List<String> tokens = new ArrayList<>(Arrays.asList(Types.asString(o).split("\\|")));
                String id = tokens.get(0);
                tokens.remove(0);
                String[] args = tokens.toArray(new String[0]);
                switch (id) {
                    case "raining":
                        return new ForgeRainingCondition(Boolean.parseBoolean(args[0]));
                    case "thundering":
                        return new ForgeThunderingCondition(Boolean.parseBoolean(args[0]));
                    case "time":
                        return new ForgeTimeCondition(TimeState.valueOf(args[0].toUpperCase()));
                    case "biome":
                        return new ForgeBiomeCondition(Stream.of(args).map(String::toUpperCase).map(ResourceLocation::new).map(ForgeRegistries.BIOMES::getValue).collect(Collectors.toSet()));
                    case "enchantment":
                        return new ForgeEnchantmentCondition(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(args[0])), Integer.parseInt(args[1]));
                    case "level":
                        return new ForgeXPLevelCondition(Integer.parseInt(args[0]));
                    case "contest":
                        return new ForgeCompetitionCondition(State.valueOf(args[0].toUpperCase()));
                    case "potion-effect":
                        return new ForgePotionEffectCondition(ForgeRegistries.POTIONS.getValue(new ResourceLocation(args[0])), Integer.parseInt(args[1]));
                    case "location-y":
                        return new ForgeLocationYCondition(new DoubleRange(Double.parseDouble(args[0]), Double.parseDouble(args[1])));
                    default:
                        throw new IllegalStateException("There's no fish condition whose id is " + id);
                }
            });
            boolean skipItemFormat = type.node("skip-item-format").getBoolean(rarity.hasNotFishItemFormat());
            boolean noDisplay = type.node("no-display").getBoolean(rarity.getNoDisplay());
            boolean firework = type.node("firework").getBoolean(rarity.hasCatchFirework());
            double additionalPrice = rarity.getAdditionalPrice() + type.node("additional-price").getDouble(0);
            FishType<ForgeFishCondition, ForgeCatchHandler, ItemStack> fishType = new FishType(name, rarity, displayName, minLength, maxLength, icon, catchHandlers, playerAnnouncement, conditions, skipItemFormat, noDisplay, firework);
            return new SimpleEntry<>(rarity, fishType);
        }, (current, latest) -> {
            ForgeMoreFish plugin = ForgeMoreFish.getInstance();
            Logger logger = plugin.getLogger();
            Config config = plugin.getPluginConfig();
            logger.info("Loaded " + config.getFishConfig().getRarities().size() + " rarities and " + config.getFishConfig().getTypes().size() + " fish types");
            if (current < latest) {
                ITextComponent msg = config.getLangConfig().format("old-file").replace(ImmutableMap.of("%s", "locale/fish_" + config.getLocale() + ".toml")).output();
                plugin.getLogger().warn(msg.getString());
            }
        });
    }

    private static Function<List<String>, Prize> defaultFishShopConfig() {
        return new FishShopConfig(new StringTextComponent("[FishShop]"), new StringTextComponent("[FishShop]").mergeStyles(TextFormatting.AQUA, TextFormatting.BOLD), o -> new TextParser(Types.asString(o)).getOutput(), p -> {
            //TODO need to send a packet that then opens a GUI here
        }, (player, fish) -> {
            //TODO replace with an items that reflect value
            player.sendMessage(new StringTextComponent("You sold " + fish.stream().mapToDouble(ForgeMoreFish.getInstance().getPluginConfig().getFishShopConfig()::priceOf).sum() + " worth of fish, but we can't actually give you anything for it :("));
        });
    }

    private static LangConfig<F, L, T> defaultLangConfig() {
        return new LangConfig<>(Paths.get("config", Reference.ID, "locale", "lang_" + ForgeMoreFish.getInstance().getPluginConfig().getLocale() + ".toml").toFile(), () -> {
            ForgeMoreFish plugin = ForgeMoreFish.getInstance();
            String path = "locale/lang_" + plugin.getPluginConfig().getLocale() + ".toml";
            Path file = Paths.get("config", Reference.ID, path);
            try {
                if (!Files.exists(file)) {
                    Files.copy(ForgeMoreFish.class.getResourceAsStream(path), file);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return file.toFile();
        }, SimpleConfigurationNode.root(), ConfigurateLoader.YAML, ForgeTextFormat::new, s -> new TextParser(s).getOutput(), second -> {
            LangConfig<TextFormat<?, ?>, TextListFormat<?, ?, ?>, Object> langConfig = ForgeMoreFish.getInstance().getPluginConfig().getLangConfig();
            ITextComponent msg = new StringTextComponent("");
            Duration duration = Duration.ofSeconds(second);
            if (duration.toMinutes() > 0L) {
                msg.append(new StringTextComponent(duration.toMinutes() + "")).append(langConfig.text("time-format-minutes")).append(new StringTextComponent(" "));
            }

            msg.append(new StringTextComponent((duration.getSeconds() % (long) 60) + "")).append(langConfig.text("time-format-seconds"));
            return msg;
        }, (current, latest) -> {
            ForgeMoreFish plugin = ForgeMoreFish.getInstance();
            Config config = ForgeMoreFish.getInstance().getPluginConfig();
            if (current < latest) {
                ITextComponent msg = config.getLangConfig().format("old-file").replace(ImmutableMap.of("%s", "locale/fish_" + config.getLocale() + ".toml")).output();
                plugin.getLogger().warn(msg.getString());
            }
        });
    }

    private static F defaultMessagesConfig() {
        return new MessagesConfig<>(ForgePlayerAnnouncement.serverBroadcast(), ForgePlayerAnnouncement.serverBroadcast(), Color.BLUE);
    }
}
