package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperArgumentCommand;
import io.musician101.musicommand.paper.command.PaperCommand;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.elsiff.morefish.command.argument.ConfigTypeArgumentType;
import me.elsiff.morefish.command.argument.ConfigTypeArgumentType.ConfigType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
class MFReload implements MFCommand, PaperLiteralCommand.AdventureFormat {

    private void execute(CommandSourceStack source, Runnable runnable) {
        try {
            runnable.run();
            sendMessage(source, Component.translatable("morefish.command.reload.success"));
        }
        catch (Exception e) {
            Component message = Component.translatable("morefish.command.reload.fail");
            getPlugin().getComponentLogger().error(message, e);
            sendMessage(source, message);
        }
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return Component.translatable("morefish.command.reload.description");
    }

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf reload");
    }

    @Override
    public boolean canUse(CommandSourceStack source) {
        return hasPermission(source, "morefish.admin");
    }

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        execute(context.getSource(), () -> getPlugin().applyConfig());
        return 1;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
        return List.of(new ConfigTypeArgument());
    }

    class ConfigTypeArgument implements PaperArgumentCommand.AdventureFormat<ConfigType> {

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) {
            MFReload.this.execute(context.getSource(), () -> {
                ConfigType type = ConfigTypeArgumentType.getConfigType(context, name());
                switch (type) {
                    case CONFIG -> getPlugin().applyMainConfig();
                    case FISH -> getPlugin().applyFishConfig();
                    case LANG -> getPlugin().applyLangConfig();
                }
            });
            return 1;
        }

        @Override
        public ArgumentType<ConfigType> type() {
            return new ConfigTypeArgumentType();
        }

        @Override
        public String name() {
            return "configType";
        }
    }
}
