package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlagDef_NoEnterPlayer extends PlayerMovementFlagDefinition {

    @Override
    public boolean allowMovement(Player player, Location lastLocation, Location to) {
        if (player.hasPermission("gpflags.bypass")) return true;

        Location from = lastLocation;

        Flag flag = this.GetFlagInstanceAtLocation(to, player);
        if (flag == null) return true;

        if (from == null || flag == this.GetFlagInstanceAtLocation(from, player)) return true;

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(to, false, playerData.lastClaim);
        if (flag.parameters.toUpperCase().contains(player.getName().toUpperCase()) && claim.allowAccess(player) != null) {
            GPFlags.sendMessage(player, TextMode.Err, Messages.NoEnterPlayerMessage);
            return false;
        }
        return true;
    }

    public FlagDef_NoEnterPlayer(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public String getName() {
        return "NoEnterPlayer";
    }

    @Override
    public SetFlagResult ValidateParameters(String parameters) {
        if (parameters.isEmpty()) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.PlayerRequired));
        }

        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
	public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledNoEnterPlayer, parameters);

    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledNoEnterPlayer);
    }

}