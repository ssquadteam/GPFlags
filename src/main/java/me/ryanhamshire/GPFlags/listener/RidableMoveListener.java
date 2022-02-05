package me.ryanhamshire.GPFlags.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.purpurmc.purpur.event.entity.RidableMoveEvent;

public class RidableMoveListener implements Listener {

    @EventHandler
    private void onRidableMove(RidableMoveEvent event) {
        Player rider = event.getRider();
        Location locTo = event.getTo();
        Location locFrom = event.getFrom();
        PlayerListener.processMovement(locTo, locFrom, rider, event);
    }
}
