package me.elsiff.morefish.fishing.competition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.Config;
import me.elsiff.morefish.gui.AbstractGUI;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.entity.Player;

public class RewardConfigGUI extends AbstractGUI {

    public RewardConfigGUI(int page, @Nonnull Player user) {
        super("Rewards Config", MoreFish.instance().getOneTickScheduler(), user);
        Config config = Config.INSTANCE;
        List<Entry<IntRange, Prize>> prizes = new ArrayList<>(config.getPrizeMapLoader().loadFrom(config.getRewards(), "").entrySet());
        IntStream.range(0, 45).forEach(x -> {
            try {
                Entry<IntRange, Prize> prizeEntry = prizes.get(x + (page - 1) * 45);
                IntRange range = prizeEntry.getKey();
                Prize prize = prizeEntry.getValue();

                //TODO left off here. checked with dwight, will implement later
                //TODO keep commands as an option
                //TODO add items, entities, fireworks, particles, sounds, and experience
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        });
    }
}
