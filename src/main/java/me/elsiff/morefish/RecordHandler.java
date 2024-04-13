package me.elsiff.morefish;

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
import me.elsiff.morefish.fishing.competition.Record;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class RecordHandler {

    private YamlConfiguration yaml = new YamlConfiguration();

    public void add(List<Record> records) {
        records.forEach(this::add);
    }

    public void add(@NotNull Record record) {
        String id = record.fisher().toString();
        ConfigurationSection cs = yaml.getConfigurationSection(id);
        if (cs != null && record.fish().length() > cs.getDouble("length")) {
            setRecord(cs, record);
        }
        else {
            setRecord(yaml.createSection(id), record);
        }
    }

    @NotNull
    public List<Record> all() {
        return yaml.getKeys(false).stream().map(yaml::getConfigurationSection).filter(Objects::nonNull).map(section -> {
            UUID id = UUID.fromString(section.getName());
            String fishTypeName = section.getString("fish-type");
            FishType fishType = getPlugin().getFishTypeTable().getTypes().stream().filter(it -> it.name().equals(fishTypeName)).findFirst().orElseThrow(() -> new IllegalStateException("Fish type doesn't exist for " + fishTypeName));
            double fishLength = section.getDouble("fish-length");
            Fish fish = new Fish(fishType, fishLength);
            return new Record(id, fish);
        }).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    public void clear() {
        yaml.getKeys(false).forEach(key -> yaml.set(key, null));
    }

    private Path getPath() {
        return getPlugin().getDataFolder().toPath().resolve("all_time_records.yml");
    }

    public void load() {
        try {
            Files.createFile(getPath());
        }
        catch (IOException e) {
            MoreFish.getPlugin().getSLF4JLogger().error("An error occurred while loading records.", e);
            return;
        }

        yaml = YamlConfiguration.loadConfiguration(this.getPath().toFile());
    }

    public void save() {
        try {
            Files.createFile(getPath());
            yaml.save(getPath().toFile());
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
}
