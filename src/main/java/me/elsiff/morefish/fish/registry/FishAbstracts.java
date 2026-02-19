package me.elsiff.morefish.fish.registry;

import me.elsiff.morefish.fish.FishAbstract;
import me.elsiff.morefish.fish.PlayerAnnouncement;
import me.elsiff.morefish.fish.PlayerAnnouncement.Type;
import me.elsiff.morefish.fish.condition.BiomesCondition;
import me.elsiff.morefish.fish.condition.EnchantmentsCondition;
import me.elsiff.morefish.fish.condition.FishConditions;
import me.elsiff.morefish.fish.condition.LocationYCondition;
import me.elsiff.morefish.fish.condition.McmmoSkillsCondition;
import me.elsiff.morefish.fish.condition.PotionEffectsCondition;
import me.elsiff.morefish.fish.condition.RainingCondition;
import me.elsiff.morefish.fish.condition.ThunderingCondition;
import me.elsiff.morefish.fish.condition.TimeCondition;
import me.elsiff.morefish.fish.condition.XpLevelCondition;
import me.elsiff.morefish.serialize.fish.NamespacedKeySerializer;
import me.elsiff.morefish.serialize.fish.PlayerAnnouncementSerializer;
import me.elsiff.morefish.serialize.fish.PlayerAnnouncementSerializer.PlayerAnnouncementTypeSerializer;
import me.elsiff.morefish.serialize.fish.condition.BiomeConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.EnchantmentsConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.FishConditionsSerializer;
import me.elsiff.morefish.serialize.fish.condition.LocationYConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.McmmoSkillConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.PotionEffectsConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.RainingConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.ThunderingConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.TimeConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.XpLevelConditionSerializer;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.serialize.TypeSerializerCollection.Builder;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public abstract sealed class FishAbstracts<F extends FishAbstract<F>> implements Iterable<F> permits FishRarities, FishTypes {

    private static final TypeSerializerCollection tsc = TypeSerializerCollection.defaults().childBuilder()
            .register(PlayerAnnouncement.class, new PlayerAnnouncementSerializer())
            .register(Type.class, new PlayerAnnouncementTypeSerializer())
            .register(FishConditions.class, new FishConditionsSerializer())
            .register(BiomesCondition.class, new BiomeConditionSerializer())
            .register(EnchantmentsCondition.class, new EnchantmentsConditionSerializer())
            .register(LocationYCondition.class, new LocationYConditionSerializer())
            .register(McmmoSkillsCondition.class, new McmmoSkillConditionSerializer())
            .register(PotionEffectsCondition.class, new PotionEffectsConditionSerializer())
            .register(RainingCondition.class, new RainingConditionSerializer())
            .register(ThunderingCondition.class, new ThunderingConditionSerializer())
            .register(TimeCondition.class, new TimeConditionSerializer())
            .register(XpLevelCondition.class, new XpLevelConditionSerializer())
            .register(NamespacedKey.class, new NamespacedKeySerializer())
            .build();
    protected final List<F> values = new ArrayList<>();
    protected final Class<F> valueClass;
    private final String pluralName;
    private final String singularName;

    public FishAbstracts(String singularName, String pluralName, Class<F> valueClass) {
        this.singularName = singularName;
        this.pluralName = pluralName;
        this.valueClass = valueClass;
    }

    public List<F> values() {
        return values;
    }

    public Optional<F> get(NamespacedKey key) {
        return values.stream().filter(v -> v.getKey().equals(key)).findFirst();
    }

    public void load() throws IOException {
        IOException exception = new IOException("One or more errors occurred while loading " + pluralName);
        if (getPlugin().getConfig().getBoolean("general.load-defaults")) {
            try (JarFile jar = new JarFile(getPlugin().getClass().getProtectionDomain().getCodeSource().getLocation().getPath())) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith("fish/" + singularName.toLowerCase() + "/") && entryName.endsWith(".yml")) {
                        getPlugin().saveResource(entryName, false);
                    }
                }
            }
        }

        if (Files.notExists(dir())) {
            Files.createDirectories(dir());
        }

        values.clear();
        try (Stream<Path> stream = Files.walk(dir())) {
            stream.filter(path -> !Files.isDirectory(path)).filter(this::isYaml).map(this::loader).map(this::loadValue).<IOException>mapMulti(Optional::ifPresent).forEach(exception::addSuppressed);
        }

        if (exception.getSuppressed().length > 0) {
            throw exception;
        }
    }

    public void save(F value) throws IOException {
        if (Files.notExists(dir())) {
            Files.createDirectories(dir());
        }

        YamlConfigurationLoader loader = loader(dir().resolve(value.getKey() + ".yml"));
        ConfigurationNode node = CommentedConfigurationNode.root(loader.defaultOptions());
        node.set(value);
        loader.save(node);
    }

    public void delete(F value) throws IOException {
        Files.delete(dir().resolve(value.getKey() + ".yml"));
        values.remove(value);
    }

    protected abstract Path dir();

    private boolean isYaml(Path path) {
        return path.toString().endsWith(".yml");
    }

    private YamlConfigurationLoader loader(Path path) {
        return YamlConfigurationLoader.builder().path(path).defaultOptions(c -> c.serializers(tsc).serializers(this::serializers)).build();
    }

    protected abstract void serializers(Builder builder);

    private Optional<IOException> loadValue(YamlConfigurationLoader loader) {
        try {
            values.add(loader.load().require(valueClass));
            return Optional.empty();
        }
        catch (IOException e) {
            return Optional.of(e);
        }
    }

    @Override
    public Iterator<F> iterator() {
        return values.iterator();
    }

    public Stream<F> stream() {
        return values.stream();
    }
}
