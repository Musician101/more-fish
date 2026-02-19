package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperArgumentCommand;
import io.musician101.musicommand.paper.command.PaperCommand;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.elsiff.morefish.command.argument.FishTypeArgumentType;
import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.fish.FishType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public class MFSimulate implements MFCommand, PaperLiteralCommand.AdventureFormat {

    @Override
    public boolean canUse(CommandSourceStack source) {
        return isPlayerAndHasPermission(source, "morefish.admin");
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return Component.translatable("morefish.command.simulate.description");
    }

    @Override
    public Integer execute(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        ItemStack fishingRod = player.getInventory().getItemInMainHand();
        if (fishingRod.getType() != Material.FISHING_ROD) {
            player.sendMessage(Component.translatable("morefish.command.simulate.no-rod"));
            return 0;
        }

        Item item = player.getWorld().spawn(player.getLocation(), Item.class, i -> i.setItemStack(new ItemStack(Material.DIRT)));
        getPlugin().types().caughtFish(item, player);
        return 1;
    }

    @Override
    public String name() {
        return "simulate";
    }

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf simulate [<fish>]");
    }

    @Override
    public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
        return List.of(new PaperArgumentCommand.AdventureFormat<FishType>() {

            @Override
            public Integer execute(CommandContext<CommandSourceStack> context) {
                Player player = (Player) context.getSource().getSender();
                FishType fishType = FishTypeArgumentType.getFishType(context);
                Fish fish = fishType.generateFish();
                Item item = player.getWorld().spawn(player.getLocation(), Item.class, i -> i.setItemStack(fish.type().icon().createItemStack(fish, player)));
                fishType.caught(item, player);
                return 1;
            }

            @Override
            public String name() {
                return "fish";
            }

            @Override
            public ArgumentType<FishType> type() {
                return new FishTypeArgumentType();
            }
        });
    }
}
