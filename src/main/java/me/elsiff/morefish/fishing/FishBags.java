package me.elsiff.morefish.fishing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
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

public class FishBags implements Listener {

    @Nonnull
    private final List<FishBag> bags = new ArrayList<>();

    public boolean addFish(@Nonnull Player player, @Nonnull ItemStack itemStack) {
        FishBag fishBag = getFishBag(player);
        for (int i = 1; i < getMaxAllowedPages(player) + 1; i++) {
            if (fishBag.addFish(i, itemStack)) {
                return true;
            }
        }

        return false;
    }

    @Nonnull
    public List<ItemStack> getFish(@Nonnull Player player, int page) {
        return getFishBag(player).getFish(page);
    }

    @Nonnull
    public FishBag getFishBag(@Nonnull Player player) {
        Optional<FishBag> fishBag = bags.stream().filter(fb -> fb.getUUID().equals(player.getUniqueId())).findFirst();
        if (fishBag.isPresent()) {
            return fishBag.get();
        }

        FishBag fb = new FishBag(player.getUniqueId());
        bags.add(fb);
        return fb;
    }

    @EventHandler
    public void onJoin(@Nonnull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FishBag fishBag = getFishBag(player);
        List<ItemStack> contraband = fishBag.getContraband();
        if (contraband.isEmpty()) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(MoreFish.instance(), () -> player.sendMessage(Component.text("[MF] CONTRABAND DETECTED IN YOUR FISH BAG. CLICK THIS MESSAGE TO RETRIEVE IT NOW.").color(NamedTextColor.RED).clickEvent(ClickEvent.runCommand("/mf contraband"))));
    }

    public int getMaxAllowedPages(@Nonnull Player player) {
        return getFishBag(player).getMaxAllowedPages();
    }

    public void load() {
        File bagsFolder = new File(MoreFish.instance().getDataFolder(), "fish_bags");
        bagsFolder.mkdirs();
        File[] files = bagsFolder.listFiles();
        if (files != null) {
            Stream.of(files).filter(file -> file.getName().endsWith(".yml")).forEach(file -> {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
                FishBag fishBag = new FishBag(uuid);
                fishBag.setMaxAllowedPages(yaml.getInt("max_allowed_pages", 0));
                ConfigurationSection fishSection = yaml.getConfigurationSection("fish");
                if (fishSection != null) {
                    IntStream.range(1, fishBag.getMaxAllowedPages() + 1).forEach(page -> {
                        ConfigurationSection pageSection = fishSection.getConfigurationSection(page + "");
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

    public void save() {
        bags.forEach(fishBag -> {
            YamlConfiguration yaml = new YamlConfiguration();
            File bagsFolder = new File(MoreFish.instance().getDataFolder(), "fish_bags");
            bagsFolder.mkdirs();
            UUID uuid = fishBag.getUUID();
            int maxAllowedPages = fishBag.getMaxAllowedPages();
            yaml.set("max_allowed_pages", maxAllowedPages);
            IntStream.range(1, maxAllowedPages + 1).forEach(page -> {
                List<ItemStack> fish = fishBag.getFish(page);
                IntStream.range(0, fish.size()).forEach(i -> yaml.set("fish." + page + "." + i, fish.get(i)));
            });

            try {
                yaml.save(new File(bagsFolder, uuid + ".yml"));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setMaxAllowedPages(@Nonnull Player player, int maxAllowedPages) {
        FishBag fishBag = getFishBag(player);
        if (!bags.contains(fishBag)) {
            bags.add(fishBag);
        }

        fishBag.setMaxAllowedPages(maxAllowedPages);
    }

    public void update(@Nonnull Player player, ItemStack[] contents, int page) {
        FishBag fishBag = getFishBag(player);
        if (!bags.contains(fishBag)) {
            bags.add(fishBag);
        }

        fishBag.updatePage(page, Arrays.stream(contents, 0, 45).filter(Objects::nonNull).collect(Collectors.toList()));
    }
}
