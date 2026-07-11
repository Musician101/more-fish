package me.elsiff.morefish.records;

import me.elsiff.morefish.command.argument.SortArgumentType.SortType;
import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.fish.FishIcon;
import me.elsiff.morefish.fish.FishRarity;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.fish.LuckOfTheSeaModifier;
import me.elsiff.morefish.fish.PlayerAnnouncement;
import me.elsiff.morefish.fish.PlayerAnnouncement.Type;
import me.elsiff.morefish.fish.condition.BiomesCondition;
import me.elsiff.morefish.fish.condition.EnchantmentsCondition;
import me.elsiff.morefish.fish.condition.FishConditions;
import me.elsiff.morefish.fish.condition.LocationYCondition;
import me.elsiff.morefish.fish.condition.PotionEffectsCondition;
import me.elsiff.morefish.fish.condition.RainingCondition;
import me.elsiff.morefish.fish.condition.ThunderingCondition;
import me.elsiff.morefish.fish.condition.TimeCondition;
import me.elsiff.morefish.fish.condition.XpLevelCondition;
import me.elsiff.morefish.lang.ArgumentUtil;
import me.elsiff.morefish.serialize.fish.FishIconSerializer;
import me.elsiff.morefish.serialize.fish.FishRaritySerializer;
import me.elsiff.morefish.serialize.fish.ItemStackSerializer;
import me.elsiff.morefish.serialize.fish.LuckOfTheSeaModifierSerializer;
import me.elsiff.morefish.serialize.fish.LuckOfTheSeaModifierSerializer.ModifierTypeSerializer;
import me.elsiff.morefish.serialize.fish.NamespacedKeySerializer;
import me.elsiff.morefish.serialize.fish.PlayerAnnouncementSerializer;
import me.elsiff.morefish.serialize.fish.PlayerAnnouncementSerializer.PlayerAnnouncementTypeSerializer;
import me.elsiff.morefish.serialize.fish.TextColorSerializer;
import me.elsiff.morefish.serialize.fish.condition.BiomeConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.EnchantmentsConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.FishConditionsSerializer;
import me.elsiff.morefish.serialize.fish.condition.LocationYConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.PotionEffectsConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.RainingConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.ThunderingConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.TimeConditionSerializer;
import me.elsiff.morefish.serialize.fish.condition.XpLevelConditionSerializer;
import me.elsiff.morefish.serialize.record.FishRecordSerializer;
import me.elsiff.morefish.serialize.record.FishSerializer;
import me.elsiff.morefish.serialize.record.FishTypeRecordSerializer;
import me.elsiff.morefish.serialize.record.UUIDSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    protected final ConfigurationOptions options = ConfigurationOptions.defaults().serializers(b -> {
        b.register(FishRecord.class, new FishRecordSerializer());
        b.register(Fish.class, new FishSerializer());
        b.register(FishType.class, new FishTypeRecordSerializer());
        b.register(FishIcon.class, new FishIconSerializer());
        b.register(ItemStack.class, new ItemStackSerializer());
        b.register(FishRarity.class, new FishRaritySerializer());
        b.register(PlayerAnnouncement.class, new PlayerAnnouncementSerializer());
        b.register(Type.class, new PlayerAnnouncementTypeSerializer());
        b.register(FishConditions.class, new FishConditionsSerializer());
        b.register(BiomesCondition.class, new BiomeConditionSerializer());
        b.register(EnchantmentsCondition.class, new EnchantmentsConditionSerializer());
        b.register(LocationYCondition.class, new LocationYConditionSerializer());
        b.register(PotionEffectsCondition.class, new PotionEffectsConditionSerializer());
        b.register(RainingCondition.class, new RainingConditionSerializer());
        b.register(ThunderingCondition.class, new ThunderingConditionSerializer());
        b.register(TimeCondition.class, new TimeConditionSerializer());
        b.register(XpLevelCondition.class, new XpLevelConditionSerializer());
        b.register(NamespacedKey.class, new NamespacedKeySerializer());
        b.register(UUID.class, new UUIDSerializer());
        b.register(TextColor.class, new TextColorSerializer());
        b.register(LuckOfTheSeaModifier.class, new LuckOfTheSeaModifierSerializer());
        b.register(LuckOfTheSeaModifier.Type.class, new ModifierTypeSerializer());
    });

    protected YamlConfigurationLoader loader() {
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
