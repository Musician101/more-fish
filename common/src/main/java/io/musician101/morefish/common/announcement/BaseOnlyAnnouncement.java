package io.musician101.morefish.common.announcement;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class BaseOnlyAnnouncement implements PlayerAnnouncement {

    @Nonnull
    @Override
    public List<UUID> receiversOf(@Nonnull UUID catcher) {
        return Collections.singletonList(catcher);
    }
}
