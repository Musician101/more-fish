package me.elsiff.morefish.competition;

import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.records.FishRecord;
import me.elsiff.morefish.records.FishRecordKeeper;
import me.elsiff.morefish.hooker.MusiBoardHooker;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static me.elsiff.morefish.MoreFish.getPlugin;

public final class FishingCompetition extends FishRecordKeeper {

    private boolean enabled = false;
    private String startTime;

    @SuppressWarnings("StringConcatenationArgumentToLogCall")
    @Override
    public void save() {
        try {
            if (Files.notExists(getPath())) {
                Files.createDirectories(getPath().getParent());
                Files.createFile(getPath());
            }

            YamlConfiguration yaml = new YamlConfiguration();
            IntStream.range(0, records.size()).forEach(i -> {
                FishRecord record = records.get(i);
                ConfigurationSection cs = new MemoryConfiguration();
                cs.set("uuid", record.fisher().toString());
                cs.set("name", record.getFishName());
                cs.set("length", record.getLength());
                cs.set("rarity", record.getRarityName());
                cs.set("rarity_probability", record.getRarityProbability());
                cs.set("timestamp", record.timestamp());
                yaml.set(i + "", cs);
            });
            yaml.save(getPath().toFile());
        }
        catch (IOException e) {
            getPlugin().getSLF4JLogger().error("Error loading " + getPath().getFileName(), e);
        }
    }

    public void disable() {
        enabled = false;
        getMusiBoard().clear();
        save();
        startTime = null;
    }

    public void enable() {
        enabled = true;
        startTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd h_mma"));
    }

    private MusiBoardHooker getMusiBoard() {
        return getPlugin().getMusiBoard();
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    protected Path getPath() {
        return getPlugin().getDataFolder().toPath().resolve("competition logs/" + startTime + ".yml");
    }

    @Override
    public void add(@NotNull FishRecord record) {
        Optional<FishRecord> optional = getRecord(record.fisher());
        if (optional.isPresent()) {
            optional.filter(r -> record.getLength() >= r.getLength()).ifPresent(r -> {
                records.remove(optional.get());
                records.add(record);
            });
        }
        else {
            records.add(record);
        }

        getMusiBoard().update();
    }

    public int rankNumberOf(@NotNull FishRecord record) {
        return getRecords().indexOf(record) + 1;
    }

    private Optional<FishRecord> getRecord(@NotNull UUID contestant) {
        return records.stream().filter(record -> contestant.equals(record.fisher())).findFirst();
    }

    @NotNull
    public FishRecord recordOf(@NotNull UUID contestant) {
        return getRecord(contestant).orElseThrow(() -> new IllegalStateException("Record not found"));
    }

    @NotNull
    public FishRecord recordOf(int rankNumber) {
        if (rankNumber >= 1 && rankNumber <= getRecords().size()) {
            return getRecords().get(rankNumber - 1);
        }

        throw new IllegalArgumentException("Rank number is out of records size.");
    }

    public boolean willBeNewFirst(@NotNull OfflinePlayer catcher, @NotNull Fish fish) {
        if (!getRecords().isEmpty()) {
            List<FishRecord> records = getRecords();
            records.sort(SortType.LENGTH.reversed());
            FishRecord record = records.getFirst();
            return fish.length() > record.getLength() && !record.fisher().equals(catcher.getUniqueId());
        }

        return true;
    }
}
