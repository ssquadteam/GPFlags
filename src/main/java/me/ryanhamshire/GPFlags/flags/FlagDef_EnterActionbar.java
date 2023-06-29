package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;


public class FlagDef_EnterActionbar extends PlayerMovementFlagDefinition {

    public FlagDef_EnterActionbar(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onChangeClaim(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (lastLocation == null) return;
        Flag flag = this.getFlagInstanceAtLocation(to, player);
        if (flag == null) return;
        Flag oldFlag = this.getFlagInstanceAtLocation(lastLocation, player);
        if (flag == oldFlag) return;
        if (oldFlag != null && flag.parameters.equals(oldFlag.parameters)) {
            if (claimFrom != null && claimTo != null && claimFrom.getOwnerName().equals(claimTo.getOwnerName())) return;
        }

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        String message = flag.getParameters()
                .replace("%name%", player.getName())
                .replace("%uuid%", player.getUniqueId().toString());
        if (playerData.lastClaim != null) {
            message = message.replace("%owner%", playerData.lastClaim.getOwnerName());
        }
        player.sendActionBar(Util.getColString(message));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim lastClaim = playerData.lastClaim;
        String message = flag.getParameters()
                .replace("%name%", player.getName())
                .replace("%uuid%", player.getUniqueId().toString());
        if (lastClaim != null) {
                message = message.replace("%owner%", playerData.lastClaim.getOwnerName());
        }
        player.sendActionBar(Util.getColString(message));
    }

    @Override
    public String getName() {
        return "EnterActionbar";
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
        if (parameters.isEmpty()) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.ActionbarRequired));
        }
        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.AddedEnterActionbar, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.RemovedEnterActionbar);
    }

}
