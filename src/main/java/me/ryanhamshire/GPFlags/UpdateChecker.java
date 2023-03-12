package me.ryanhamshire.GPFlags;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.bukkit.Bukkit.getLogger;

public class UpdateChecker {
    static String userAgent;

    public static void checkForUpdates(JavaPlugin plugin) {
        // Get current version
        String version = plugin.getDescription().getVersion();
        if (version.contains("-")) {
            version = version.split("-")[0];
        }
        DefaultArtifactVersion usingVersion = new DefaultArtifactVersion(version);

        // Get latest version from Modrinth
        final JsonElement json = getJsonAs("gpflags/" + version);
        if (json == null) return;
        version = json.getAsJsonArray().get(0).getAsJsonObject().get("version_number").getAsString();
        if (version.contains("-")) {
            version = version.split("-")[0];
        }
        DefaultArtifactVersion latestVersion = new DefaultArtifactVersion(version);

        // Compare versions
        if (usingVersion.compareTo(latestVersion) < 0) {
            getLogger().warning("You are using an outdated version of GPFlags. Please update at https://modrinth.com/plugin/gpflags");
        }
    }

    private static JsonElement getJsonAs(String userAgent) {
        final HttpURLConnection connection;
        final JsonElement json;
        try {
            connection = (HttpURLConnection) new URL("https://api.modrinth.com/v2/project/Z0NVSlL6/version").openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", userAgent);
            if (connection.getResponseCode() == 404) return null;
            json = new JsonParser().parse(new InputStreamReader(connection.getInputStream()));
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
        connection.disconnect();
        return json;
    }
}
