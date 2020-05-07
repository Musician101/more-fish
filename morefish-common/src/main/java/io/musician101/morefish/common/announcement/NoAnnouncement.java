package io.musician101.morefish.common.announcement;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public abstract class NoAnnouncement<P> implements PlayerAnnouncement<P> {

    @Nonnull
    @Override
    public final List<P> receiversOf(@Nonnull P catcher) {
        return Collections.emptyList();
    }
}
