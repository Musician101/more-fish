package me.elsiff.morefish.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class UpdateChecker {

    private final String currentVersion;
    @Nullable
    public String newVersion;
    private URL checkUrl;

    public UpdateChecker(int projectId, @Nonnull String currentVersion) {
        super();
        this.currentVersion = currentVersion;
        try {
            this.checkUrl = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectId);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public final void check() {
        URLConnection connection = null;
        try {
            connection = checkUrl.openConnection();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            newVersion = br.readLine();
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public final String getNewVersion() {
        return this.newVersion;
    }

    public final boolean hasNewVersion() {
        return Objects.equals(newVersion, currentVersion);
    }
}
