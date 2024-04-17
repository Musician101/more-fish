package me.elsiff.morefish.fishing.fishrecords;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class FishingLogs extends FishingRecordKeeper {

    public void load() {
        try {
            if (getFile().exists()) {
                getFile().createNewFile();
            }

            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(getFile());
            yaml.getKeys(false).stream().map(yaml::getList).filter(Objects::nonNull).map(ConfigurationSection.class::cast).map(cs -> {
                UUID uuid = UUID.fromString(cs.getName());
                String fishName = cs.getString("name", "invalid");
                String rarityName = cs.getString("rarity", "invalid");
                double length = cs.getDouble("length");
                double rarityProbability = cs.getDouble("rarity_probability");
                long timestamp = cs.getLong("timestamp");
                return new FishRecord(uuid, length, fishName, rarityName, rarityProbability, timestamp);
            }).forEach(records::add);
        }
        catch (IOException e) {
            getPlugin().getSLF4JLogger().error("Error loading fishing_logs.yml", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void save() {
        try {
            if (getFile().exists()) {
                getFile().createNewFile();
            }

            YamlConfiguration yaml = new YamlConfiguration();
            records.forEach(record -> {
                String fisher = record.fisher().toString();
                List<ConfigurationSection> list = (List<ConfigurationSection>) yaml.getList(fisher, new ArrayList<>());
                ConfigurationSection cs = new MemoryConfiguration();
                cs.set("name", record.getFishName());
                cs.set("length", record.getLength());
                cs.set("rarity", record.getRarityName());
                cs.set("rarity_probability", record.getRarityProbability());
                cs.set("timestamp", record.timestamp());
                list.add(cs);
                yaml.set(fisher, cs);
            });
            yaml.save(getFile());
        }
        catch (IOException e) {
            getPlugin().getSLF4JLogger().error("Error loading fishing_logs.yml", e);
        }
    }

    private File getFile() {
        return new File(getPlugin().getDataFolder(), "fishing_logs.yml");
    }

    @NotNull
    public List<FishRecord> getFisher(@NotNull UUID fisher) {
        return records.stream().filter(r -> fisher.equals(r.fisher())).toList();
    }
}
