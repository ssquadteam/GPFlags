package me.ryanhamshire.GPFlags.flags;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Base flag definition for movement based flags
 * <p>When creating a flag that requires checks for players moving in/out of claims, extend from this class</p>
 */
@SuppressWarnings("WeakerAccess")
public abstract class PlayerMovementFlagDefinition extends TimedPlayerFlagDefinition implements Runnable {

    private ConcurrentHashMap<UUID, Location> lastLocationMap = new ConcurrentHashMap<>();

    public PlayerMovementFlagDefinition(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    public abstract boolean allowMovement(Player player, Location lastLocation);

    @Override
    public long getPlayerCheckFrequency_Ticks() {
        return 20L;
    }

    @Override
    public void processPlayer(Player player) {
        UUID playerID = player.getUniqueId();
        Location lastLocation = this.lastLocationMap.get(playerID);
        Location location = player.getLocation();
        if (lastLocation != null && location.getBlockX() == lastLocation.getBlockX() && location.getBlockY() == lastLocation.getBlockY() && location.getBlockZ() == lastLocation.getBlockZ())
            return;
        if (!this.allowMovement(player, lastLocation)) {
            this.undoMovement(player, lastLocation);
        } else {
            this.lastLocationMap.put(playerID, location);
        }
    }

    public void undoMovement(Player player, Location lastLocation) {
        if (lastLocation != null) {
            player.teleport(lastLocation);
        } else if (player.getBedSpawnLocation() != null) {
            player.teleport(player.getBedSpawnLocation());
        } else {
            player.teleport(player.getWorld().getSpawnLocation());
        }
    }

    @Override
    public List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }

}
