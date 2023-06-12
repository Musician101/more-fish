package me.elsiff.morefish.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import me.elsiff.morefish.configuration.Lang;
import me.elsiff.morefish.shop.FishShopGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static me.elsiff.morefish.configuration.Lang.PREFIX;
import static me.elsiff.morefish.configuration.Lang.join;
import static net.kyori.adventure.text.Component.text;

public class MFShop extends MFCommand implements LiteralCommand {

    @Nonnull
    @Override
    public String name() {
        return "shop";
    }

    @Nonnull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new MFPlayer());
    }

    @Override
    public boolean canUse(@Nonnull CommandSender sender) {
        return sender.hasPermission("morefish.shop");
    }

    @Override
    public int execute(@Nonnull CommandContext<CommandSender> context) {
        return shop(context.getSource(), (Player) context.getSource());
    }

    class MFPlayer extends AbstractMFPlayer {

        @Override
        public int execute(@Nonnull CommandContext<CommandSender> context) {
            return shop(context.getSource(), context.getArgument("player", Player.class));
        }

        @Override
        public boolean canUse(@Nonnull CommandSender sender) {
            return testAdmin(sender);
        }
    }

    int shop(CommandSender sender, Player guiUser) {
        if (!getFishShop().getEnabled() || !getPlugin().getVault().hasEconomy()) {
            sender.sendMessage(Lang.SHOP_DISABLED);
        }
        else {
            new FishShopGui(guiUser, 1);
            if (!guiUser.getUniqueId().equals(((Player) sender).getUniqueId())) {
                sender.sendMessage(Lang.replace(join(PREFIX, text("Forced %s to open Shop GUI.")), Map.of("%s", guiUser.getName())));
            }
        }

        return 1;
    }
}
