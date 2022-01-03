package io.musician101.morefish.common.announcement;

public abstract class RangedAnnouncement implements PlayerAnnouncement {

    protected final double radius;

    protected RangedAnnouncement(double radius) {
        this.radius = radius;
    }
}
