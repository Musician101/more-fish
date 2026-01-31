package me.elsiff.morefish.records;

import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.lang.TagResolverUtil;
import me.elsiff.morefish.serialize.fish.FishRaritySerializer;
import me.elsiff.morefish.serialize.record.FishRecordSerializer;
import me.elsiff.morefish.serialize.record.FishSerializer;
import me.elsiff.morefish.serialize.record.FishTypeRecordSerializer;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.NodePath;
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
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
public abstract class FishRecordKeeper {

    @Nullable
    protected YamlConfigurationLoader loader;
    protected boolean loading = false;

    protected void initLoader() {
        ConfigurationOptions options = ConfigurationOptions.defaults().serializers(b -> {
            b.register(FishRecord.class, new FishRecordSerializer());
            b.register(Fish.class, new FishSerializer());
            b.register(FishType.class, new FishTypeRecordSerializer());
            b.register(FishRarity.class, new FishRaritySerializer());
        });
        loader = YamlConfigurationLoader.builder().path(getPath()).nodeStyle(NodeStyle.BLOCK).defaultOptions(options).build();
    }

    protected final List<FishRecord> records = new ArrayList<>();

    public void informAboutRanking(CommandSender receiver) {
        informAboutRanking(receiver, records);
    }

    public void informAboutRanking(CommandSender receiver, List<FishRecord> fishRecords) {
        if (loading) {
            return;
        }

        NodePath topPath = NodePath.path("main", "top");
        if (fishRecords.isEmpty()) {
            receiver.sendMessage(lang().getComponent(topPath.withAppendedChild("no-catches")));
        }
        else {
            int topSize = getPlugin().getConfig().getInt("messages.top-number", 1);
            fishRecords.sort(SortType.LENGTH.reversed());
            List<FishRecord> top = fishRecords.subList(0, Math.min(topSize, fishRecords.size()));
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                TagResolver resolver = TagResolver.resolver(TagResolverUtil.ordinal(number), record);
                receiver.sendMessage(lang().getComponent(topPath.withAppendedChild("ranked-record"), resolver));
            });

            NodePath playerPath = topPath.withAppendedChild("player");
            if (receiver instanceof Player player) {
                if (contains(player.getUniqueId())) {
                    Entry<Integer, FishRecord> entry = rankedRecordOf(player, fishRecords);
                    FishRecord record = entry.getValue();
                    TagResolver resolver = TagResolver.resolver(record, TagResolverUtil.ordinal(entry.getKey() + 1));
                    receiver.sendMessage(lang().getComponent(playerPath.withAppendedChild("record"), resolver));
                }
                else {
                    receiver.sendMessage(lang().getComponent(playerPath.withAppendedChild("no-catch")));
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
