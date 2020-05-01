package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;


public class FlagDef_EnterPlayerCommand extends PlayerMovementFlagDefinition
{
    @Override
    public boolean allowMovement(Player player, Location lastLocation, Location to)
    {
        if(lastLocation == null) return true;
        Flag flag = this.GetFlagInstanceAtLocation(to, player);
        if(flag == null) return true;

        if(flag == this.GetFlagInstanceAtLocation(lastLocation, player)) return true;

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        String [] commandLines = flag.parameters.replace("%owner%", playerData.lastClaim.getOwnerName()).replace("%name%", player.getName()).replace("%uuid%", player.getUniqueId().toString()).split(";");
        for(String commandLine : commandLines)
        {
            GPFlags.logFlagCommands("Entrance command: " + commandLine);
            Bukkit.getServer().dispatchCommand(player, commandLine);
        }

        return true;
    }

    public FlagDef_EnterPlayerCommand(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }

    @Override
    public String getName()
    {
        return "EnterPlayerCommand";
    }

    @Override
    public SetFlagResult ValidateParameters(String parameters)
    {
        if(parameters.isEmpty())
        {
            return new SetFlagResult(false, new MessageSpecifier(Messages.PlayerCommandRequired));
        }

        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.AddedEnterCommand, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage()
    {
        return new MessageSpecifier(Messages.RemovedEnterCommand);
    }
}
