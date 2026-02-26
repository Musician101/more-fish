package me.elsiff.morefish.lang;

import me.elsiff.morefish.util.ExceptionUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public class Lang extends MiniMessageTranslator {

    private final Map<Locale, ConfigurationNode> locales = new HashMap<>();

    @Override
    public Key name() {
        return Key.key("morefish:lang");
    }

    @Override
    protected @Nullable String getMiniMessageString(String key, Locale locale) {
        ConfigurationNode node = locales.get(locale);
        if (node == null) {
            node = locales.get(Locale.US);
        }

        return node.node(NodePath.of(key.split("\\."))).getString();
    }

    public void load() throws IOException {
        getPlugin().saveResource("lang/en_us/command.yml", false);
        getPlugin().saveResource("lang/en_us/editor.yml", false);
        getPlugin().saveResource("lang/en_us/gui.yml", false);
        getPlugin().saveResource("lang/en_us/main.yml", false);
        ExceptionUtils.throwIOException("One or more errors occurred while loading language files.", Locale.availableLocales().map(this::loadLocale).mapMulti(Optional::ifPresent));
    }

    private Optional<IOException> loadLocale(Locale locale) {
        try (Stream<Path> localeStream = Files.walk(getPlugin().getDataPath().resolve("lang/" + locale.toString().toLowerCase()))) {
            return localeStream.filter(this::isYAML).map(this::loader).map(loader -> loadFile(locale, loader)).filter(Objects::nonNull).collect(ExceptionUtils.toIOException("One or more errors occurred while loading " + locale));
        }
        catch (IOException e) {
            return Optional.of(e);
        }
    }

    private boolean isYAML(Path path) {
        return !Files.isDirectory(path) && path.getFileName().toString().endsWith("*.yml");
    }

    private YamlConfigurationLoader loader(Path path) {
        return YamlConfigurationLoader.builder().nodeStyle(NodeStyle.BLOCK).path(path).build();
    }

    @Nullable
    private IOException loadFile(Locale locale, YamlConfigurationLoader loader) {
        try {
            locales.put(locale, loader.load());
            return null;
        }
        catch (IOException e) {
            return e;
        }
    }
}
