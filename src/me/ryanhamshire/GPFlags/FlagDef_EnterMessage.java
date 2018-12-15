package me.ryanhamshire.GPFlags;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_EnterMessage extends PlayerMovementFlagDefinition
{
    @Override
    public boolean allowMovement(Player player, Location lastLocation)
    {
        if(lastLocation == null) return true;
        Location to = player.getLocation();
        Flag flag = this.GetFlagInstanceAtLocation(to, player);
        if(flag == null) return true;
        
        if(flag == this.GetFlagInstanceAtLocation(lastLocation, player)) return true;
        
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        String message = flag.parameters;
        if(playerData.lastClaim != null)
        {
            message = message.replace("%owner%", playerData.lastClaim.getOwnerName()).replace("%name%", player.getName());
        }

        String prefix = ChatColor.translateAlternateColorCodes('&', new FlagsDataStore().getMessage(Messages.EnterExitPrefix));
        GPFlags.sendMessage(player, TextMode.Info,prefix +  message);
        
        return true;
    }
    
    public FlagDef_EnterMessage(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if(flag == null) return;
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        String message = flag.parameters;
        message = message.replace("%owner%", playerData.lastClaim.getOwnerName()).replace("%name%", player.getName());
        String prefix = ChatColor.translateAlternateColorCodes('&', new FlagsDataStore().getMessage(Messages.EnterExitPrefix));
        GPFlags.sendMessage(player, TextMode.Info, prefix + message);
    }
    
    @Override
    String getName()
    {
        return "EnterMessage";
    }

    @Override
    SetFlagResult ValidateParameters(String parameters)
    {
        if(parameters.isEmpty())
        {
            return new SetFlagResult(false, new MessageSpecifier(Messages.MessageRequired));
        }

        return new SetFlagResult(true, this.GetSetMessage(parameters));
    }
    
    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.AddedEnterMessage, parameters);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.RemovedEnterMessage);
    }
}
