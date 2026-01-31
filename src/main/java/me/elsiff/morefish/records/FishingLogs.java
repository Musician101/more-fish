package me.elsiff.morefish.records;

import org.bukkit.Bukkit;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
public final class FishingLogs extends FishRecordKeeper {

    private final NodePath fishingLogsPath = NodePath.path("main", "fishing-logs");

    @Override
    public void save() {
        NodePath savePath = fishingLogsPath.withAppendedChild("save");
        try {
            if (Files.notExists(getPath())) {
                Files.createDirectories(getPath().getParent());
                Files.createFile(getPath());
            }

            if (loader == null) {
                getPlugin().getComponentLogger().error(lang().getComponent(savePath.withAppendedChild("critical-error")));
                return;
            }

            ConfigurationNode node = loader.createNode();
            node.node("records").set(records);
            loader.save(node);
        }
        catch (IOException e) {
            getPlugin().getComponentLogger().error(lang().getComponent(savePath.withAppendedChild("error")), e);
        }
    }

    public void load() {
        loading = true;
        Bukkit.getAsyncScheduler().runNow(getPlugin(), task -> {
            try {
                if (Files.notExists(getPath())) {
                    Files.createDirectories(getPath().getParent());
                    Files.createFile(getPath());
                }

                initLoader();
                ConfigurationNode node = loader.load();
                records.addAll(node.node("records").getList(FishRecord.class, List.of()));
            }
            catch (IOException e) {
                getPlugin().getComponentLogger().error(lang().getComponent(fishingLogsPath.withAppendedChild("load-error")), e);
            }

            loading = false;
        });
    }

    @Override
    protected Path getPath() {
        return getPlugin().getDataPath().resolve("fishing_logs.yml");
    }

    public List<FishRecord> getFisher(UUID fisher) {
        return records.stream().filter(r -> fisher.equals(r.fisher())).toList();
    }
}
