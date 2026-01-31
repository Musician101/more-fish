package me.elsiff.morefish.bags;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.NodePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.MoreFish.lang;

@NullMarked
public class FishBags implements Listener {

    private final List<FishBag> bags = new ArrayList<>();

    public boolean addFish(Player player, ItemStack itemStack) {
        FishBag fishBag = getFishBag(player);
        for (int i = 1; i < getMaxAllowedPages(player) + 1; i++) {
            if (fishBag.addFish(i, itemStack)) {
                return true;
            }
        }

        return false;
    }

    public List<ItemStack> getFish(Player player, int page) {
        return getFishBag(player).getFish(page);
    }

    public FishBag getFishBag(Player player) {
        Optional<FishBag> fishBag = bags.stream().filter(fb -> fb.getUUID().equals(player.getUniqueId())).findFirst();
        if (fishBag.isPresent()) {
            return fishBag.get();
        }

        FishBag fb = new FishBag(player.getUniqueId());
        bags.add(fb);
        return fb;
    }

    public int getMaxAllowedPages(Player player) {
        return getFishBag(player).getMaxAllowedPages();
    }

    private void load(Path path) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(path.toFile());
        UUID uuid = UUID.fromString(path.getFileName().toString().replace(".yml", ""));
        FishBag fishBag = new FishBag(uuid);
        fishBag.setMaxAllowedPages(yaml.getInt("max_allowed_pages", 0));
        ConfigurationSection fishSection = yaml.getConfigurationSection("fish");
        if (fishSection != null) {
            IntStream.range(1, fishBag.getMaxAllowedPages() + 1).forEach(page -> {
                ConfigurationSection pageSection = fishSection.getConfigurationSection(String.valueOf(page));
                if (pageSection == null) {
                    return;
                }

                //noinspection NullableProblems
                fishBag.updatePage(page, IntStream.range(0, 45).mapToObj(Integer::toString).map(pageSection::getItemStack).filter(Objects::nonNull).collect(Collectors.toList()));
            });
        }

        this.bags.add(fishBag);
    }

    public void load() {
        Path bagsFolder = getPlugin().getDataPath().resolve("fish_bags");
        try (Stream<Path> bags = Files.list(bagsFolder)) {
            bags.filter(path -> path.getFileName().toString().endsWith(".yml")).forEach(this::load);
        }
        catch (IOException e) {
            getPlugin().getComponentLogger().error(lang().getComponent(FISH_BAGS_PATH.withAppendedChild("load-error")), e);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FishBag fishBag = getFishBag(player);
        List<ItemStack> contraband = fishBag.getContraband();
        if (contraband.isEmpty()) {
            return;
        }

        Bukkit.getGlobalRegionScheduler().run(getPlugin(), task -> player.sendMessage(lang().getComponent("main", "contraband-alert")));
    }

    private void save(FishBag fishBag) {
        YamlConfiguration yaml = new YamlConfiguration();
        Path bagsFolder = getPlugin().getDataPath().resolve("fish_bags");
        UUID uuid = fishBag.getUUID();
        int maxAllowedPages = fishBag.getMaxAllowedPages();
        yaml.set("max_allowed_pages", maxAllowedPages);
        IntStream.range(1, maxAllowedPages + 1).forEach(page -> {
            List<ItemStack> fish = fishBag.getFish(page);
            IntStream.range(0, fish.size()).forEach(i -> yaml.set("fish." + page + "." + i, fish.get(i)));
        });

        try {
            yaml.save(bagsFolder.resolve(uuid + ".yml").toFile());
        }
        catch (IOException e) {
            getPlugin().getComponentLogger().error(lang().getComponent(FISH_BAGS_PATH.withAppendedChild("save-error"), Placeholder.parsed("uuid", uuid.toString())), e);
        }
    }

    private static final NodePath FISH_BAGS_PATH = NodePath.path("main", "fish-bags");

    public void save() {
        bags.forEach(this::save);
    }

    public void setMaxAllowedPages(Player player, int maxAllowedPages) {
        FishBag fishBag = getFishBag(player);
        if (!bags.contains(fishBag)) {
            bags.add(fishBag);
        }

        fishBag.setMaxAllowedPages(maxAllowedPages);
    }

    public void update(Player player, @Nullable ItemStack[] contents, int page) {
        FishBag fishBag = getFishBag(player);
        if (!bags.contains(fishBag)) {
            bags.add(fishBag);
        }

        //noinspection NullableProblems
        fishBag.updatePage(page, Arrays.stream(contents, 0, 45).filter(Objects::nonNull).collect(Collectors.toList()));
    }
}
