package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlagDef_NoEnter extends PlayerMovementFlagDefinition {

    public FlagDef_NoEnter(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public boolean allowMovement(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (player.hasPermission("gpflags.bypass")) return true;

        Flag flag = this.getFlagInstanceAtLocation(to, player);
        if (flag == null) return true;

        if (lastLocation == null || flag == this.getFlagInstanceAtLocation(lastLocation, player)) return true;

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(to, false, playerData.lastClaim);
        if (claim.checkPermission(player, ClaimPermission.Access, null) != null) {
            Util.sendClaimMessage(player, TextMode.Err, Messages.NoEnterMessage);
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "NoEnter";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledNoEnter, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledNoEnter);
    }

}
