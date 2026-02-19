package me.elsiff.morefish.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.core.command.CommandException;
import io.musician101.musicommand.paper.command.PaperArgumentCommand;
import io.musician101.musicommand.paper.command.PaperCommand;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.elsiff.morefish.command.argument.FishLengthArgumentType;
import me.elsiff.morefish.command.argument.FishTypeArgumentType;
import me.elsiff.morefish.command.argument.PlayerArgumentType;
import me.elsiff.morefish.fish.Fish;
import me.elsiff.morefish.fish.FishType;
import me.elsiff.morefish.lang.ArgumentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
class MFGive implements MFCommand, PaperLiteralCommand.AdventureFormat {

    private static void giveFish(CommandContext<CommandSourceStack> context, FishType fishType, double length, int amount) {
        Player player = PlayerArgumentType.getPlayer(context, "player");
        Fish fish = fishType.generateFish(length);
        ItemStack itemStack = fishType.icon().createItemStack(fish, player);
        itemStack.setAmount(amount);
        player.getWorld().dropItem(player.getLocation(), itemStack);
        CommandSourceStack source = context.getSource();
        if (!(source.getSender() instanceof Player p && p.getUniqueId().equals(player.getUniqueId()))) {
            Component message = Component.translatable("morefish.command.give.sender", fishType, ArgumentUtil.player(player));
            source.getSender().sendMessage(message);
        }

        Component message = Component.translatable("morefish.command.give.receiver", fishType);
        player.sendMessage(message);
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return Component.translatable("morefish.command.give.description");
    }

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf give <player> <fish> [<length> [<amount>]]");
    }

    @Override
    public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
        return List.of(new MFPlayer());
    }

    @Override
    public boolean canUse(CommandSourceStack source) {
        return hasPermission(source, "morefish.admin");
    }

    @Override
    public String name() {
        return "give";
    }

    static class MFAmount implements PaperArgumentCommand.AdventureFormat<Integer> {

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) throws CommandException {
            FishType fishType = FishTypeArgumentType.getFishType(context);
            giveFish(context, fishType, FishLengthArgumentType.getLength(context, fishType), IntegerArgumentType.getInteger(context, "amount"));
            return 1;
        }

        @Override
        public String name() {
            return "amount";
        }

        @Override
        public ArgumentType<Integer> type() {
            return IntegerArgumentType.integer(0);
        }
    }

    static class MFFish implements PaperArgumentCommand.AdventureFormat<FishType> {

        @Override
        public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
            return List.of(new MFLength());
        }

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) {
            FishType fishType = FishTypeArgumentType.getFishType(context);
            giveFish(context, fishType, fishType.minLength(), 1);
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
    }

    static class MFLength implements PaperArgumentCommand.AdventureFormat<Float> {

        @Override
        public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
            return List.of(new MFAmount());
        }

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) throws CommandException {
            FishType fishType = FishTypeArgumentType.getFishType(context);
            giveFish(context, fishType, FishLengthArgumentType.getLength(context, fishType), 1);
            return 1;
        }

        @Override
        public String name() {
            return "length";
        }

        @Override
        public ArgumentType<Float> type() {
            return new FishLengthArgumentType();
        }
    }

    static class MFPlayer extends AbstractMFPlayer {

        @Override
        public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
            return List.of(new MFFish());
        }
    }
}
