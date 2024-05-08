package me.elsiff.morefish.fishing;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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

public class FishBags implements Listener {

    @NotNull
    private final List<FishBag> bags = new ArrayList<>();

    public boolean addFish(@NotNull Player player, @NotNull ItemStack itemStack) {
        FishBag fishBag = getFishBag(player);
        for (int i = 1; i < getMaxAllowedPages(player) + 1; i++) {
            if (fishBag.addFish(i, itemStack)) {
                return true;
            }
        }

        return false;
    }

    @NotNull
    public List<ItemStack> getFish(@NotNull Player player, int page) {
        return getFishBag(player).getFish(page);
    }

    @NotNull
    public FishBag getFishBag(@NotNull Player player) {
        Optional<FishBag> fishBag = bags.stream().filter(fb -> fb.getUUID().equals(player.getUniqueId())).findFirst();
        if (fishBag.isPresent()) {
            return fishBag.get();
        }

        FishBag fb = new FishBag(player.getUniqueId());
        bags.add(fb);
        return fb;
    }

    public int getMaxAllowedPages(@NotNull Player player) {
        return getFishBag(player).getMaxAllowedPages();
    }

    public void load() {
        Bukkit.getAsyncScheduler().runNow(getPlugin(), task -> {
            Path bagsFolder = getPlugin().getDataFolder().toPath().resolve("fish_bags");
            try (Stream<Path> bags = Files.list(bagsFolder)) {
                bags.filter(path -> path.getFileName().toString().endsWith(".yml")).forEach(path -> {
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

                            fishBag.updatePage(page, IntStream.range(0, 45).mapToObj(Integer::toString).map(pageSection::getItemStack).filter(Objects::nonNull).collect(Collectors.toList()));
                        });
                    }

                    this.bags.add(fishBag);
                });
            }
            catch (IOException e) {
                getPlugin().getSLF4JLogger().error("An error occurred while loading fish bags.", e);
            }
        });
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FishBag fishBag = getFishBag(player);
        List<ItemStack> contraband = fishBag.getContraband();
        if (contraband.isEmpty()) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> player.sendMessage(Component.text("[MF] CONTRABAND DETECTED IN YOUR FISH BAG. CLICK THIS MESSAGE TO RETRIEVE IT NOW.").color(NamedTextColor.RED).clickEvent(ClickEvent.runCommand("/mf contraband"))));
    }

    @SuppressWarnings("StringConcatenationArgumentToLogCall")
    public void save() {
        Bukkit.getAsyncScheduler().runNow(getPlugin(), task -> {
            bags.forEach(fishBag -> {
                YamlConfiguration yaml = new YamlConfiguration();
                Path bagsFolder = getPlugin().getDataFolder().toPath().resolve("fish_bags");
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
                    getPlugin().getSLF4JLogger().error("An error occurred while saving fish bag for " + uuid + ".", e);
                }
            });
        });
    }

    public void setMaxAllowedPages(@NotNull Player player, int maxAllowedPages) {
        FishBag fishBag = getFishBag(player);
        if (!bags.contains(fishBag)) {
            bags.add(fishBag);
        }

        fishBag.setMaxAllowedPages(maxAllowedPages);
    }

    public void update(@NotNull Player player, ItemStack[] contents, int page) {
        FishBag fishBag = getFishBag(player);
        if (!bags.contains(fishBag)) {
            bags.add(fishBag);
        }

        fishBag.updatePage(page, Arrays.stream(contents, 0, 45).filter(Objects::nonNull).collect(Collectors.toList()));
    }
}
