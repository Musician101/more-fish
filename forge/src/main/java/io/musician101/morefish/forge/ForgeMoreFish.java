package io.musician101.morefish.forge;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.musician101.morefish.common.Reference;
import io.musician101.morefish.common.config.AutoRunningConfig;
import io.musician101.morefish.common.config.Config;
import io.musician101.morefish.common.fishing.FishBags;
import io.musician101.morefish.common.fishing.Records;
import io.musician101.morefish.common.fishing.competition.FishingCompetition;
import io.musician101.morefish.forge.command.MoreFishCommand;
import io.musician101.morefish.forge.config.ForgeConfig;
import io.musician101.morefish.forge.fishing.FishingListener;
import io.musician101.morefish.forge.fishing.catchhandler.ForgeCatchBroadcaster;
import io.musician101.morefish.forge.fishing.catchhandler.ForgeCatchHandler;
import io.musician101.morefish.forge.fishing.catchhandler.ForgeCompetitionRecordAdder;
import io.musician101.morefish.forge.fishing.catchhandler.ForgeNewFirstBroadcaster;
import io.musician101.morefish.forge.fishing.competition.FishingCompetitionAutoRunner;
import io.musician101.morefish.forge.fishing.competition.FishingCompetitionHost;
import io.musician101.morefish.forge.item.FishCoinItem;
import io.musician101.morefish.forge.item.FishItemStackConverter;
import io.musician101.musicianlibrary.java.configurate.ConfigurateLoader;
import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.ID)
public class ForgeMoreFish {

    @Nonnull
    private final FishingCompetitionAutoRunner autoRunner = new FishingCompetitionAutoRunner();
    @Nonnull
    private final FishingCompetitionHost competitionHost = new FishingCompetitionHost();
    @Nonnull
    private final FishItemStackConverter converter = new FishItemStackConverter();
    @Nonnull
    private final FishBags<ItemStack> fishBags = new FishBags<>(new File("config/" + Reference.ID + "/fish_bags"), ConfigurateLoader.TOML, ".toml", node -> {
        try {
            return ItemStack.read(new JsonToNBT(new StringReader(node.getString())).readStruct());
        }
        catch (CommandSyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    });
    @Nonnull
    private final List<ForgeCatchHandler> globalCatchHandlers = Arrays.asList(new ForgeCatchBroadcaster(), new ForgeNewFirstBroadcaster(), new ForgeCompetitionRecordAdder());
    private final Logger logger = LogManager.getLogger(Reference.ID);
    @Nonnull
    private final Config config = ForgeConfig.defaultConfig();
    @Nonnull
    private final FishingCompetition<ItemStack> competition = new FishingCompetition(new Records(Paths.get("config", Reference.ID, "records").toFile(), ConfigurateLoader.YAML, () -> config.getFishConfig().getTypes().stream()));

    public ForgeMoreFish() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ForgeMoreFish getInstance() {
        return ModList.get().getModObjectById(Reference.ID).filter(ForgeMoreFish.class::isInstance).map(ForgeMoreFish.class::cast).orElseThrow(() -> new IllegalStateException(Reference.NAME + " is not enabled!"));
    }

    public final void applyConfig() {
        config.reload();
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        server.getPlayerList().func_232641_a_(new StringTextComponent("[MoreFish]").mergeStyle(TextFormatting.AQUA).append(new StringTextComponent(" The config is being updated. To prevent issues, the window has been closed.").mergeStyle(TextFormatting.RESET)), ChatType.CHAT, Util.DUMMY_UUID);
        server.getPlayerList().getPlayers().forEach(player -> {
            //TODO send packet to all players that closes the client GUIs
        });
        if (autoRunner.isEnabled()) {
            autoRunner.disable();
        }

        AutoRunningConfig autoRunningConfig = config.getAutoRunningConfig();
        if (autoRunningConfig.isEnabled()) {
            autoRunner.setScheduledTimes(autoRunningConfig.getStartTimes());
            autoRunner.enable();
        }
    }

    private void doClientStuff(FMLClientSetupEvent event) {
        //TODO keep empty method until it's determined not to be needed.
    }

    @Nonnull
    public final FishingCompetitionAutoRunner getAutoRunner() {
        return autoRunner;
    }

    @Nonnull
    public final FishingCompetition<ItemStack> getCompetition() {
        return competition;
    }

    @Nonnull
    public final FishingCompetitionHost getCompetitionHost() {
        return competitionHost;
    }

    @Nonnull
    public final FishItemStackConverter getConverter() {
        return converter;
    }

    @Nonnull
    public FishBags<ItemStack> getFishBags() {
        return fishBags;
    }

    @Nonnull
    public final List<ForgeCatchHandler> getGlobalCatchHandlers() {
        return globalCatchHandlers;
    }

    public Logger getLogger() {
        return logger;
    }

    public Config getPluginConfig() {
        return config;
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        fishBags.load();
        applyConfig();
        MinecraftForge.EVENT_BUS.register(new FishingListener());
        //MinecraftForge.EVENT_BUS.register(new FishShopSignListener());
        MoreFishCommand.init();
        getLogger().info("Plugin has been enabled.");
        if (config.autoStart()) {
            competitionHost.openCompetition();
        }
    }

    @SubscribeEvent
    public void registerItems(Register<Item> event) {
        event.getRegistry().register(FishCoinItem.FISH_COIN_ITEM);
    }
}
