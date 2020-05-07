package io.musician101.morefish.common.config;

import io.musician101.morefish.common.announcement.PlayerAnnouncement;
import java.util.function.Function;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;

public final class MessagesConfig<A extends PlayerAnnouncement<?>, C> implements ConfigModule {

    @Nonnull
    private final Function<Object, A> announcementLoader;
    @Nonnull
    private final Function<Object, C> barColorLoader;
    @Nonnull
    protected A announceCatch;
    @Nonnull
    protected A announceNew1st;
    protected boolean broadcastStart = true;
    protected boolean broadcastStop = true;
    @Nonnull
    protected C contestBarColor;
    protected boolean onlyAnnounceFishingRod = false;
    protected boolean showTopOnEnding = true;
    protected int topNumber = 3;

    public MessagesConfig(@Nonnull A announceCatch, @Nonnull A announceNew1st, @Nonnull C contestBarColor, @Nonnull Function<Object, A> announcementLoader, @Nonnull Function<Object, C> barColorLoader) {
        this.announceCatch = announceCatch;
        this.announceNew1st = announceNew1st;
        this.contestBarColor = contestBarColor;
        this.announcementLoader = announcementLoader;
        this.barColorLoader = barColorLoader;
    }

    public boolean broadcastOnStart() {
        return broadcastStart;
    }

    public boolean broadcastOnStop() {
        return broadcastStop;
    }

    @Nonnull
    public A getAnnounceCatch() {
        return announceCatch;
    }

    @Nonnull
    public A getAnnounceNew1st() {
        return announceNew1st;
    }

    @Nonnull
    public C getContestBarColor() {
        return contestBarColor;
    }

    public int getTopNumber() {
        return topNumber;
    }

    @Override
    public void load(@Nonnull ConfigurationNode node) {
        if (node.isVirtual()) {
            return;
        }

        announceCatch = node.getNode("announce-catch").getValue(announcementLoader, announceCatch);
        announceCatch = node.getNode("announce-new-1st").getValue(announcementLoader, announceNew1st);
        onlyAnnounceFishingRod = node.getNode("only-announce-fishing-rod").getBoolean(onlyAnnounceFishingRod);
        broadcastStart = node.getNode("broadcast-start").getBoolean(broadcastStart);
        broadcastStop = node.getNode("broadcast-stop").getBoolean(broadcastStop);
        showTopOnEnding = node.getNode("show-top-on-ending").getBoolean(showTopOnEnding);
        contestBarColor = node.getNode("contest-bar-color").getValue(barColorLoader, contestBarColor);
        topNumber = node.getNode("top-number").getInt(topNumber);
    }

    public boolean onlyAnnounceFishingRod() {
        return onlyAnnounceFishingRod;
    }

    public boolean showTopOnEnding() {
        return showTopOnEnding;
    }
}
