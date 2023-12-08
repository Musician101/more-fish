package me.elsiff.morefish.sponge.fishing;

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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;

public class SpongeFishBags extends FishBags<SpongeFishBag, ItemStack> {

    @Override
    public void load() {
        Path bagsFolder = getPlugin().getConfigDir().resolve("fish_bags");
        try {
            if (Files.notExists(bagsFolder)) {
                Files.createDirectories(bagsFolder);
            }

            try (Stream<Path> fishBagFiles = Files.list(bagsFolder)) {
                fishBagFiles.filter(file -> file.getFileName().toString().endsWith(".yml")).forEach(file -> {
                    try {
                        ConfigurationNode yaml = YamlConfigurationLoader.builder().file(file.toFile()).build().load();
                        UUID uuid = UUID.fromString(file.getFileName().toString().replace(".yml", ""));
                        SpongeFishBag fishBag = new SpongeFishBag(uuid);
                        fishBag.setMaxAllowedPages(yaml.node("max_allowed_pages").getInt(0));
                        ConfigurationNode fishSection = yaml.node("fish");
                        if (fishSection != null) {
                            IntStream.range(1, fishBag.getMaxAllowedPages() + 1).forEach(page -> {
                                ConfigurationNode pageSection = fishSection.node(String.valueOf(page));
                                if (pageSection == null) {
                                    return;
                                }

                                fishBag.updatePage(page, IntStream.range(0, 45).mapToObj(Integer::toString).map(s -> {
                                    ConfigurationNode itemStack = pageSection.node(s);
                                    try {
                                        String string = HoconConfigurationLoader.builder().buildAndSaveString(itemStack);
                                        return ItemStack.builder().fromContainer(DataFormats.HOCON.get().read(string)).build();
                                    }
                                    catch (IOException e) {
                                        getPlugin().getLogger().error("Failed to read fish bag files.", e);
                                        return null;
                                    }
                                }).filter(Objects::nonNull).collect(Collectors.toList()));
                            });
                        }

                        bags.add(fishBag);
                    }
                    catch (ConfigurateException e) {
                        getPlugin().getLogger().error("Failed to read fish bag files.", e);
                    }
                });
            }
        }
        catch (IOException e) {
            getPlugin().getLogger().error("Failed to read fish bag files.", e);
        }
    }

    @Override
    protected SpongeFishBag newFishBag(@NotNull UUID player) {
        return new SpongeFishBag(player);
    }

    @Listener
    public void onJoin(@NotNull ServerSideConnectionEvent.Join event) {
        ServerPlayer player = event.player();
        SpongeFishBag fishBag = getFishBag(player.uniqueId());
        List<ItemStack> contraband = fishBag.getContraband();
        if (contraband.isEmpty()) {
            return;
        }

        Sponge.asyncScheduler().submit(Task.builder().plugin(getPlugin().getPluginContainer()).execute(() -> player.sendMessage(Component.text("[MF] CONTRABAND DETECTED IN YOUR FISH BAG. CLICK THIS MESSAGE TO RETRIEVE IT NOW.").color(NamedTextColor.RED).clickEvent(ClickEvent.runCommand("/mf contraband")))).delay(Ticks.zero()).build());
    }

    public void save() {
        bags.forEach(fishBag -> {
            ConfigurationNode node = BasicConfigurationNode.root();
            Path bagsFolder = getPlugin().getConfigDir().resolve("fish_bags");
            UUID uuid = fishBag.getUUID();
            int maxAllowedPages = fishBag.getMaxAllowedPages();

            try {
                node.node("max_allowed_pages").set(maxAllowedPages);
                IntStream.range(1, maxAllowedPages + 1).forEach(page -> {
                    List<ItemStack> fish = fishBag.getFish(page);
                    IntStream.range(0, fish.size()).forEach(i -> {
                        try {
                            ItemStack itemStack = fish.get(i);
                            String s = DataFormats.HOCON.get().write(itemStack.toContainer());
                            ConfigurationNode cn = HoconConfigurationLoader.builder().buildAndLoadString(s);
                            node.node("fish." + page + "." + i).set(cn);
                        }
                        catch (IOException e) {
                            getPlugin().getLogger().error("An error occurred while saving fish bag for " + uuid + ".", e);
                        }
                    });
                });
                YamlConfigurationLoader.builder().file(bagsFolder.resolve(uuid + ".yml").toFile()).build().save(node);
            }
            catch (IOException e) {
                getPlugin().getLogger().error("An error occurred while saving fish bag for " + uuid + ".", e);
            }
        });
    }
}
