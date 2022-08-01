package me.ryanhamshire.GPFlags;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.bukkit.entity.Entity;

public class MythicMobsHook {

    public static boolean isMythicMob(Entity entity) {
        BukkitAPIHelper mythicMobsAPI = new BukkitAPIHelper();
        return mythicMobsAPI.isMythicMob(entity);
    }
}
