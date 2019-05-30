package me.ryanhamshire.GPFlags;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import me.ryanhamshire.GriefPrevention.events.ClaimExpirationEvent;

import java.util.Collections;
import java.util.List;

public class FlagDef_NoExpiration extends FlagDefinition
{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClaimExpiration(ClaimExpirationEvent event)
    {
        Location location = event.getClaim().getLesserBoundaryCorner();
        Flag flag = this.GetFlagInstanceAtLocation(location, null);
        if(flag != null)
        {
            event.setCancelled(true);
        }
    }
    
    FlagDef_NoExpiration(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }
    
    @Override
    String getName()
    {
        return "NoExpiration";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnableNoExpiration);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisableNoExpiration);
    }

    @Override
    List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }

}
