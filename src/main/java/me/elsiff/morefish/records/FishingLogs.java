package me.elsiff.morefish.records;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public final class FishingLogs extends FishRecordKeeper {

    @Override
    public void save() {
        try {
            if (Files.notExists(getPath())) {
                Files.createDirectories(getPath().getParent());
                Files.createFile(getPath());
            }

            ConfigurationNode node = loader().createNode();
            node.node("records").set(records);
            loader().save(node);
        }
        catch (IOException e) {
            getPlugin().getComponentLogger().error(Component.translatable("morefish.main.fishing-logs.error.save"), e);
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

                loader();
                ConfigurationNode node = loader().load();
                records.addAll(node.node("records").getList(FishRecord.class, List.of()));
            }
            catch (IOException e) {
                getPlugin().getComponentLogger().error(Component.translatable("morefish.main.fishing-logs.error.load"), e);
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
