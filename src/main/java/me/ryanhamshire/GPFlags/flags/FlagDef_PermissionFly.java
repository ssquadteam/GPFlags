package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;


public class FlagDef_PermissionFly extends FlagDefinition {

    public FlagDef_PermissionFly(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onFlagSet(Claim claim, String param) {
        for (Player p : Util.getPlayersIn(claim)) {
            FlightManager.managePlayerFlight(p, p.getLocation());
        }
    }

    @Override
    public void onFlagUnset(Claim claim) {
        for (Player p : Util.getPlayersIn(claim)) {
            FlightManager.managePlayerFlight(p, p.getLocation());
        }
    }

    public static boolean letPlayerFly(Player player, Location location) {
        Flag flag = GPFlags.getInstance().getFlagManager().getFlag(location, "PermissionFly");
        if (flag == null) return false;
        if (!flag.getSet()) return false;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
        return Util.shouldBypass(player, claim, flag);
    }

    @Override
    public String getName() {
        return "PermissionFly";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.PermissionFlightEnabled);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.PermissionFlightDisabled);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}

