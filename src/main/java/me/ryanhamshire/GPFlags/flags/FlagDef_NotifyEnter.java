package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_NotifyEnter extends PlayerMovementFlagDefinition {

    public FlagDef_NotifyEnter(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onChangeClaim(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (lastLocation == null) return;
        Flag flag = this.getFlagInstanceAtLocation(to, player);
        if (flag == null) return;

        // get specific EnterMessage flag of destination claim and ExitMessage flag of origin claim
        Flag flagTo = plugin.getFlagManager().getFlag(claimTo, this);
        Flag flagFromExit = plugin.getFlagManager().getFlag(claimFrom, plugin.getFlagManager().getFlagDefinitionByName("NotifyExit"));

        // Don't repeat the enter message of a claim in certain cases
        if (claimFrom != null && claimTo != null) {
            // moving to sub-claim, and the sub claim does not have its own enter message
            if (claimTo.parent == claimFrom && (flagTo == null || !flagTo.getSet())) {
                return;
            }
            // moving to parent claim, and the sub claim does not have its own exit message
            if (claimFrom.parent == claimTo && (flagFromExit == null || !flagFromExit.getSet())) {
                return;
            }
        }

        if (claimTo == null) return;
        Player owner = Bukkit.getPlayer(claimTo.getOwnerID());
        if (owner == null) return;
        if (owner.getName().equals(player.getName())) return;
        if (!owner.canSee(player)) return;
        String param = flag.parameters;
        if (param == null || param.isEmpty()) {
            param = "claim " + claimTo.getID();
        }
        Util.sendClaimMessage(owner, TextMode.Info, Messages.NotifyEnter, player.getName(), param);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);
        if (claim == null) return;
        Player owner = Bukkit.getPlayer(claim.getOwnerID());
        if (owner == null) return;
        if (owner.getName().equals(player.getName())) return;
        String param = flag.parameters;
        if (param == null || param.isEmpty()) {
            param = "claim " + claim.getID();
        }
        Util.sendClaimMessage(owner, TextMode.Info, Messages.NotifyEnter, player.getName(), param);

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
