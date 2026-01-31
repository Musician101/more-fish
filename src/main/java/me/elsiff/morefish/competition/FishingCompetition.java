package me.elsiff.morefish.competition;

import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.records.FishRecord;
import me.elsiff.morefish.records.FishRecordKeeper;
import me.elsiff.morefish.util.CheckedConsumer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;

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
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
public final class FishingCompetition extends FishRecordKeeper {

    private boolean enabled = false;
    @Nullable
    private String startTime;

    @Override
    public void save() {
        NodePath path = NodePath.path("main", "contest", "logs");
        try {
            if (Files.notExists(getPath())) {
                Files.createDirectories(getPath().getParent());
                Files.createFile(getPath());
            }

            if (loader == null) {
                getPlugin().getComponentLogger().error(lang().getComponent(path.withAppendedChild("critical-error")));
                return;
            }

            ConfigurationNode node = loader.createNode();
            ConfigurateException ex = new ConfigurateException();
            IntStream.range(0, records.size()).boxed().map(CheckedConsumer.asFunction(i -> node.node(i).set(records.get(i)))).filter(Objects::nonNull).forEach(ex::addSuppressed);
            loader.save(node);
            if (ex.getSuppressed().length > 0) {
                throw ex;
            }
        }
        catch (IOException e) {
            TagResolver resolver = Placeholder.parsed("file", getPath().getFileName().toString());
            getPlugin().getComponentLogger().error(lang().getComponent(path.withAppendedChild("error"), resolver), e);
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
        initLoader();
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

    public int rankNumberOf(FishRecord record) {
        return getRecords().indexOf(record) + 1;
    }

    private Optional<FishRecord> getRecord(UUID contestant) {
        return records.stream().filter(record -> contestant.equals(record.fisher())).findFirst();
    }

    public FishRecord recordOf(UUID contestant) {
        return getRecord(contestant).orElseThrow(() -> new IllegalStateException("Record not found"));
    }

    public FishRecord recordOf(int rankNumber) {
        if (rankNumber >= 1 && rankNumber <= getRecords().size()) {
            return getRecords().get(rankNumber - 1);
        }

        throw new IllegalArgumentException("Rank number is out of records size.");
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
