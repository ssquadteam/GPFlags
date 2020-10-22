package me.ryanhamshire.GPFlags.util;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.HashMap;

@SuppressWarnings("WeakerAccess")
public class Util {

    /** Check if server is running a minimum Minecraft version
     * @param major Major version to check (Most likely just going to be 1)
     * @param minor Minor version to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor) {
        return isRunningMinecraft(major, minor, 0);
    }

    /** Check if server is running a minimum Minecraft version
     * @param major Major version to check (Most likely just going to be 1)
     * @param minor Minor version to check
     * @param revision Revision to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor, int revision) {
        String[] version = Bukkit.getServer().getBukkitVersion().split("-")[0].split("\\.");
        int maj = Integer.parseInt(version[0]);
        int min = Integer.parseInt(version[1]);
        int rev;
        try {
            rev = Integer.parseInt(version[2]);
        } catch (Exception ignore) {
            rev = 0;
        }
        return maj > major || min > minor || (min == minor && rev >= revision);
    }


    /** Disable the flight mode of a player whom cant fly
     * <p>This is mainly used when a player deletes their claim.</p>
     * @param player Player to disable flight for
     */
    public static void disableFlight(Player player) {
        if (player.isFlying() && !canFly(player)) {
            Location loc = player.getLocation();
            Block block = loc.getBlock();
            while (block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER) {
                block = block.getRelative(BlockFace.DOWN);
            }
            player.setAllowFlight(false);
            if (loc.getY() - block.getY() >= 4) {
                GPFlags.getInstance().getPlayerListener().addFallingPlayer(player);
            }
            GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
        }
        if (player.getAllowFlight() && !canFly(player)) {
            player.setAllowFlight(false);
            GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
        }
    }

    private static boolean canFly(Player player) {
        GameMode mode = player.getGameMode();
        return mode == GameMode.SPECTATOR || mode == GameMode.CREATIVE ||
                player.hasPermission("gpflags.bypass.fly") || player.hasPermission("gpflags.bypass");
    }

}
