package me.elsiff.morefish.fishing.fishrecords;

import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.replace;
import static me.elsiff.morefish.text.Lang.tagResolver;
import static me.elsiff.morefish.util.NumberUtils.ordinalOf;
import static net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.resolver;

public abstract class FishRecordKeeper {

    @NotNull
    protected final List<FishRecord> records = new ArrayList<>();

    public void informAboutRanking(@NotNull CommandSender receiver) {
        informAboutRanking(receiver, records);
    }

    public void informAboutRanking(@NotNull CommandSender receiver, @NotNull List<FishRecord> fishRecords) {
        if (fishRecords.isEmpty()) {
            receiver.sendMessage(replace("<mf-lang:top-no-catches>"));
        }
        else {
            int topSize = getPlugin().getConfig().getInt("messages.top-number", 1);
            fishRecords.sort(SortType.LENGTH.reversed());
            List<FishRecord> top = fishRecords.subList(0, Math.min(topSize, fishRecords.size()));
            top.forEach(record -> {
                int number = top.indexOf(record) + 1;
                OfflinePlayer player = Bukkit.getOfflinePlayer(record.fisher());
                receiver.sendMessage(replace("<mf-lang:top-ranked-record>", resolver(tagResolver("player", player.getName() == null ? player.getUniqueId().toString() : player.getName()), tagResolver("ordinal", ordinalOf(number)), tagResolver("record-length", record.getLength()), tagResolver("record-fish-name", record.getFishName()))));
            });

            if (receiver instanceof Player player) {
                if (contains(player.getUniqueId())) {
                    Entry<Integer, FishRecord> entry = rankedRecordOf(player, fishRecords);
                    FishRecord record = entry.getValue();
                    receiver.sendMessage(replace("<mf-lang:top-player-record>", resolver(tagResolver("ordinal", ordinalOf(entry.getKey() + 1)), tagResolver("record-length", record.getLength()), tagResolver("record-fish-name", record.getFishName()))));
                }
                else {
                    receiver.sendMessage(replace("<mf-lang:top-player-no-catch>"));
                }
            }
        }
    }

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
}
