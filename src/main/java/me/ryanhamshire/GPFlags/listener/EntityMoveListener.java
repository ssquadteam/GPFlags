package me.ryanhamshire.GPFlags.listener;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityMoveListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    private void onMove(EntityMoveEvent event) {
        Player rider = event.getEntity().getRider();
        if (rider != null) {
            Location locTo = event.getTo();
            Location locFrom = event.getFrom();
            PlayerListener.processMovement(locTo, locFrom, rider, event);
        }
    }
}