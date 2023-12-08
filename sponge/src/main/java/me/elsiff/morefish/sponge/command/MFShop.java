package me.elsiff.morefish.sponge.command;

import java.util.List;
import java.util.Map;
import me.elsiff.morefish.sponge.shop.FishShopGui;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import static me.elsiff.morefish.common.configuration.Lang.SHOP_DISABLED;
import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.PREFIX;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.lang;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class MFShop extends MFCommand {

    private final Parameter.Value<ServerPlayer> player = Parameter.player().key("player").optional().build();

    @Override
    public CommandResult execute(@NotNull CommandContext context) {
        return context.one(this.player).map(player -> {
            if (!context.hasPermission("morefish.admin")) {
                return CommandResult.error(join(PREFIX, text("Permission denied.", RED)));
            }

            return shop(context, player);
        }).orElseGet(() -> {
            if (context.cause() instanceof ServerPlayer p) {
                return shop(context, p);
            }

            return CommandResult.error(join(PREFIX, text("Permission denied.", RED)));
        });
    }

    @NotNull
    @Override
    public String getName() {
        return "shop";
    }

    @Override
    public @NotNull List<Parameter> getParameters() {
        return List.of(player);
    }

    CommandResult shop(CommandContext context, ServerPlayer guiUser) {
        if (!getFishShop().getEnabled() || !getPlugin().getEconomyHooker().hasEconomy()) {
            context.sendMessage(SHOP_DISABLED);
        }
        else {
            new FishShopGui(guiUser, 1);
            if (!guiUser.uniqueId().equals(((ServerPlayer) context.cause()).uniqueId())) {
                context.sendMessage(lang().replace(join(PREFIX, text("Forced %s to open Shop GUI.")), Map.of("%s", guiUser.name())));
            }
        }

        return CommandResult.success();
    }
}
