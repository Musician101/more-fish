package me.elsiff.morefish.fish.condition;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import me.elsiff.morefish.hooker.McmmoHooker;
import me.elsiff.morefish.lang.TagResolverUtil;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static me.elsiff.morefish.MoreFish.getPlugin;

@NullMarked
public class McmmoSkillsCondition extends FishCondition<Map<PrimarySkillType, Integer>> {

	public McmmoSkillsCondition(Map<PrimarySkillType, Integer> value) {
		super(value);
	}


    public boolean check(Item caught, Player fisher) {
        McmmoHooker mcmmoHooker = getPlugin().getMcmmo();
        if (mcmmoHooker.hasHooked()) {
            return value.entrySet().stream().allMatch(e -> mcmmoHooker.skillLevelOf(fisher, e.getKey()) >= e.getValue());
        }

        return true;
    }

    @Override
    public @Nullable Tag resolve(String name, ArgumentQueue arguments, Context ctx) throws ParsingException {
        if (has(name)) {
            McmmoHooker mcmmoHooker = getPlugin().getMcmmo();
            if (mcmmoHooker.hasHooked()) {
                return TagResolverUtil.fromMap(value, arguments, ctx, 0, mcmmoHooker::getSkill, i -> Tag.preProcessParsed(i + ""));
            }
        }

        return null;
    }

    @Override
    public boolean has(String name) {
        return name.equals("mcmmo-skills");
    }
}
