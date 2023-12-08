package me.elsiff.morefish.paper.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import java.util.List;
import java.util.Map;
import me.elsiff.morefish.paper.shop.FishShopGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.common.configuration.Lang.SHOP_DISABLED;
import static me.elsiff.morefish.paper.PaperMoreFish.getPlugin;
import static me.elsiff.morefish.paper.configuration.PaperLang.PREFIX;
import static me.elsiff.morefish.paper.configuration.PaperLang.join;
import static me.elsiff.morefish.paper.configuration.PaperLang.lang;
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

    int shop(CommandSender sender, Player guiUser) {
        if (!getFishShop().getEnabled() || !getPlugin().getVault().hasEconomy()) {
            sender.sendMessage(SHOP_DISABLED);
        }
        else {
            new FishShopGui(guiUser, 1);
            if (!guiUser.getUniqueId().equals(((Player) sender).getUniqueId())) {
                sender.sendMessage(lang().replace(join(PREFIX, text("Forced %s to open Shop GUI.")), Map.of("%s", guiUser.getName())));
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
