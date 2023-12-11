package me.elsiff.morefish.sponge.command;

import io.musician101.spongecmd.help.HelpSubCMD;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.plugin.metadata.PluginMetadata;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;
import static me.elsiff.morefish.sponge.configuration.SpongeLang.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.Style.style;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

class MFHelp extends HelpSubCMD {

    public MFHelp(@NotNull MFMain main) {
        super(main, getPlugin().getPluginContainer());
    }

    @NotNull
    @Override
    public String getName() {
        return "help";
    }

    @Override
    protected @NotNull Component header() {
        PluginMetadata pmd = getPlugin().getPluginContainer().metadata();
        String pluginName = pmd.name().orElse(pmd.id());
        return join(text("> ===== ", DARK_AQUA), text(pluginName + ' ', style(AQUA, BOLD)), text('v' + pmd.version().toString(), AQUA), text(" ===== <", DARK_AQUA));
    }
}
