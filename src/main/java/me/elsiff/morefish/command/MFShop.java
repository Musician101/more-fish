package me.elsiff.morefish.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.shop.FishShopGui;
import me.elsiff.morefish.text.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.text.Lang.PREFIX_COMPONENT;
import static me.elsiff.morefish.text.Lang.join;
import static net.kyori.adventure.text.Component.text;

public class MFShop extends MFCommand implements LiteralCommand {

    @NotNull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new MFPlayer());
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("morefish.shop");
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        return shop(context.getSource(), (Player) context.getSource());
    }

    @NotNull
    @Override
    public String name() {
        return "shop";
    }

    @SuppressWarnings("SameReturnValue")
    int shop(CommandSender sender, Player guiUser) {
        if (!getFishShop().getEnabled() || !getPlugin().getVault().hasEconomy()) {
            sender.sendMessage(Lang.SHOP_DISABLED);
        }
        else {
            new FishShopGui(guiUser, 1);
            if (!guiUser.getUniqueId().equals(((Player) sender).getUniqueId())) {
                sender.sendMessage(join(PREFIX_COMPONENT, text("Forced " + guiUser.getName() + " to open Shop GUI.")));
            }
        }

        return 1;
    }

    class MFPlayer extends AbstractMFPlayer {

        @Override
        public boolean canUse(@NotNull CommandSender sender) {
            return testAdmin(sender);
        }

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            return shop(context.getSource(), context.getArgument("player", Player.class));
        }
    }
}
