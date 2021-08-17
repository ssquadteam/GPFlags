package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.event.PlayerClaimBorderEvent;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Collections;
import java.util.List;

/**
 * Base flag definition for movement based flags
 * <p>When creating a flag that requires checks for players moving in/out of claims, extend from this class</p>
 */
@SuppressWarnings("WeakerAccess")
public abstract class PlayerMovementFlagDefinition extends FlagDefinition {

    public PlayerMovementFlagDefinition(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Deprecated
    public boolean allowMovement(Player player, Location from, Location to) {
        return true;
    }

    public boolean allowMovement(Player player, Location from, Location to, Claim claimFrom, Claim claimTo) {
        return true;
    }

    @EventHandler
    public void onMove(PlayerClaimBorderEvent event) {
        Player player = event.getPlayer();
        Location lastLocation = event.getLocFrom();
        Location to = event.getLocTo();
        int maxHeight = to.getWorld().getMaxHeight();
        if (to.getY() > maxHeight) {
            to.setY(maxHeight);
        }
        if (!this.allowMovement(player, lastLocation, to)) {
            //this.undoMovement(player, lastLocation);
            event.setCancelled(true);
        }
        if (!this.allowMovement(player, lastLocation, to, event.getClaimFrom(), event.getClaimTo())) {
            event.setCancelled(true);
        }
    }

    // This is being removed, but we are keeping it for a bit just in case
    public void undoMovement(Player player, Location lastLocation) {
        Bukkit.broadcastMessage("Undoing movement");
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
