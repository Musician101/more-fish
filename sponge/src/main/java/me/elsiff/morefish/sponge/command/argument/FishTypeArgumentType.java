package me.elsiff.morefish.sponge.command.argument;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.elsiff.morefish.sponge.fishing.SpongeFishType;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader.Mutable;
import org.spongepowered.api.command.parameter.managed.ValueParameter.Simple;

import static me.elsiff.morefish.sponge.SpongeMoreFish.getPlugin;

public class FishTypeArgumentType implements Simple<SpongeFishType> {

    @Override
    public List<CommandCompletion> complete(CommandCause context, String currentInput) {
        return fishes().map(SpongeFishType::name).filter(name -> name.startsWith(currentInput)).map(CommandCompletion::of).toList();
    }

    private Stream<SpongeFishType> fishes() {
        return getPlugin().getFishTypeTable().getTypes().stream();
    }

    @Override
    public Optional<? extends SpongeFishType> parseValue(CommandCause commandCause, Mutable reader) throws ArgumentParseException {
        String name = reader.parseString();
        return fishes().filter(f -> f.name().equals(name)).findFirst();
    }
}
