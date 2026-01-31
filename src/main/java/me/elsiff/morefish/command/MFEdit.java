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
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Supplier;

import static me.elsiff.morefish.MoreFish.lang;

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
        return lang().getComponent("command", "edit", "description");
    }

    @Override
    public List<PaperCommand<? extends ArgumentBuilder<CommandSourceStack, ?>, ComponentLike>> children() {
        return List.of(new EditFish("rarities", () -> new FishRaritiesDialog().build()), new EditFish("types", () -> new FishTypesDialog().build()));
    }

    @Override
    public String name() {
        return "edit";
    }

    public class EditFish implements PaperLiteralCommand.AdventureFormat {

        private final String name;
        private final Supplier<Dialog> dialog;

        public EditFish(String name, Supplier<Dialog> dialog) {
            this.name = name;
            this.dialog = dialog;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Integer execute(CommandContext<CommandSourceStack> context) {
            getPlayer(context).showDialog(dialog.get());
            return 1;
        }
    }
}
