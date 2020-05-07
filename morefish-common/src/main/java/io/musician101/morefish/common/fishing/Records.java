package io.musician101.morefish.common.fishing;

import io.musician101.morefish.common.ConfigurateLoader;
import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import io.musician101.morefish.common.fishing.catchhandler.CatchHandler;
import io.musician101.morefish.common.fishing.competition.Record;
import io.musician101.morefish.common.fishing.condition.FishCondition;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Records<A extends PlayerAnnouncement<?>, B, C extends FishCondition<?, ?>, H extends CatchHandler<?, ?>, I> {

    @Nonnull
    private final ConfigurationLoader<? extends ConfigurationNode> loader;
    @Nonnull
    private Supplier<Stream<FishType<A, B, C, H, I>>> fishTypesSupplier;
    private ConfigurationNode node;

    public Records(@Nonnull File file, @Nonnull ConfigurateLoader loader, @Nonnull Supplier<Stream<FishType<A, B, C, H, I>>> fishTypesSupplier) {
        this.fishTypesSupplier = fishTypesSupplier;
        this.loader = loader.get(file.toPath());
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
    public List<Record<A, B, C, H, I>> all() {
        return node.getChildrenMap().entrySet().stream().map(e -> {
            String key = e.getKey().toString();
            ConfigurationNode section = e.getValue();
            UUID id = UUID.fromString(key);
            String fishTypeName = section.getString("fish-type");
            FishType<A, B, C, H, I> fishType = fishTypesSupplier.get().filter(it -> it.getName().equals(fishTypeName)).findFirst().orElseThrow(() -> new IllegalStateException("Fish type doesn't exist for " + fishTypeName));
            double fishLength = section.getNode("fish-length").getDouble();
            Fish<A, B, C, H, I> fish = new Fish<>(fishType, fishLength);
            return new Record<>(id, fish);
        }).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    public void clear() {
        node.getChildrenMap().forEach((key, value) -> node.removeChild(key));
        try {
            loader.save(node);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insert(@Nonnull Record<A, B, C, H, I> record) {
        String id = record.getFisher().toString();
        ConfigurationNode cn = node.getNode(id);
        if (!cn.isVirtual()) {
            throw new IllegalArgumentException("Record must not exist in the ranking");
        }

        setRecord(cn, record);
        try {
            loader.save(node);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setRecord(ConfigurationNode node, Record<A, B, C, H, I> record) {
        node.getNode("fish-type").setValue(record.getFish().getType().getName());
        node.getNode("fish-length").setValue(record.getFish().getLength());
    }

    @Nonnull
    public List<Record<A, B, C, H, I>> top(int size) {
        return all().subList(0, Math.min(size, all().size()));
    }

    public void update(@Nonnull Record<A, B, C, H, I> record) {
        String id = record.getFisher().toString();
        ConfigurationNode cn = node.getNode(id);
        if (cn.isVirtual()) {
            throw new IllegalArgumentException("Record must exist in the ranking");
        }

        setRecord(cn, record);
        try {
            loader.save(node);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
