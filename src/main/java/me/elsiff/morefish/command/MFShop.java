package me.elsiff.morefish.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.shop.FishShopGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.playerName;
import static me.elsiff.morefish.text.Lang.raw;
import static me.elsiff.morefish.text.Lang.replace;

class MFShop implements LiteralCommand {

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return raw("command-shop-description");
    }

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf shop" + (sender.hasPermission("morefish.admin") ? " [<player>]" : "");
    }

    @NotNull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new MFPlayer());
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        shop(context.getSource(), (Player) context.getSource());
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "shop";
    }

    void shop(CommandSender sender, Player guiUser) {
        if (!getPlugin().getConfig().getBoolean("fish-shop.enable") || !getPlugin().getVault().hasEconomy()) {
            sender.sendMessage(replace("<mf-lang:command-shop-disabled>"));
        }
        else {
            new FishShopGui(guiUser, 1);
            if (!guiUser.getUniqueId().equals(((Player) sender).getUniqueId())) {
                sender.sendMessage(replace("<mf-lang:command-shop-forced>", playerName(guiUser)));
            }
        }
    }

    class MFPlayer extends AbstractMFPlayer {

        @Override
        public boolean canUse(@NotNull CommandSender sender) {
            return sender.hasPermission("morefish.admin");
        }

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            shop(context.getSource(), context.getArgument("player", Player.class));
            return 1;
        }
    }
}
