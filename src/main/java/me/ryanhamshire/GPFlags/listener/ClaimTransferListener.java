package me.ryanhamshire.GPFlags.listener;


import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.events.ClaimTransferEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;

public class ClaimTransferListener implements Listener {

    @EventHandler
    private void onTransferClaim(ClaimTransferEvent event) {
        Claim claim = event.getClaim();
        if (claim.isAdminClaim()) return;
        if (event.getNewOwner() == null) return;
        Collection<Flag> flags = GPFlags.getInstance().getFlagManager().getFlags(claim);
        flags.clear();


    }
}
