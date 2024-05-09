package me.elsiff.morefish.fishing.fishrecords;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class FishingLogs extends FishRecordKeeper {

    public void load() {
        Bukkit.getAsyncScheduler().runNow(getPlugin(), task -> {
            try {
                if (Files.notExists(getPath())) {
                    Files.createFile(getPath());
                }

                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(getPath().toFile());
                yaml.getMapList("records").stream().map(map -> {
                    UUID uuid = UUID.fromString(getString(map, "uuid", null));
                    String fishName = getString(map, "name", "invalid");
                    String rarityName = getString(map, "rarity", "invalid");
                    double length = getDouble(map, "length");
                    double rarityProbability = getDouble(map, "rarity_probability");
                    Object obj = map.get("timestamp");
                    long timestamp = obj == null ? 0L : Long.parseLong(obj.toString());
                    return new FishRecord(uuid, length, fishName, rarityName, rarityProbability, timestamp);
                }).forEach(records::add);
            }
            catch (IOException e) {
                getPlugin().getSLF4JLogger().error("Error loading fishing_logs.yml", e);
            }
        });
    }

    private double getDouble(Map<?, ?> map, String key) {
        Object obj = map.get(key);
        return obj == null ? 0D : Double.parseDouble(obj.toString());
    }

    private String getString(Map<?, ?> map, String key, String defaultValue) {
        if (defaultValue == null && !map.containsKey(key)) {
            throw new IllegalArgumentException("UUID is missing!");
        }

        Object obj = map.get(key);
        return obj == null ? defaultValue : obj.toString();
    }

    public void save() {
        try {
            if (Files.notExists(getPath())) {
                Files.createFile(getPath());
            }

            YamlConfiguration yaml = new YamlConfiguration();
            yaml.set("records", records.stream().map(record -> {
                ConfigurationSection cs = new MemoryConfiguration();
                cs.set("uuid", record.fisher().toString());
                cs.set("name", record.getFishName());
                cs.set("length", record.getLength());
                cs.set("rarity", record.getRarityName());
                cs.set("rarity_probability", record.getRarityProbability());
                cs.set("timestamp", record.timestamp());
                return cs;
            }).toList());
            yaml.save(getPath().toFile());
        }
        catch (IOException e) {
            getPlugin().getSLF4JLogger().error("Error loading fishing_logs.yml", e);
        }
    }

    private Path getPath() {
        return getPlugin().getDataFolder().toPath().resolve("fishing_logs.yml");
    }

    @NotNull
    public List<FishRecord> getFisher(@NotNull UUID fisher) {
        return records.stream().filter(r -> fisher.equals(r.fisher())).toList();
    }
}
