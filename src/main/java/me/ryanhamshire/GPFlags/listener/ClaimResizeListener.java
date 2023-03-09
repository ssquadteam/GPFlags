package me.ryanhamshire.GPFlags.listener;

import me.ryanhamshire.GriefPrevention.events.ClaimResizeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ClaimResizeListener implements Listener {

    @EventHandler
    // Call the claim border event when a player resizes a claim and they are now outside of the claim
    private void onChangeClaim(ClaimResizeEvent event) {
        PlayerListener.onClaimResize(event);
    }
}
