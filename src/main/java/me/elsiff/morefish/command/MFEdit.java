package me.elsiff.morefish.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.musician101.musicommand.paper.command.PaperCommand;
import io.musician101.musicommand.paper.command.PaperLiteralCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.dialog.Dialog;
import me.elsiff.morefish.editor.rarity.FishRaritiesDialog;
import me.elsiff.morefish.editor.type.FishTypesDialog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

@NullMarked
public class MFEdit implements MFCommand, PaperLiteralCommand.AdventureFormat {

    @Override
    public ComponentLike usage(CommandSourceStack source) {
        return Component.text("/mf edit [rarities|types]");
    }

    @Override
    public boolean canUse(CommandSourceStack source) {
        return isPlayerAndHasPermission(source, "mf.admin");
    }

    @Override
    public ComponentLike description(CommandSourceStack source) {
        return Component.translatable("morefish.command.edit.description");
    }

    @Override
    public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
        return List.of(new EditFish("rarities", l -> new FishRaritiesDialog(l).build()), new EditFish("types", l -> new FishTypesDialog(l).build()));
    }

    @Override
    public String name() {
        return "edit";
    }

    public class EditFish implements PaperLiteralCommand.AdventureFormat {

        private final String name;
        private final Function<Locale, Dialog> dialog;

        public EditFish(String name, Function<Locale, Dialog> dialog) {
            this.name = name;
            this.dialog = dialog;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) {
            Player player = getPlayer(context);
            player.showDialog(dialog.apply(player.locale()));
            return 1;
        }
    }
}
