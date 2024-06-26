package me.ryanhamshire.GPFlags.listener;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.event.PlayerPostClaimBorderEvent;
import me.ryanhamshire.GPFlags.event.PlayerPreClaimBorderEvent;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PlayerListener implements Listener {

    private static final DataStore dataStore = GriefPrevention.instance.dataStore;
    private final FlagManager FLAG_MANAGER = GPFlags.getInstance().getFlagManager();

    @EventHandler(ignoreCancelled = true)
    private void onMove(PlayerMoveEvent event) {
        Location locTo = event.getTo();
        Location locFrom = event.getFrom();
        Player player = event.getPlayer();
        processMovement(locTo, locFrom, player, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onTeleport(PlayerTeleportEvent event) {
        Location locTo = event.getTo();
        Location locFrom = event.getFrom();
        Player player = event.getPlayer();
        processMovement(locTo, locFrom, player, event);
    }

    @EventHandler
    private void onVehicleMove(VehicleMoveEvent event) {
        Location locTo = event.getTo();
        Location locFrom = event.getFrom();
        Vehicle vehicle = event.getVehicle();
        for (Entity entity : vehicle.getPassengers()) {
            if (entity instanceof Player) {
                Player player = ((Player) entity);
                if (!processMovement(locTo, locFrom, player, null)) {
                    vehicle.eject();
                    ItemStack itemStack = Util.getItemFromVehicle(vehicle);
                    if (itemStack != null) {
                        vehicle.getWorld().dropItem(locFrom, itemStack);
                    }
                    vehicle.remove();
                    player.teleport(locFrom);
                }
            }
        }
    }

    @EventHandler
    private void onMount(VehicleEnterEvent event) {
        Entity entity = event.getEntered();
        Vehicle vehicle = event.getVehicle();
        if (entity instanceof Player) {
            Player player = ((Player) entity);
            Location from = player.getLocation();
            Location to = vehicle.getLocation();
            processMovement(to, from, player, event);
        }
    }

    /**
     *
     * @param locTo
     * @param locFrom
     * @param player
     * @param event
     * @return If the created PreClaimBorderEvent was permitted
     */
    public static boolean processMovement(Location locTo, Location locFrom, Player player, Cancellable event) {
        if (locTo.getBlockX() == locFrom.getBlockX() && locTo.getBlockY() == locFrom.getBlockY() && locTo.getBlockZ() == locFrom.getBlockZ())
            return true;
        Location locFrom2 = locFrom.clone();
        int maxWorldHeightFrom = Util.getMaxHeight(locFrom2);
        if (locFrom2.getY() >= maxWorldHeightFrom) {
            locFrom2.setY(maxWorldHeightFrom - 1);
        }
        Location locTo2 = locTo.clone();
        int maxWorldHeightTo = Util.getMaxHeight(locTo2);
        if (locTo2.getY() >= maxWorldHeightTo) {
            locTo2.setY(maxWorldHeightTo - 1);
        }
        Claim claimTo = dataStore.getClaimAt(locTo2, false, null);
        Claim claimFrom = dataStore.getClaimAt(locFrom2, false, null);
        if (claimTo == claimFrom) return true;
        PlayerPreClaimBorderEvent playerPreClaimBorderEvent = new PlayerPreClaimBorderEvent(player, claimFrom, claimTo, locFrom2, locTo2);
        Bukkit.getPluginManager().callEvent(playerPreClaimBorderEvent);
        if (!playerPreClaimBorderEvent.isCancelled()) {
            Bukkit.getPluginManager().callEvent(new PlayerPostClaimBorderEvent(playerPreClaimBorderEvent));
        }
        if (event != null) {
            event.setCancelled(playerPreClaimBorderEvent.isCancelled());
        }
        return !playerPreClaimBorderEvent.isCancelled();
    }

}
