package me.ryanhamshire.GPFlags;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NetherPortalConsoleCommand extends FlagDefinition {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        TeleportCause cause = event.getCause();
        
        if(cause != TeleportCause.NETHER_PORTAL) return;
        
        Player player = event.getPlayer();
        
        Flag flag = this.GetFlagInstanceAtLocation(event.getFrom(), player);
        if(flag == null) return;
        
        event.setCancelled(true);
        String [] commandLines = flag.parameters.replace("%name%", player.getName()).replace("%uuid%", player.getUniqueId().toString()).split(";");
        for(String commandLine : commandLines)
        {
            GPFlags.AddLogEntry("Nether portal command: " + commandLine);
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commandLine);
        }
    }
    
    FlagDef_NetherPortalConsoleCommand(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }
    
    @Override
    SetFlagResult ValidateParameters(String parameters)
    {
        if(parameters.isEmpty())
        {
            return new SetFlagResult(false, new MessageSpecifier(Messages.ConsoleCommandRequired));
        }

        return new SetFlagResult(true, this.GetSetMessage(parameters));
    }
    
    @Override
    String getName()
    {
        return "NetherPortalConsoleCommand";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnableNetherPortalConsoleCommand);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisableNetherPortalConsoleCommand);
    }

    @Override
    List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD);
    }

}
