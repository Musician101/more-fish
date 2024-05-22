package me.elsiff.morefish.text;

import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.fishrecords.FishRecord;
import me.elsiff.morefish.text.tagresolver.FishTagResolver;
import me.elsiff.morefish.text.tagresolver.LangTagResolver;
import me.elsiff.morefish.text.tagresolver.RankTagResolver;
import me.elsiff.morefish.text.tagresolver.TopFishLengthTagResolver;
import me.elsiff.morefish.text.tagresolver.TopFishTagResolver;
import me.elsiff.morefish.text.tagresolver.TopPlayerTagResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.resolver;

public class Lang {

    @NotNull
    private static ConfigurationSection LANG = new MemoryConfiguration();

    private Lang() {

    }

    public static void reload() {
        getPlugin().saveResource("lang.yml", false);
        File file = new File(getPlugin().getDataFolder(), "lang.yml");
        LANG = YamlConfiguration.loadConfiguration(file);
    }

    @NotNull
    public static String raw(@NotNull String key) {
        return LANG.getString(key, "");
    }

    @NotNull
    public static Set<String> keys() {
        return LANG.getKeys(false);
    }

    @NotNull
    public static Component replace(@NotNull String string, @NotNull TagResolver extraTagResolvers, @Nullable Player player) {
        UUID uuid = player == null ? null : player.getUniqueId();
        TagResolver tagResolver = resolver(new LangTagResolver(extraTagResolvers, player), new FishTagResolver(uuid), new RankTagResolver(uuid), new TopFishTagResolver(), new TopFishLengthTagResolver(), new TopPlayerTagResolver(), extraTagResolvers);
        return miniMessage().deserialize(string, tagResolver);
    }

    @NotNull
    public static TagResolver tagResolver(@NotNull String name, @NotNull String tag) {
        return tagResolver(name, text(tag));
    }

    @NotNull
    public static TagResolver tagResolver(@NotNull String name, @NotNull Component tag) {
        return tagResolver(name, Tag.selfClosingInserting(tag));
    }

    @SuppressWarnings("PatternValidation")
    @NotNull
    public static TagResolver tagResolver(@NotNull String name, @NotNull Tag tag) {
        return TagResolver.builder().tag(name, tag).build();
    }

    @NotNull
    public static TagResolver tagResolver(@NotNull String name, double tag) {
        return tagResolver(name, text(tag));
    }

    @NotNull
    public static Component replace(@NotNull String string) {
        return replace(string, TagResolver.empty());
    }

    @NotNull
    public static Component replace(@NotNull String string, @NotNull TagResolver extraTagResolvers) {
        return replace(string, extraTagResolvers, null);
    }

    @NotNull
    public static List<Component> replace(@NotNull List<String> strings, @NotNull TagResolver extraTagResolver, @Nullable Player player) {
        return strings.stream().map(c -> replace(c, extraTagResolver, player)).collect(Collectors.toList());
    }

    @NotNull
    public static TagResolver playerName(@NotNull OfflinePlayer player) {
        String name = player.getName();
        if (name == null) {
            name = player.getUniqueId().toString();
        }

        return tagResolver("player", name);
    }

    @NotNull
    public static TagResolver fishLength(@NotNull Fish fish) {
        return tagResolver("length", fish.length());
    }

    @NotNull
    public static TagResolver fishLength(@NotNull FishRecord record) {
        return tagResolver("length", record.getLength());
    }

    @NotNull
    public static TagResolver date(@NotNull FishRecord record) {
        return resolver("date", (argumentQueue, context) -> {
            Date date = new Date(record.timestamp());
            DateFormat format = DateFormat.getDateInstance();
            if (argumentQueue.hasNext()) {
                format = new SimpleDateFormat(argumentQueue.pop().value());
            }

            return Tag.selfClosingInserting(text(format.format(date)));
        });
    }

    @NotNull
    public static TagResolver fishRarity(@NotNull Fish fish) {
        return tagResolver("rarity", fish.rarity().displayName().toUpperCase());
    }

    @NotNull
    public static TagResolver fishRarityColor(@NotNull Fish fish) {
        return tagResolver("rarity_color", Tag.styling(builder -> {
            String color = fish.rarity().color();
            TextColor textColor = NamedTextColor.NAMES.valueOr(color, NamedTextColor.WHITE);
            if (color.startsWith(TextColor.HEX_PREFIX)) {
                textColor = TextColor.fromHexString(color);
            }

            builder.color(textColor);
        }));
    }

    @NotNull
    public static TagResolver fishName(@NotNull Fish fish) {
        return tagResolver("fish_name", fish.name());
    }

    @NotNull
    public static TagResolver fishName(@NotNull FishRecord record) {
        return tagResolver("fish_name", record.getFishName());
    }

    @NotNull
    public static TagResolver timeRemaining(long second) {
        StringBuilder builder = new StringBuilder();
        Duration duration = Duration.ofSeconds(second);
        if (duration.toMinutes() > 0L) {
            builder.append(duration.toMinutes()).append("m").append(" ");
        }

        builder.append(duration.getSeconds() % (long) 60).append("s");
        return tagResolver("time-remaining", builder.toString());
    }
}
