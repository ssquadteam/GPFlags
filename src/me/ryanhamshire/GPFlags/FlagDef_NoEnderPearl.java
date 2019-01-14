package me.ryanhamshire.GPFlags;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class FlagDef_NoEnderPearl extends FlagDefinition
{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        if(event.getCause() != TeleportCause.ENDER_PEARL) return;

        Player player = event.getPlayer();

        Flag flag = this.GetFlagInstanceAtLocation(event.getFrom(), event.getPlayer());
        if(flag != null)
        {
            event.setCancelled(true);
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getFrom(), true, null);
            if (claim != null) {
                String owner = claim.getOwnerName();

                String msg = new FlagsDataStore().getMessage(Messages.NoEnderPearlInClaim);
                GPFlags.sendMessage(player, TextMode.Warn, msg.replace("{o}", owner).replace("{p}", player.getName()));
                return;
            }
            String msg = new FlagsDataStore().getMessage(Messages.NoEnderPearlInWorld);
            GPFlags.sendMessage(player, TextMode.Warn, msg.replace("{p}", player.getName()));
            return;
        }
        
        flag = this.GetFlagInstanceAtLocation(event.getTo(), event.getPlayer());
        if(flag != null)
        {
            event.setCancelled(true);
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getTo(), true, null);
            if (claim != null) {
                String owner = claim.getOwnerName();

                String msg = new FlagsDataStore().getMessage(Messages.NoEnderPearlToClaim);
                GPFlags.sendMessage(player, TextMode.Warn, msg.replace("{o}", owner).replace("{p}", player.getName()));
            }
        }

    }
    
    public FlagDef_NoEnderPearl(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }
    
    @Override
    String getName() {
        return "NoEnderPearl";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnableNoEnderPearl);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisableNoEnderPearl);
    }
}
