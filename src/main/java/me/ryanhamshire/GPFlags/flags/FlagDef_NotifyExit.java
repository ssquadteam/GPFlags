package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlagDef_NotifyExit extends PlayerMovementFlagDefinition {

    public FlagDef_NotifyExit(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onChangeClaim(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (lastLocation == null) return;
        Flag flag = this.getFlagInstanceAtLocation(lastLocation, player);
        if (flag == null) return;

        // get specific ExitMessage flag of origin claim and EnterMessage flag of destination claim
        Flag flagFrom = plugin.getFlagManager().getFlag(claimFrom, this);
        Flag flagToEnter = plugin.getFlagManager().getFlag(claimTo, plugin.getFlagManager().getFlagDefinitionByName("NotifyEnter"));

        // Don't repeat the exit message of a claim in certain cases
        if (claimFrom != null && claimTo != null) {
            // moving to parent claim, and the sub claim does not have its own exit message
            if (claimFrom.parent == claimTo && (flagFrom == null || !flagFrom.getSet())) {
                return;
            }
            // moving to sub-claim, and the sub claim does not have its own enter message
            if (claimTo.parent == claimFrom && (flagToEnter == null || !flagToEnter.getSet())) {
                return;
            }
        }

        if (claimTo == null) return;
        Player owner = Bukkit.getPlayer(claimTo.getOwnerID());
        if (owner == null) return;
        if (owner.getName().equals(player.getName())) return;
        if (!owner.canSee(player)) return;
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        String param = flag.parameters;
        if (param == null || param.isEmpty()) {
            param = "claim " + claimTo.getID();
        }
        Util.sendClaimMessage(owner, TextMode.Info, Messages.NotifyEnter, player.getName(), param);
    }


    @Override
    public String getName() {
        return "NotifyExit";
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNotifyExit, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNotifyExit);
    }

}
