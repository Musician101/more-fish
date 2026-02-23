package me.elsiff.morefish.records;

import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.lang.ArgumentUtil;
import me.elsiff.morefish.serialize.fish.FishRaritySerializer;
import me.elsiff.morefish.serialize.record.FishRecordSerializer;
import me.elsiff.morefish.serialize.record.FishSerializer;
import me.elsiff.morefish.serialize.record.FishTypeRecordSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public abstract class FishRecordKeeper {

    protected final List<FishRecord> records = new ArrayList<>();
    protected boolean loading = false;

    protected YamlConfigurationLoader loader() {
        ConfigurationOptions options = ConfigurationOptions.defaults().serializers(b -> {
            b.register(FishRecord.class, new FishRecordSerializer());
            b.register(Fish.class, new FishSerializer());
            b.register(FishType.class, new FishTypeRecordSerializer());
            b.register(FishRarity.class, new FishRaritySerializer());
        });
        return YamlConfigurationLoader.builder().path(getPath()).nodeStyle(NodeStyle.BLOCK).defaultOptions(options).build();
    }

    public void informAboutRanking(CommandSender receiver) {
        informAboutRanking(receiver, records);
    }

    public void informAboutRanking(CommandSender receiver, List<FishRecord> fishRecords) {
        if (loading) {
            return;
        }

        if (fishRecords.isEmpty()) {
            receiver.sendMessage(Component.translatable("morefish.main.top.no-catches"));
        }
        else {
            int topSize = getPlugin().getConfig().getInt("messages.top-number", 1);
            fishRecords.sort(SortType.LENGTH.reversed());
            List<FishRecord> top = fishRecords.subList(0, Math.min(topSize, fishRecords.size()));
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                receiver.sendMessage(Component.translatable("morefish.main.top.ranked-record", ArgumentUtil.ordinal(number), record));
            });

            if (receiver instanceof Player player) {
                if (contains(player.getUniqueId())) {
                    Entry<Integer, FishRecord> entry = rankedRecordOf(player, fishRecords);
                    FishRecord record = entry.getValue();
                    receiver.sendMessage(Component.translatable("morefish.main.top.player.record", ArgumentUtil.ordinal(entry.getKey() + 1), record));
                }
                else {
                    receiver.sendMessage(Component.translatable("morefish.main.top.player.no-catch"));
                }
            }
        }
    }

    public abstract void save();

    public void clear() {
        if (!loading) {
            records.clear();
        }
    }

    public void clearRecordHolder(UUID holder) {
        if (!loading) {
            records.removeIf(record -> holder.equals(record.fisher()));
        }
    }

    public List<FishRecord> getRecords() {
        if (loading) {
            return List.of();
        }

        return new ArrayList<>(records);
    }

    public void add(FishRecord record) {
        if (!loading) {
            records.add(record);
        }
    }

    public boolean contains(UUID uuid) {
        if (loading) {
            return false;
        }

        return records.stream().anyMatch(r -> r.fisher().equals(uuid));
    }

    private Map.Entry<Integer, FishRecord> rankedRecordOf(OfflinePlayer contestant, List<FishRecord> records) {
        records.sort(SortType.LENGTH.reversed());
        int place = 0;
        for (FishRecord record : records) {
            if (record.fisher().equals(contestant.getUniqueId())) {
                return new SimpleEntry<>(place, record);
            }

            place++;
        }

        throw new IllegalStateException("Record not found");
    }

    protected abstract Path getPath();
}
