package me.elsiff.morefish.sponge.command;

import me.elsiff.morefish.sponge.fishing.SpongeFishBag;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerWorld;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

class MFContraband extends MFCommand {

    @Override
    public boolean canUse(@NotNull CommandContext context) {
        return context.cause() instanceof ServerPlayer;
    }

    @Override
    public CommandResult execute(@NotNull CommandContext context) {
        ServerPlayer player = (ServerPlayer) context.cause();
        SpongeFishBag fishBag = getPlugin().getFishBags().getFishBag(player.uniqueId());
        fishBag.getContraband().forEach(i -> {
            ServerWorld world = player.world();
            Item item = world.createEntity(EntityTypes.ITEM, player.position());
            item.offer(item.item().set(i.createSnapshot()));
            world.spawnEntity(item);
        });
        fishBag.clearContraband();
        player.sendMessage(text("[MF] All contraband has been dropped from your bag. Make sure you get it all.", GREEN));
        return CommandResult.success();
    }

    @NotNull
    @Override
    public String getName() {
        return "contraband";
    }
}
