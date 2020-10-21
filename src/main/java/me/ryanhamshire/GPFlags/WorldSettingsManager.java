package me.ryanhamshire.GPFlags;

import org.bukkit.World;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager for world settings
 */
public class WorldSettingsManager {

    private ConcurrentHashMap<String, WorldSettings> nameToSettingsMap = new ConcurrentHashMap<String, WorldSettings>();
    private final String OtherWorldsKey = "Other Worlds";

    WorldSettingsManager() {
        this.nameToSettingsMap.put(this.OtherWorldsKey, new WorldSettings());
    }

    void set(World world, WorldSettings settings) {
        this.set(world.getName(), settings);
    }

    public void set(String key, WorldSettings settings) {
        this.nameToSettingsMap.put(key, settings);
    }

    public WorldSettings get(World world) {
        return this.get(world.getName());
    }

    WorldSettings get(String key) {
        WorldSettings settings = this.nameToSettingsMap.get(key);
        if (settings != null) return settings;
        return this.nameToSettingsMap.get(this.OtherWorldsKey);
    }

    public WorldSettings create(String worldName) {
        WorldSettings settings = new WorldSettings();
        this.nameToSettingsMap.remove(worldName);
        this.nameToSettingsMap.put(worldName, settings);
        return settings;
    }

}
