package me.elsiff.morefish.dao.yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import me.elsiff.morefish.dao.RecordDao;
import me.elsiff.morefish.fishing.Fish;
import me.elsiff.morefish.fishing.FishType;
import me.elsiff.morefish.fishing.FishTypeTable;
import me.elsiff.morefish.fishing.competition.Record;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public final class YamlRecordDao implements RecordDao {

    private final File file;
    private final FishTypeTable fishTypeTable;
    private final Plugin plugin;
    private final YamlConfiguration yaml;

    public YamlRecordDao(@Nonnull Plugin plugin, @Nonnull FishTypeTable fishTypeTable) {
        super();
        this.plugin = plugin;
        this.fishTypeTable = fishTypeTable;
        Path path = plugin.getDataFolder().toPath().resolve("records");
        file = path.toFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.yaml = YamlConfiguration.loadConfiguration(this.file);
    }

    @Nonnull
    public List<Record> all() {
        List<Record> records = new ArrayList<>();
        yaml.getKeys(false).stream().map(yaml::getConfigurationSection).forEach(section -> {
            UUID id = UUID.fromString(section.getName());
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(id);
            String fishTypeName = section.getString("fish-type");
            FishType fishType = fishTypeTable.getTypes().stream().filter(it -> it.getName().equals(fishTypeName)).findFirst().orElseThrow(() -> new IllegalStateException("Fish type doesn't exist for " + fishTypeName));
            double fishLength = section.getDouble("fish-length");
            Fish fish = new Fish(fishType, fishLength);
            records.add(new Record(player, fish));
        });

        records.sort(Comparator.reverseOrder());
        return records;
    }

    public void clear() {
        yaml.getKeys(false).forEach(key -> yaml.set(key, null));
        try {
            yaml.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insert(@Nonnull Record record) {
        String id = record.getFisher().getUniqueId().toString();
        if (yaml.contains(id)) {
            throw new IllegalArgumentException("Record must not exist in the ranking");
        }

        setRecord(yaml.createSection(id), record);
        try {
            yaml.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setRecord(ConfigurationSection section, Record record) {
        section.set("fish-type", record.getFish().getType().getName());
        section.set("fish-length", record.getFish().getLength());
    }

    @Nonnull
    public List<Record> top(int size) {
        return all().subList(0, Math.min(5, all().size()));
    }

    public void update(@Nonnull Record record) {
        String id = record.getFisher().getUniqueId().toString();
        if (!yaml.contains(id)) {
            throw new IllegalArgumentException("Record must exist in the ranking");
        }

        setRecord(yaml.getConfigurationSection(id), record);
        try {
            yaml.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
