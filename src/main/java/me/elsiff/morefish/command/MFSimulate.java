package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.command.argument.FishTypeArgumentType;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.text.Lang;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.elsiff.morefish.MoreFish.getPlugin;

public class MFSimulate implements LiteralCommand {

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("morefish.admin") && sender instanceof Player;
    }

    @Override
    public @NotNull String description(@NotNull CommandSender sender) {
        return Lang.raw("command-simulate-description");
    }

    @Override
    public int execute(@NotNull CommandContext<CommandSender> context) {
        Player player = (Player) context.getSource();
        ItemStack fishingRod = player.getInventory().getItemInMainHand();
        if (fishingRod.getType() != Material.FISHING_ROD) {
            player.sendMessage(Lang.replace("<mf-lang:command-simulate-no-rod>"));
            return 0;
        }

        Item item = player.getWorld().spawn(player.getLocation(), Item.class, i -> i.setItemStack(new ItemStack(Material.DIRT)));
        getPlugin().getFishTypeTable().caughtFish(item, player, false);
        return 1;
    }

    @Override
    public @NotNull String name() {
        return "simulate";
    }

    @Override
    public @NotNull String usage(@NotNull CommandSender sender) {
        return "/mf simulate [<fish>]";
    }

    @Override
    public @NotNull List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new ArgumentCommand<FishType>() {

            @Override
            public int execute(@NotNull CommandContext<CommandSender> context) {
                Player player = (Player) context.getSource();
                FishType fishType = FishTypeArgumentType.get(context);
                getPlugin().getFishTypeTable().simulateCatch(player, fishType);
                return 1;
            }

            @Override
            public @NotNull String name() {
                return "fish";
            }

            @Override
            public @NotNull ArgumentType<FishType> type() {
                return new FishTypeArgumentType();
            }
        });
    }
}
