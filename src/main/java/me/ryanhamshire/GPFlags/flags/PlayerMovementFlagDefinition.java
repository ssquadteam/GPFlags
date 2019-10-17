package me.ryanhamshire.GPFlags.flags;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.event.PlayerClaimBorderEvent;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Base flag definition for movement based flags
 * <p>When creating a flag that requires checks for players moving in/out of claims, extend from this class</p>
 */
@SuppressWarnings("WeakerAccess")
public abstract class PlayerMovementFlagDefinition extends FlagDefinition {

    public PlayerMovementFlagDefinition(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    public abstract boolean allowMovement(Player player, Location from, Location to);

    @EventHandler
    void onMove(PlayerClaimBorderEvent event) {
        Player player = event.getPlayer();
        Location lastLocation = event.getLocFrom();
        if (!this.allowMovement(player, lastLocation, event.getLocTo())) {
            this.undoMovement(player, lastLocation);
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
