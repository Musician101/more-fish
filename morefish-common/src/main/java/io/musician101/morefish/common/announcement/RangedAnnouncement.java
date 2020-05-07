package io.musician101.morefish.common.announcement;

public abstract class RangedAnnouncement<P> implements PlayerAnnouncement<P> {

    protected final double radius;

    protected RangedAnnouncement(double radius) {
        this.radius = radius;
    }
}
