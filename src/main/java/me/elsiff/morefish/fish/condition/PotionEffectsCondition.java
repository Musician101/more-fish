package me.elsiff.morefish.fish.condition;

import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.bukkit.Registry;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public class PotionEffectsCondition extends FishCondition<Map<PotionEffectType, Integer>> {

	public PotionEffectsCondition(Map<PotionEffectType, Integer> value) {
		super(value);
	}


    public boolean check(Item caught, Player fisher) {
        return value.entrySet().stream().allMatch(e -> {
            PotionEffect pe = fisher.getPotionEffect(e.getKey());
            return value.isEmpty() || fisher.hasPotionEffect(e.getKey()) && pe != null && pe.getAmplifier() >= e.getValue();
        });
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            return TagResolverUtil.fromMap(value, arguments, ctx, 0, s -> {
                Key key = Key.key(s);
                return Registry.POTION_EFFECT_TYPE.get(key);
            }, i -> Tag.preProcessParsed(i + ""));
        }

        return null;
    }

    @Override
    public boolean has(String name) {
        return name.equals("potion-effects");
    }
}
