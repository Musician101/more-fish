package me.elsiff.morefish.competition;

import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.records.FishRecord;
import me.elsiff.morefish.records.FishRecordKeeper;
import me.elsiff.morefish.util.CheckedConsumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public final class FishingCompetition extends FishRecordKeeper {

    private boolean enabled = false;
    @Nullable
    private String startTime;

    @Override
    public void save() {
        try {
            if (Files.notExists(getPath())) {
                Files.createDirectories(getPath().getParent());
                Files.createFile(getPath());
            }

            ConfigurationNode node = loader().createNode();
            ConfigurateException ex = new ConfigurateException();
            IntStream.range(0, records.size()).boxed().map(CheckedConsumer.asFunction(i -> node.node(i).set(records.get(i)))).filter(Objects::nonNull).forEach(ex::addSuppressed);
            loader().save(node);
            if (ex.getSuppressed().length > 0) {
                throw ex;
            }
        }
        catch (IOException e) {
            Component message = Component.translatable("morefish.main.contest.logs.error", Argument.string("file", getPath().getFileName().toString()));
            getPlugin().getComponentLogger().error(message, e);
        }
    }

    public void disable() {
        enabled = false;
        getPlugin().getMusiBoard().clear();
        save();
        startTime = null;
    }

    public void enable() {
        enabled = true;
        startTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd h_mma"));
        loader();
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    protected Path getPath() {
        return getPlugin().getDataPath().resolve("competition logs/" + startTime + ".yml");
    }

    @Override
    public void add(FishRecord record) {
        Optional<FishRecord> optional = getRecord(record.fisher());
        if (optional.isPresent()) {
            optional.filter(r -> record.fish().length() >= r.fish().length()).ifPresent(r -> {
                records.remove(optional.get());
                records.add(record);
            });
        }
        else {
            records.add(record);
        }

        getPlugin().getMusiBoard().update();
    }

    private Optional<FishRecord> getRecord(UUID contestant) {
        return records.stream().filter(record -> contestant.equals(record.fisher())).findFirst();
    }

    public boolean willBeNewFirst(OfflinePlayer catcher, Fish fish) {
        if (!getRecords().isEmpty()) {
            List<FishRecord> records = getRecords();
            records.sort(SortType.LENGTH.reversed());
            FishRecord record = records.getFirst();
            return fish.length() > record.fish().length() && !record.fisher().equals(catcher.getUniqueId());
        }

        return true;
    }
}
