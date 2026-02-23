package me.elsiff.morefish.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperCommand;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.elsiff.morefish.command.argument.PlayerArgumentType;
import me.elsiff.morefish.lang.ArgumentUtil;
import me.elsiff.morefish.shop.FishShopGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
class MFShop implements MFCommand, PaperLiteralCommand.AdventureFormat {

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return Component.translatable("morefish.command.shop.description");
    }

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf shop" + (hasPermission(source, "morefish.admin") ? " [<player>]" : ""));
    }

    @Override
    public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
        return List.of(new MFPlayer());
    }

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        shop(context.getSource(), getPlayer(context));
        return 1;
    }

    @Override
    public String name() {
        return "shop";
    }

    private void shop(CommandSourceStack source, Player guiUser) {
        if (!getPlugin().getConfig().getBoolean("fish-shop.enable") || !getPlugin().getVault().hasEconomy()) {
            sendMessage(source, Component.translatable("morefish.command.shop.disabled"));
        }
        else {
            new FishShopGui(guiUser);
            if (!guiUser.getUniqueId().equals(((Player) source.getSender()).getUniqueId())) {
                sendMessage(source, Component.translatable("morefish.command.shop.forced", ArgumentUtil.player(guiUser)));
            }
        }
    }

    class MFPlayer extends AbstractMFPlayer {

        @Override
        public boolean canUse(CommandSourceStack source) {
            return hasPermission(source, "morefish.admin");
        }

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) {
            shop(context.getSource(), PlayerArgumentType.getPlayer(context, name()));
            return 1;
        }
    }
}
