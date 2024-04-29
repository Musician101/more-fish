package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.fishing.FishBag;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.elsiff.morefish.MoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

class MFContraband implements LiteralCommand {

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return "Reclaim contraband in your fish bags.";
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
        player.sendMessage(text("[MF] All contraband has been dropped from your bag. Make sure you get it all.", GREEN));
        return 1;
    }

    @NotNull
    @Override
    public String name() {
        return "contraband";
    }
}
