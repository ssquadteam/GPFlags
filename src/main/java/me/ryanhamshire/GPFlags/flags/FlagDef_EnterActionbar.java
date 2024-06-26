package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.MessagingUtil;
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
        if (claimTo == null) return;
        Flag flagTo = plugin.getFlagManager().getEffectiveFlag(to, this.getName(), claimTo);
        if (flagTo == null) return;
        Flag flagFrom = plugin.getFlagManager().getEffectiveFlag(lastLocation, this.getName(), claimFrom);
        if (flagFrom == flagTo) return;
        // moving to different claim with the same params
        if (flagFrom != null && flagFrom.parameters.equals(flagTo.parameters)) return;

        sendActionbar(flagTo, player, claimTo);
    }

    public void sendActionbar(Flag flag, Player player, Claim claim) {
        String message = flag.parameters;
        String owner = claim.getOwnerName();
        if (owner != null) {
            message = message.replace("%owner%", owner);
        }
        message = message.replace("%name%", player.getName());
        MessagingUtil.sendActionbar(player, message);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);

        sendActionbar(flag, player, claim);
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
