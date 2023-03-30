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
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_NoEnterPlayer extends PlayerMovementFlagDefinition {

    public FlagDef_NoEnterPlayer(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onFlagSet(Claim claim, String string) {
        Flag flag = this.getFlagInstanceAtLocation(claim.getLesserBoundaryCorner(), null);
        for (Player player : Util.getPlayersIn(claim)) {
            if (!isAllowed(player, claim, flag)) {
                GriefPrevention.instance.ejectPlayer(player);
                Util.sendClaimMessage(player, TextMode.Err, Messages.NoEnterPlayerMessage);
            }
        }
    }

    @Override
    public boolean allowMovement(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        Flag flag = this.getFlagInstanceAtLocation(to, player);
        if (flag == null) return true;

        if (isAllowed(player, claimTo, flag)) return true;

        Util.sendClaimMessage(player, TextMode.Err, Messages.NoEnterPlayerMessage);
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Location loc = player.getLocation();

        Flag flag = this.getFlagInstanceAtLocation(loc, player);
        if (flag == null) return;

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);
        if (isAllowed(player, claim, flag)) return;

        Util.sendClaimMessage(player, TextMode.Err, Messages.NoEnterPlayerMessage);
        GriefPrevention.instance.ejectPlayer(player);
    }

    public boolean isAllowed(Player p, Claim c, Flag f) {
        if (c == null) return true;
        if (p.hasPermission("gpflags.bypass.noenter")) return true;
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(p.getUniqueId());
        if (playerData.ignoreClaims) return true;
        String playername = p.getName();
        if (playername.equalsIgnoreCase(c.getOwnerName())) return true;

        String[] paramArray = f.getParametersArray();
        for (String nameOrUUID : paramArray) {
            if (nameOrUUID.equalsIgnoreCase(playername)) return false;
            if (nameOrUUID.equalsIgnoreCase(String.valueOf(p.getUniqueId()))) return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "NoEnterPlayer";
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
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
