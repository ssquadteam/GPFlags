package me.ryanhamshire.GPFlags;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlagDef_NoEnterPlayer extends PlayerMovementFlagDefinition {

    @Override
    public boolean allowMovement(Player player, Location lastLocation) {
        if(player.hasPermission("gpflags.bypass")) return true;

        Location to = player.getLocation();
        Location from = lastLocation;

        Flag flag = this.GetFlagInstanceAtLocation(to, player);
        if(flag == null) return true;

        if(from == null || flag == this.GetFlagInstanceAtLocation(from, player)) return true;
        if(flag.parameters.contains(player.getName()))
        {
            GPFlags.sendMessage(player, TextMode.Err, Messages.NoEnterPlayerMessage);
            return false;
        }
        return true;
    }

    public FlagDef_NoEnterPlayer(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }

    @Override
    String getName() {
        return "NoEnterPlayer";
    }

    @Override
    SetFlagResult ValidateParameters(String parameters) {
        if(parameters.isEmpty())
        {
            return new SetFlagResult(false, new MessageSpecifier(Messages.PlayerRequired));
        }

        return new SetFlagResult(true, this.GetSetMessage(parameters));
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledNoEnterPlayer, parameters);

    }

    @Override
    MessageSpecifier GetUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledNoEnterPlayer);
    }
}