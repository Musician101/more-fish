package me.elsiff.morefish.common.announcement;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface PlayerAnnouncement<P> {

    @NotNull
    static <P> PlayerAnnouncement<P> ofBaseOnly() {
        return new BaseOnlyAnnouncement<>();
    }

    @NotNull
    static <P> PlayerAnnouncement<P> ofEmpty() {
        return new NoAnnouncement<>();
    }

    @NotNull List<P> receiversOf(@NotNull P player);

    final class BaseOnlyAnnouncement<P> implements PlayerAnnouncement<P> {

        @NotNull
        public List<P> receiversOf(@NotNull P player) {
            return List.of(player);
        }
    }

    final class NoAnnouncement<P> implements PlayerAnnouncement<P> {

        @NotNull
        public List<P> receiversOf(@NotNull P player) {
            return List.of();
        }
    }

    abstract class RangedAnnouncement<P> implements PlayerAnnouncement<P> {

        protected final double radius;

        public RangedAnnouncement(double radius) {
            this.radius = radius;
        }
    }
}
