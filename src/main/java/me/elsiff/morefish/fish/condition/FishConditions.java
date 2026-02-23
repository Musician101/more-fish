package me.elsiff.morefish.fish.condition;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.VirtualComponent;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.translation.Argument;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public class FishConditions implements ComponentLike, TagResolver {

    @Nullable
    private BiomesCondition biomes;
    @Nullable
    private EnchantmentsCondition enchantments;
    @Nullable
    private LocationYCondition locationY;
    @Nullable
    private McmmoSkillsCondition mcmmoSkills;
    @Nullable
    private PotionEffectsCondition potionEffects;
    @Nullable
    private RainingCondition raining;
    @Nullable
    private ThunderingCondition thundering;
    @Nullable
    private TimeCondition time;
    @Nullable
    private XpLevelCondition xpLevel;

    public Optional<BiomesCondition> biomes() {
        return Optional.ofNullable(biomes);
    }

    public void biomes(@Nullable BiomesCondition biomes) {
        this.biomes = biomes;
    }

    public Optional<EnchantmentsCondition> enchantments() {
        return Optional.ofNullable(enchantments);
    }

    public void enchantments(@Nullable EnchantmentsCondition enchantments) {
        this.enchantments = enchantments;
    }

    public Optional<LocationYCondition> locationY() {
        return Optional.ofNullable(locationY);
    }

    public void locationY(@Nullable LocationYCondition locationY) {
        this.locationY = locationY;
    }

    public Optional<McmmoSkillsCondition> mcmmoSkills() {
        return Optional.ofNullable(mcmmoSkills);
    }

    public void mcmmoSkills(@Nullable McmmoSkillsCondition mcmmoSkills) {
        this.mcmmoSkills = mcmmoSkills;
    }

    public Optional<PotionEffectsCondition> potionEffects() {
        return Optional.ofNullable(potionEffects);
    }

    public void potionEffects(@Nullable PotionEffectsCondition potionEffects) {
        this.potionEffects = potionEffects;
    }

    public Optional<RainingCondition> raining() {
        return Optional.ofNullable(raining);
    }

    public void raining(@Nullable RainingCondition raining) {
        this.raining = raining;
    }

    public Optional<ThunderingCondition> thundering() {
        return Optional.ofNullable(thundering);
    }

    public void thundering(@Nullable ThunderingCondition thundering) {
        this.thundering = thundering;
    }

    public Optional<TimeCondition> time() {
        return Optional.ofNullable(time);
    }

    public void time(@Nullable TimeCondition time) {
        this.time = time;
    }

    public Optional<XpLevelCondition> xpLevel() {
        return Optional.ofNullable(xpLevel);
    }

    public void xpLevel(@Nullable XpLevelCondition xpLevel) {
        this.xpLevel = xpLevel;
    }

    public boolean check(Item caught, Player fisher) {
        boolean canCatch = true;
        if (biomes().isPresent()) {
            canCatch = biomes().get().check(caught, fisher);
        }

        if (enchantments().isPresent()) {
            canCatch = enchantments().get().check(caught, fisher);
        }

        if (locationY().isPresent()) {
            canCatch = locationY().get().check(caught, fisher);
        }

        if (mcmmoSkills().isPresent()) {
            canCatch = mcmmoSkills().get().check(caught, fisher);
        }

        if (potionEffects().isPresent()) {
            canCatch = potionEffects().get().check(caught, fisher);
        }

        if (raining().isPresent()) {
            canCatch = raining().get().check(caught, fisher);
        }

        if (thundering().isPresent()) {
            canCatch = thundering().get().check(caught, fisher);
        }

        if (time().isPresent()) {
            canCatch = time().get().check(caught, fisher);
        }

        if (xpLevel().isPresent()) {
            canCatch = xpLevel().get().check(caught, fisher);
        }

        return canCatch;
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            arguments.reset();
            String value = arguments.popOr(name + " requires at least 1 argument").value();
            return switch (value) {
                case "biomes" -> resolve(name, biomes, arguments, ctx);
                case "enchantments" -> resolve(name, enchantments, arguments, ctx);
                case "location-y" -> resolve(name, locationY, arguments, ctx);
                case "mcmmo-skills" -> resolve(name, mcmmoSkills, arguments, ctx);
                case "potion-effects" -> resolve(name, potionEffects, arguments, ctx);
                case "raining" -> resolve(name, raining, arguments, ctx);
                case "thundering" -> resolve(name, thundering, arguments, ctx);
                case "time" -> resolve(name, time, arguments, ctx);
                case "xp-level" -> resolve(name, xpLevel, arguments, ctx);
                default -> null;
            };
        }

        return null;
    }

    @SuppressWarnings("PatternValidation")
    private @Nullable Tag resolve(String name, @Nullable FishCondition<?> condition, ArgumentQueue arguments, Context ctx) {
        if (condition == null) {
            return null;
        }

        return condition.resolve(name, arguments, ctx);
    }

    @Override
    public boolean has(String name) {
        return name.equals("conditions");
    }

    @Override
    public Component asComponent() {
        return (VirtualComponent) Argument.tagResolver(this);
    }
}
