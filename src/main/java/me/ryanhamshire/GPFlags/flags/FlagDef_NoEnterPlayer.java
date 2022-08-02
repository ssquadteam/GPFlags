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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_NoEnterPlayer extends PlayerMovementFlagDefinition {

    public FlagDef_NoEnterPlayer(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public boolean allowMovement(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (player.hasPermission("gpflags.bypass.noenter")) return true;

        Flag flag = this.getFlagInstanceAtLocation(to, player);
        if (flag == null) return true;
        if (!flag.parameters.toUpperCase().contains(player.getName().toUpperCase())) return true;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(to, false, null);
        if (player.getName().equalsIgnoreCase(claim.getOwnerName())) return true;
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        if (playerData.ignoreClaims) return true;
        Util.sendClaimMessage(player, TextMode.Err, Messages.NoEnterPlayerMessage);
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;
        if (!flag.parameters.toUpperCase().contains(player.getName().toUpperCase())) return;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);
        if (player.getName().equalsIgnoreCase(claim.getOwnerName())) return;
        Util.sendClaimMessage(player, TextMode.Err, Messages.NoEnterPlayerMessage);
        GriefPrevention.instance.ejectPlayer(player);
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
