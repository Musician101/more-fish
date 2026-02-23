package me.elsiff.morefish.command;

import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.elsiff.morefish.bags.FishBag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
class MFContraband implements MFCommand, PaperLiteralCommand.AdventureFormat {

    @Override
    public boolean canUse(CommandSourceStack source) {
        return isPlayer(source);
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return Component.translatable("morefish.command.contraband.description");
    }

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf contraband");
    }

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        Player player = getPlayer(context);
        FishBag fishBag = getPlugin().getFishBags().getFishBag(player);
        fishBag.getContraband().forEach(i -> {
            World world = player.getWorld();
            world.dropItem(player.getLocation(), i);
        });
        fishBag.clearContraband();
        player.sendMessage(Component.translatable("morefish.command.contraband.success"));
        return 1;
    }

    @Override
    public String name() {
        return "contraband";
    }
}
