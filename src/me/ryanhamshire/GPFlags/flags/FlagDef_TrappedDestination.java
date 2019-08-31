package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;

import me.ryanhamshire.GriefPrevention.events.SaveTrappedPlayerEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_TrappedDestination extends FlagDefinition {
    @EventHandler
    public void onPlayerDeath(SaveTrappedPlayerEvent event) {
        Flag flag = this.GetFlagInstanceAtLocation(event.getClaim().getLesserBoundaryCorner(), null);
        if (flag == null) return;

        String[] params = flag.getParametersArray();
        World world = Bukkit.getServer().getWorld(params[0]);
        Location location = new Location(world, Integer.valueOf(params[1]), Integer.valueOf(params[2]), Integer.valueOf(params[3]));

        event.setDestination(location);
    }

    public FlagDef_TrappedDestination(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public String getName() {
        return "TrappedDestination";
    }

    @Override
    public SetFlagResult ValidateParameters(String parameters) {
        String[] params = parameters.split(" ");

        if (params.length != 4) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.LocationRequired));
        }

        World world = Bukkit.getWorld(params[0]);
        if (world == null) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.WorldNotFound));
        }

        try {
            Integer.valueOf(params[1]);
            Integer.valueOf(params[2]);
            Integer.valueOf(params[3]);
        } catch (NumberFormatException e) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.LocationRequired));
        }

        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
	public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableTrappedDestination);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableTrappedDestination);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }
}
