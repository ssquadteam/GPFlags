package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.MessagingUtil;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.event.EventPriority;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoFlight extends FlagDefinition {

    public FlagDef_NoFlight(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        FlightManager.managePlayerFlight(player, player.getLocation());
    }


    public static boolean letPlayerFly(Player player, Location location, Claim claim) {
        Flag flag = GPFlags.getInstance().getFlagManager().getInheritedLogicalFlag(location, "NoFlight", claim);
        if (flag == null) return true;
        return Util.shouldBypass(player, claim, flag);
    }


    @Override
    public String getName() {
        return "NoFlight";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoFlight);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoFlight);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }
}
