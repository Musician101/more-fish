package io.musician101.morefish.common.fishing;

import io.musician101.musicianlibrary.java.configurate.ConfigurateLoader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FishBags<I> {

    @Nonnull
    private final List<FishBag<I>> bags = new ArrayList<>();
    @Nonnull
    private final File bagsFolder;
    @Nonnull
    private final String extension;
    @Nonnull
    private final Class<I> itemStackClass;
    @Nonnull
    private final ConfigurateLoader loader;
    @Nonnull
    private final TypeSerializerCollection typeSerializerCollection;

    public FishBags(@Nonnull File bagsFolder, @Nonnull ConfigurateLoader loader, @Nonnull String extension, @Nonnull TypeSerializerCollection typeSerializerCollection, @Nonnull Class<I> itemStackClass) {
        this.bagsFolder = bagsFolder;
        this.loader = loader;
        this.extension = extension;
        this.typeSerializerCollection = typeSerializerCollection;
        this.itemStackClass = itemStackClass;
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
                    ConfigurationNode node = loader.loader(file.toPath(), typeSerializerCollection).load();
                    UUID uuid = UUID.fromString(file.getName().replace(extension, ""));
                    FishBag<I> fishBag = new FishBag<>(uuid);
                    fishBag.setMaxAllowedPages(node.node("max_allowed_pages").getInt(0));
                    ConfigurationNode fishNode = node.node("fish");
                    if (!fishNode.empty()) {
                        IntStream.range(1, fishBag.getMaxAllowedPages() + 1).forEach(page -> {
                            ConfigurationNode pageNode = fishNode.node(page);
                            if (pageNode.empty()) {
                                return;
                            }

                            List<I> list = new ArrayList<>();
                            for (int i = 0; i < 45; i++) {
                                try {
                                    I itemStack = pageNode.node(i).get(itemStackClass);
                                    list.add(itemStack);
                                }
                                catch (SerializationException e) {
                                    e.printStackTrace();
                                }
                            }

                            fishBag.updatePage(page, list);
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

            try {
                ConfigurationNode node = BasicConfigurationNode.root();
                bagsFolder.mkdirs();
                UUID uuid = fishBag.getUUID();
                int maxAllowedPages = fishBag.getMaxAllowedPages();
                node.node("max_allowed_pages").set(maxAllowedPages);
                for (int page = 1; page < maxAllowedPages + 1; page++) {
                    List<I> fish = fishBag.getFish(page);
                    int bound = fish.size();
                    for (int i = 0; i < bound; i++) {
                        node.node("fish", page, i).set(fish.get(i));
                    }
                }
                loader.loader(Paths.get(bagsFolder.getPath(), uuid + extension), typeSerializerCollection).save(node);
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
