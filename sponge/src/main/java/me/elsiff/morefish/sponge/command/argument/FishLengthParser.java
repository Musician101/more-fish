package me.elsiff.morefish.sponge.command.argument;

import java.util.List;
import java.util.Optional;
import me.elsiff.morefish.sponge.command.MFGive;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.CommandContext.Builder;
import org.spongepowered.api.command.parameter.Parameter.Key;
import org.spongepowered.api.command.parameter.managed.ValueParameter;

import static net.kyori.adventure.text.Component.text;
import static org.spongepowered.api.command.CommandCompletion.of;

public class FishLengthParser implements ValueParameter<Double> {

    @Override
    public List<CommandCompletion> complete(CommandContext context, String currentInput) {
        return context.one(MFGive.FISH_TYPE).map(fishType -> List.of(of(fishType.lengthMin() + "", text("Minimum length.")), of(fishType.lengthMax() + "", text("Maximum length.")))).orElse(List.of());
    }

    @Override
    public Optional<? extends Double> parseValue(Key<? super Double> parameterKey, Mutable reader, Builder context) throws ArgumentParseException {
        return Optional.of(reader.parseDouble());
    }
}
