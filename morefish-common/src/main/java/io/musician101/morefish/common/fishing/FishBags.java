package io.musician101.morefish.common.fishing;

import io.musician101.morefish.common.ConfigurateLoader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;

public class FishBags<I> {

    @Nonnull
    private final List<FishBag<I>> bags = new ArrayList<>();
    @Nonnull
    private final File bagsFolder;
    @Nonnull
    private final String extension;
    @Nonnull
    private final Function<ConfigurationNode, I> itemStackLoader;
    @Nonnull
    private final ConfigurateLoader loader;

    public FishBags(@Nonnull File bagsFolder, @Nonnull ConfigurateLoader loader, @Nonnull String extension, @Nonnull Function<ConfigurationNode, I> itemStackLoader) {
        this.bagsFolder = bagsFolder;
        this.loader = loader;
        this.extension = extension;
        this.itemStackLoader = itemStackLoader;
    }

    public boolean addFish(@Nonnull UUID player, @Nonnull I itemStack) {
        FishBag<I> fishBag = getFishBag(player);
        for (int i = 1; i < getMaxAllowedPages(player) + 1; i++) {
            if (fishBag.addFish(i, itemStack)) {
                return true;
            }
        }

        return false;
    }

    @Nonnull
    public List<I> getFish(@Nonnull UUID player, int page) {
        return getFishBag(player).getFish(page);
    }

    @Nonnull
    public FishBag<I> getFishBag(@Nonnull UUID player) {
        Optional<FishBag<I>> fishBag = bags.stream().filter(fb -> fb.getUUID().equals(player)).findFirst();
        if (fishBag.isPresent()) {
            return fishBag.get();
        }

        FishBag<I> fb = new FishBag<>(player);
        bags.add(fb);
        return fb;
    }

    public int getMaxAllowedPages(@Nonnull UUID player) {
        return getFishBag(player).getMaxAllowedPages();
    }

    public void load() {
        bagsFolder.mkdirs();
        File[] files = bagsFolder.listFiles();
        if (files != null) {
            Stream.of(files).filter(file -> file.getName().endsWith(extension)).forEach(file -> {
                try {
                    ConfigurationNode node = loader.get(file.toPath()).load();
                    UUID uuid = UUID.fromString(file.getName().replace(extension, ""));
                    FishBag<I> fishBag = new FishBag<>(uuid);
                    fishBag.setMaxAllowedPages(node.getNode("max_allowed_pages").getInt(0));
                    ConfigurationNode fishNode = node.getNode("fish");
                    if (!fishNode.isVirtual()) {
                        IntStream.range(1, fishBag.getMaxAllowedPages() + 1).forEach(page -> {
                            ConfigurationNode pageNode = fishNode.getNode(page);
                            if (pageNode.isVirtual()) {
                                return;
                            }

                            fishBag.updatePage(page, IntStream.range(0, 45).mapToObj(pageNode::getNode).filter(configurationNode -> !configurationNode.isVirtual()).map(itemStackLoader).collect(Collectors.toList()));
                        });
                    }

                    bags.add(fishBag);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void save() {
        bags.forEach(fishBag -> {
            ConfigurationNode node = SimpleConfigurationNode.root();
            bagsFolder.mkdirs();
            UUID uuid = fishBag.getUUID();
            int maxAllowedPages = fishBag.getMaxAllowedPages();
            node.getNode("max_allowed_pages").setValue(maxAllowedPages);
            IntStream.range(1, maxAllowedPages + 1).forEach(page -> {
                List<I> fish = fishBag.getFish(page);
                IntStream.range(0, fish.size()).forEach(i -> node.getNode("fish", page, i).setValue(fish.get(i)));
            });

            try {
                loader.get(Paths.get(bagsFolder.getPath(), uuid.toString() + extension)).save(node);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setMaxAllowedPages(@Nonnull UUID player, int maxAllowedPages) {
        FishBag<I> fishBag = getFishBag(player);
        if (!bags.contains(fishBag)) {
            bags.add(fishBag);
        }

        fishBag.setMaxAllowedPages(maxAllowedPages);
    }

    public void update(@Nonnull UUID player, I[] contents, int page) {
        FishBag<I> fishBag = getFishBag(player);
        if (!bags.contains(fishBag)) {
            bags.add(fishBag);
        }

        fishBag.updatePage(page, Arrays.stream(contents, 0, 45).filter(Objects::nonNull).collect(Collectors.toList()));
    }
}
