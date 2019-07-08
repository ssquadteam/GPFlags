package me.ryanhamshire.GPFlags;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlagDef_NoEnterPlayer extends PlayerMovementFlagDefinition {

    @Override
    public boolean allowMovement(Player player, Location lastLocation) {
        if (player.hasPermission("gpflags.bypass")) return true;

        Location to = player.getLocation();
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
    String getName() {
        return "NoEnterPlayer";
    }

    @Override
    SetFlagResult ValidateParameters(String parameters) {
        if (parameters.isEmpty()) {
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