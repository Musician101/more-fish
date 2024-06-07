package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.musician101.bukkitier.command.ArgumentCommand;
import io.musician101.bukkitier.command.Command;
import io.musician101.bukkitier.command.LiteralCommand;
import me.elsiff.morefish.command.argument.FishLengthArgumentType;
import me.elsiff.morefish.command.argument.FishTypeArgumentType;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.item.FishItemStackConverter;
import me.elsiff.morefish.text.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class MFGive implements LiteralCommand {

    @SuppressWarnings("SameReturnValue")
    private static int giveFish(CommandContext<CommandSender> context, FishType fishType, double length, int amount) {
        Player player = context.getArgument("player", Player.class);
        ItemStack itemStack = FishItemStackConverter.createItemStack(fishType.generateFish(), length, player);
        itemStack.setAmount(amount);
        player.getWorld().dropItem(player.getLocation(), itemStack);
        CommandSender sender = context.getSource();
        if (!(sender instanceof Player p && p.getUniqueId().equals(player.getUniqueId()))) {
            context.getSource().sendMessage(Lang.replace("<mf-lang:command-give-sender>", Lang.tagResolver("fish-display-name", fishType.displayName())));
        }

        player.sendMessage(Lang.replace("<mf-lang:command-give-receiver>", Lang.tagResolver("fish-display-name", fishType.displayName())));
        return 1;
    }

    @NotNull
    @Override
    public String description(@NotNull CommandSender sender) {
        return Lang.raw("command-give-description");
    }

    @NotNull
    @Override
    public String usage(@NotNull CommandSender sender) {
        return "/mf give <player> <fish> [<length> [<amount>]]";
    }

    @NotNull
    @Override
    public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
        return List.of(new MFPlayer());
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender.hasPermission("morefish.admin");
    }

    @NotNull
    @Override
    public String name() {
        return "give";
    }

    static class MFAmount implements ArgumentCommand<Integer> {

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) throws CommandSyntaxException {
            FishType fishType = FishTypeArgumentType.get(context);
            return giveFish(context, fishType, FishLengthArgumentType.get(context, fishType), IntegerArgumentType.getInteger(context, "amount"));
        }

        @NotNull
        @Override
        public String name() {
            return "amount";
        }

        @NotNull
        @Override
        public ArgumentType<Integer> type() {
            return IntegerArgumentType.integer(0);
        }
    }

    static class MFFish implements ArgumentCommand<FishType> {

        @NotNull
        @Override
        public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
            return List.of(new MFLength());
        }

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) {
            FishType fishType = FishTypeArgumentType.get(context);
            return giveFish(context, fishType, fishType.lengthMin(), 1);
        }

        @NotNull
        @Override
        public String name() {
            return "fish";
        }

        @NotNull
        @Override
        public ArgumentType<FishType> type() {
            return new FishTypeArgumentType();
        }
    }

    static class MFLength implements ArgumentCommand<Double> {

        @NotNull
        @Override
        public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
            return List.of(new MFAmount());
        }

        @Override
        public int execute(@NotNull CommandContext<CommandSender> context) throws CommandSyntaxException {
            FishType fishType = FishTypeArgumentType.get(context);
            return giveFish(context, fishType, FishLengthArgumentType.get(context, fishType), 1);
        }

        @NotNull
        @Override
        public String name() {
            return "length";
        }

        @NotNull
        @Override
        public ArgumentType<Double> type() {
            return new FishLengthArgumentType();
        }
    }

    static class MFPlayer extends AbstractMFPlayer {

        @NotNull
        @Override
        public List<Command<? extends ArgumentBuilder<CommandSender, ?>>> arguments() {
            return List.of(new MFFish());
        }
    }
}
