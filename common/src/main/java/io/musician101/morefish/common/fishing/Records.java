package io.musician101.morefish.common.fishing;

import io.musician101.morefish.common.fishing.competition.Record;
import io.musician101.musicianlibrary.java.configurate.ConfigurateLoader;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

public class Records {

    @Nonnull
    private final Supplier<Stream<FishType>> fishTypesSupplier;
    @Nonnull
    private final ConfigurationLoader<? extends ConfigurationNode> loader;
    private ConfigurationNode node;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Records(@Nonnull File file, @Nonnull ConfigurateLoader loader, @Nonnull Supplier<Stream<FishType>> fishTypesSupplier) {
        this.fishTypesSupplier = fishTypesSupplier;
        this.loader = loader.loader(file.toPath());
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            this.node = this.loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    public List<Record> all() {
        return node.childrenMap().entrySet().stream().map(e -> {
            String key = e.getKey().toString();
            ConfigurationNode section = e.getValue();
            UUID id = UUID.fromString(key);
            String fishTypeName = section.getString("fish-type");
            FishType fishType = fishTypesSupplier.get().filter(it -> it.getName().equals(fishTypeName)).findFirst().orElseThrow(() -> new IllegalStateException("Fish type doesn't exist for " + fishTypeName));
            double fishLength = section.node("fish-length").getDouble();
            Fish fish = new Fish(fishType, fishLength);
            return new Record(id, fish);
        }).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    public void clear() {
        node.childrenMap().forEach((key, value) -> node.removeChild(key));
        try {
            loader.save(node);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insert(@Nonnull Record record) {
        String id = record.getFisher().toString();
        ConfigurationNode cn = node.node(id);
        if (!cn.empty()) {
            throw new IllegalArgumentException("Record must not exist in the ranking");
        }

        try {
            setRecord(cn, record);
            loader.save(node);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setRecord(ConfigurationNode node, Record record) throws SerializationException {
        node.node("fish-type").set(record.getFish().getType().getName());
        node.node("fish-length").set(record.getFish().getLength());
    }

    @Nonnull
    public List<Record> top(int size) {
        return all().subList(0, Math.min(size, all().size()));
    }

    public void update(@Nonnull Record record) {
        String id = record.getFisher().toString();
        ConfigurationNode cn = node.node(id);
        if (cn.empty()) {
            throw new IllegalArgumentException("Record must exist in the ranking");
        }

        try {
            setRecord(cn, record);
            loader.save(node);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
