package me.elsiff.morefish.paper.fishing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import me.elsiff.morefish.common.fishing.FishBags;
import me.elsiff.morefish.paper.PaperMoreFish;
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

import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;

public class PaperFishBags extends FishBags<PaperFishBag, ItemStack> implements Listener {

    @Override
    public void load() {
        Path bagsFolder = getPlugin().getDataFolder().toPath().resolve("fish_bags");
        try {
            if (Files.notExists(bagsFolder)) {
                Files.createDirectories(bagsFolder);
            }

            try (Stream<Path> fishBagFiles = Files.list(bagsFolder)) {
                fishBagFiles.filter(file -> file.getFileName().toString().endsWith(".yml")).forEach(file -> {
                    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file.toFile());
                    UUID uuid = UUID.fromString(file.getFileName().toString().replace(".yml", ""));
                    PaperFishBag fishBag = new PaperFishBag(uuid);
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

                    bags.add(fishBag);
                });
            }
        }
        catch (IOException e) {
            getPlugin().getSLF4JLogger().error("Failed to read fish bag files.", e);
        }
    }

    @Override
    protected PaperFishBag newFishBag(@NotNull UUID player) {
        return new PaperFishBag(player);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PaperFishBag fishBag = getFishBag(player.getUniqueId());
        List<ItemStack> contraband = fishBag.getContraband();
        if (contraband.isEmpty()) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> player.sendMessage(Component.text("[MF] CONTRABAND DETECTED IN YOUR FISH BAG. CLICK THIS MESSAGE TO RETRIEVE IT NOW.").color(NamedTextColor.RED).clickEvent(ClickEvent.runCommand("/mf contraband"))));
    }

    public void save() {
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
                PaperMoreFish.getPlugin().getSLF4JLogger().error("An error occurred while saving fish bag for " + uuid + ".", e);
            }
        });
    }
}
