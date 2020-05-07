package io.musician101.morefish.common.announcement;

import java.util.List;
import javax.annotation.Nonnull;

public interface PlayerAnnouncement<P> {

    @Nonnull
    List<P> receiversOf(@Nonnull P catcher);
}
