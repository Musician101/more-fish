package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.fishing.FishBag;
import me.elsiff.morefish.text.Lang;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;

class MFContraband implements LiteralCommand {

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return Lang.raw("command-contraband-description");
    }

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf contraband";
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        Player player = (Player) context.getSource();
        FishBag fishBag = getPlugin().getFishBags().getFishBag(player);
        fishBag.getContraband().forEach(i -> {
            World world = player.getWorld();
            world.dropItem(player.getLocation(), i);
        });
        fishBag.clearContraband();
        player.sendMessage(Lang.replace("command-contraband-success"));
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "contraband";
    }
}
