package me.elsiff.morefish.sponge.command;

import java.util.List;
import me.elsiff.morefish.sponge.command.argument.FishLengthParser;
import me.elsiff.morefish.sponge.command.argument.FishTypeArgumentType;
import me.elsiff.morefish.sponge.fishing.SpongeFishType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.server.ServerWorld;

import static me.elsiff.morefish.common.configuration.Lang.PREFIX;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static me.elsiff.morefish.sponge.item.FishItemStackConverter.createItemStack;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static org.spongepowered.api.command.parameter.Parameter.key;

public class MFGive extends MFCommand {

    public static final Parameter.Value<SpongeFishType> FISH_TYPE = Parameter.builder(key("fish", SpongeFishType.class)).addParser(new FishTypeArgumentType()).build();
    private static final Parameter.Value<Integer> AMOUNT = Parameter.rangedInteger(0, 64).key("amount").optional().build();
    private static final Parameter.Value<Double> LENGTH = Parameter.builder(key("length", Double.class)).addParser(new FishLengthParser()).optional().build();
    private static final Parameter.Value<ServerPlayer> PLAYER = Parameter.playerOrTarget().key("player").build();

    @Override
    public boolean canUse(@NotNull CommandContext context) {
        return testAdmin(context);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        return context.one(PLAYER).flatMap(player -> context.one(FISH_TYPE).map(fishType -> {
            double length = context.one(LENGTH).orElse(fishType.lengthMin());
            if (fishType.lengthMin() <= length && length <= fishType.lengthMax()) {
                int amount = context.one(AMOUNT).orElse(1);
                ItemStack itemStack = createItemStack(fishType.generateFish(), length, player);
                itemStack.setQuantity(amount);
                ServerWorld world = player.world();
                Item item = world.createEntity(EntityTypes.ITEM, player.position());
                item.offer(item.item().set(itemStack.createSnapshot()));
                world.spawnEntity(item);
                if (!(context.cause() instanceof ServerPlayer p && p.uniqueId().equals(player.uniqueId()))) {
                    context.sendMessage(join(PREFIX, text(fishType.displayName() + " given to " + player.name() + ".")));
                }

                player.sendMessage(join(PREFIX, text("You just received a " + fishType.displayName() + ".")));
                return CommandResult.success();
            }

            return CommandResult.error(join(PREFIX, text(length + " is outside the bounds of " + fishType.name(), RED)));
        })).orElse(CommandResult.error(join(PREFIX, text("That player is not online.", RED))));
    }

    @NotNull
    @Override
    public String getName() {
        return "give";
    }

    @Override
    public @NotNull List<Parameter> getParameters() {
        return List.of(PLAYER, FISH_TYPE, LENGTH, AMOUNT);
    }
}
