package me.elsiff.morefish;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishType;
import me.elsiff.morefish.fishing.FishTypeTable;
import me.elsiff.morefish.fishing.competition.Record;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class RecordHandler {

    private final File file;
    private final FishTypeTable fishTypeTable;
    private final YamlConfiguration yaml;

    public RecordHandler() {
        this.fishTypeTable = getPlugin().getFishTypeTable();
        Path path = getPlugin().getDataFolder().toPath().resolve("records");
        file = path.toFile();
        try {
            Files.createFile(path);
        }
        catch (IOException e) {
            MoreFish.getPlugin().getSLF4JLogger().error("An error occurred while saving records.", e);
        }

        this.yaml = YamlConfiguration.loadConfiguration(this.file);
    }

    @NotNull
    public List<Record> all() {
        return yaml.getKeys(false).stream().map(yaml::getConfigurationSection).filter(Objects::nonNull).map(section -> {
            UUID id = UUID.fromString(section.getName());
            String fishTypeName = section.getString("fish-type");
            FishType fishType = fishTypeTable.getTypes().stream().filter(it -> it.name().equals(fishTypeName)).findFirst().orElseThrow(() -> new IllegalStateException("Fish type doesn't exist for " + fishTypeName));
            double fishLength = section.getDouble("fish-length");
            Fish fish = new Fish(fishType, fishLength);
            return new Record(id, fish);
        }).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    public void clear() {
        yaml.getKeys(false).forEach(key -> yaml.set(key, null));
        try {
            yaml.save(file);
        }
        catch (IOException e) {
            MoreFish.getPlugin().getSLF4JLogger().error("An error occurred while saving records.", e);
        }
    }

    public void insert(@NotNull Record record) {
        String id = record.fisher().toString();
        if (yaml.contains(id)) {
            throw new IllegalArgumentException("Record must not exist in the ranking");
        }

        setRecord(yaml.createSection(id), record);
        try {
            yaml.save(file);
        }
        catch (IOException e) {
            MoreFish.getPlugin().getSLF4JLogger().error("An error occurred while saving records.", e);
        }
    }

    private void setRecord(ConfigurationSection section, Record record) {
        section.set("fish-type", record.fish().type().name());
        section.set("fish-length", record.fish().length());
    }

    @NotNull
    public List<Record> top(int size) {
        return all().subList(0, Math.min(size, all().size()));
    }

    public void update(@NotNull Record record) {
        String id = record.fisher().toString();
        ConfigurationSection cs = yaml.getConfigurationSection(id);
        if (!yaml.contains(id) || cs == null) {
            throw new IllegalArgumentException("Record must exist in the ranking");
        }

        setRecord(cs, record);
        try {
            yaml.save(file);
        }
        catch (IOException e) {
            MoreFish.getPlugin().getSLF4JLogger().error("An error occurred while saving records.", e);
        }
    }
}
