package me.ryanhamshire.GPFlags.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PlaceholderApiHook {

    public static String addPlaceholders(OfflinePlayer player, String message) {
        try {
            return PlaceholderAPI.setPlaceholders(player, message);
        } catch (NoClassDefFoundError e) {
            return message;
        }
    }
}
