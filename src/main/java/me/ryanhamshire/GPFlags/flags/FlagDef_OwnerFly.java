package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.MessagingUtil;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FlagDef_OwnerFly extends FlagDefinition {

    public FlagDef_OwnerFly(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onFlagSet(Claim claim, String param) {
        UUID uuid = claim.getOwnerID();
        Player owner = Bukkit.getPlayer(uuid);
        if (owner == null) return;
        Location location = owner.getLocation();
        if (!claim.contains(location, false, false)) return;
        FlightManager.managePlayerFlight(owner, location);
    }

    @Override
    public void onFlagUnset(Claim claim) {
        UUID uuid = claim.getOwnerID();
        Player owner = Bukkit.getPlayer(uuid);
        if (owner == null) {
            return;
        };
        Location location = owner.getLocation();
        if (!claim.contains(location, false, false)) {
            return;
        }
        FlightManager.managePlayerFlight(owner, owner.getLocation());
    }

    public static boolean letPlayerFly(Player player, Location location) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
        if (claim == null) return false;
        Flag flag = GPFlags.getInstance().getFlagManager().getFlag(claim, "OwnerFly");
        if (flag == null) return false;
        if (!flag.getSet()) return false;
        return Util.canEdit(player, claim);
    }

    @Override
    public String getName() {
        return "OwnerFly";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.OwnerFlightEnabled);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.OwnerFlightDisabled);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }


}

