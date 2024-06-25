package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.MessagingUtil;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class FlagDef_NotifyEnter extends PlayerMovementFlagDefinition {

    public FlagDef_NotifyEnter(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onChangeClaim(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (lastLocation == null) return;
        Flag flag = this.getFlagInstanceAtLocation(to, player);
        if (flag == null) return;
        if (claimTo == null) return;

        // get specific EnterMessage flag of destination claim and ExitMessage flag of origin claim
        Flag flagTo = plugin.getFlagManager().getInheritedRawClaimFlag(claimTo, this.getName());
        Flag flagFromExit = plugin.getFlagManager().getInheritedRawClaimFlag(claimFrom, "NotifyExit");

        // Don't repeat the enter message of a claim in certain cases
        if (claimFrom != null) {
            // moving to sub-claim, and the sub claim does not have its own enter message
            if (claimTo.parent == claimFrom && (flagTo == null || !flagTo.getSet())) {
                return;
            }
            // moving to parent claim, and the sub claim does not have its own exit message
            if (claimFrom.parent == claimTo && (flagFromExit == null || !flagFromExit.getSet())) {
                return;
            }
        }
        if (shouldNotify(player, claimTo)) notifyEntry(flag, claimTo, player);
    }

    public boolean shouldNotify(Player p, Claim c) {
        UUID ownerID = c.getOwnerID();
        if (ownerID == null) return false;
        Player owner = Bukkit.getPlayer(ownerID);
        if (owner == null) return false;
        if (owner.getName().equals(p.getName())) return false;
        if (!owner.canSee(p)) return false;
        if (p.getGameMode() == GameMode.SPECTATOR) return false;
        if (p.hasPermission("gpflags.bypass.notifyenter")) return false;
        return true;
    }

    public void notifyEntry(Flag flag, Claim claim, Player player) {
        Player owner = Bukkit.getPlayer(claim.getOwnerID());
        if (owner == null) return;
        if (owner.getName().equals(player.getName())) return;
        String param = flag.parameters;
        if (param == null || param.isEmpty()) {
            param = "claim " + claim.getID();
        }
        MessagingUtil.sendMessage(owner, TextMode.Info, Messages.NotifyEnter, player.getName(), param);

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);
        if (claim == null) return;
        if (shouldNotify(player, claim)) notifyEntry(flag, claim, player);
    }

    @Override
    public String getName() {
        return "NotifyEnter";
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNotifyEnter, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNotifyEnter);
    }

}
