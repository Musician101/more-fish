package me.elsiff.morefish.sponge.announcement;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.elsiff.morefish.common.announcement.PlayerAnnouncement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public interface SpongePlayerAnnouncement extends PlayerAnnouncement<ServerPlayer> {

    @NotNull
    static PlayerAnnouncement<ServerPlayer> fromConfigOrDefault(@Nullable JsonObject json, @NotNull String path, @NotNull PlayerAnnouncement<ServerPlayer> def) {
        if (json == null || !json.has(path)) {
            return def;
        }

        return fromValue(json.getAsJsonPrimitive(path).getAsDouble());
    }

    @NotNull
    static PlayerAnnouncement<ServerPlayer> fromValue(double d) {
        return switch ((int) d) {
            case -2 -> PlayerAnnouncement.ofEmpty();
            case -1 -> SpongePlayerAnnouncement.ofServerBroadcast();
            case 0 -> PlayerAnnouncement.ofBaseOnly();
            default -> SpongePlayerAnnouncement.ofRanged(d);
        };
    }

    @NotNull
    static PlayerAnnouncement<ServerPlayer> ofRanged(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must not be negative");
        }

        return new SpongeRangedAnnouncement(radius);
    }

    @NotNull
    static PlayerAnnouncement<ServerPlayer> ofServerBroadcast() {
        return new SpongeServerAnnouncement();
    }

    @NotNull List<ServerPlayer> receiversOf(@NotNull ServerPlayer catcher);

    final class SpongeRangedAnnouncement extends RangedAnnouncement<ServerPlayer> {

        public SpongeRangedAnnouncement(double radius) {
            super(radius);
        }

        @NotNull
        public List<ServerPlayer> receiversOf(@NotNull ServerPlayer catcher) {
            return catcher.world().players().stream().filter(player -> player.position().distance(catcher.position()) <= radius).collect(Collectors.toList());
        }
    }

    final class SpongeServerAnnouncement implements SpongePlayerAnnouncement {

        @NotNull
        public List<ServerPlayer> receiversOf(@NotNull ServerPlayer catcher) {
            return new ArrayList<>(Sponge.server().onlinePlayers());
        }
    }
}
