package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.event.PlayerPostClaimBorderEvent;
import me.ryanhamshire.GPFlags.event.PlayerPreClaimBorderEvent;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

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

    public boolean allowMovement(Player player, Location from, Location to, Claim claimFrom, Claim claimTo) {
        return true;
    }

    @EventHandler
    public void onMove(PlayerPreClaimBorderEvent event) {
        Player player = event.getPlayer();
        Location from = event.getLocFrom().clone();
        int fromMaxHeight = Util.getMaxHeight(from);
        if (from.getY() > fromMaxHeight) {
            from.setY(fromMaxHeight);
        }
        int fromMinHeight = Util.getMinHeight(from);
        if (from.getY() < fromMinHeight) {
            from.setY(fromMinHeight);
        }
        Location to = event.getLocTo().clone();
        int toMaxHeight = Util.getMaxHeight(to);
        if (to.getY() > toMaxHeight) {
            to.setY(toMaxHeight);
        }
        int toMinHeight = Util.getMinHeight(to);
        if (to.getY() < toMinHeight) {
            to.setY(toMaxHeight);
        }
        Claim claimFrom = event.getClaimFrom();
        Claim claimTo = event.getClaimTo();
        if (!this.allowMovement(player, from, to, claimFrom, claimTo)) {
            event.setCancelled(true);
            event.getPlayer().setVelocity(new Vector());
        }
    }

    @EventHandler
    public void onPostMove(PlayerPostClaimBorderEvent event) {
        onChangeClaim(event.getPlayer(), event.getLocFrom(), event.getLocTo(), event.getClaimFrom(), event.getClaimTo());
    }

    public void onChangeClaim(Player player, Location from, Location to, Claim claimFrom, Claim claimTo) {}

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
