package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GriefPrevention.events.ClaimExpirationEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Collections;
import java.util.List;

public class FlagDef_NoExpiration extends FlagDefinition {

    public FlagDef_NoExpiration(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClaimExpiration(ClaimExpirationEvent event) {
        Location location = event.getClaim().getLesserBoundaryCorner();
        Flag flag = this.getFlagInstanceAtLocation(location, null);
        if (flag != null) {
            event.setCancelled(true);
        }
    }

    @Override
    public String getName() {
        return "NoExpiration";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoExpiration);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoExpiration);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }

}
