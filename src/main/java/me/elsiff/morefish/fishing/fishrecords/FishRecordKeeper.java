package me.elsiff.morefish.fishing.fishrecords;

import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.text.Lang;
import me.elsiff.morefish.util.NumberUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;

public abstract class FishRecordKeeper {

    @NotNull
    protected final List<FishRecord> records = new ArrayList<>();

    public void informAboutRanking(@NotNull CommandSender receiver) {
        informAboutRanking(receiver, records);
    }

    public void informAboutRanking(@NotNull CommandSender receiver, @NotNull List<FishRecord> fishRecords) {
        if (fishRecords.isEmpty()) {
            receiver.sendMessage(Lang.replace("<mf-lang:top-no-catches>"));
        }
        else {
            int topSize = getPlugin().getConfig().getInt("messages.top-number", 1);
            fishRecords.sort(SortType.LENGTH.reversed());
            List<FishRecord> top = fishRecords.subList(0, Math.min(topSize, fishRecords.size()));
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                OfflinePlayer player = Bukkit.getOfflinePlayer(record.fisher());
                TagResolver tagResolver = TagResolver.resolver(Lang.tagResolver("player", player.getName() == null ? player.getUniqueId().toString() : player.getName()), Lang.tagResolver("ordinal", NumberUtils.ordinalOf(number)), Lang.tagResolver("record-length", record.getLength()), Lang.tagResolver("record-fish-name", record.getFishName()));
                receiver.sendMessage(Lang.replace("<mf-lang:top-ranked-record>", tagResolver));
            });

            if (receiver instanceof Player player) {
                if (contains(player.getUniqueId())) {
                    Entry<Integer, FishRecord> entry = rankedRecordOf(player, fishRecords);
                    FishRecord record = entry.getValue();
                    TagResolver tagResolver = TagResolver.resolver(Lang.tagResolver("ordinal", NumberUtils.ordinalOf(entry.getKey() + 1)), Lang.tagResolver("record-length", record.getLength()), Lang.tagResolver("record-fish-name", record.getFishName()));
                    receiver.sendMessage(Lang.replace("<mf-lang:top-player-record>", tagResolver));
                }
                else {
                    receiver.sendMessage(Lang.replace("<mf-lang:top-player-no-catch>"));
                }
            }
        }
    }

    public abstract void save();

    public void clear() {
        records.clear();
    }

    public void clearRecordHolder(@NotNull UUID holder) {
        records.removeIf(record -> holder.equals(record.fisher()));
    }

    @NotNull
    public List<FishRecord> getRecords() {
        return new ArrayList<>(records);
    }

    public void add(@NotNull FishRecord record) {
        records.add(record);
    }

    public boolean contains(@NotNull UUID uuid) {
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
