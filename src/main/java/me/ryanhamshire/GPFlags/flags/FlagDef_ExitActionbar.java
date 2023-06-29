package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlagDef_ExitActionbar extends PlayerMovementFlagDefinition {

    public FlagDef_ExitActionbar(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onChangeClaim(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (lastLocation == null) return;
        Flag flag = this.getFlagInstanceAtLocation(lastLocation, player);
        if (flag == null) return;

        // get specific ExitMessage flag of origin claim and EnterMessage flag of destination claim
        Flag flagFrom = plugin.getFlagManager().getFlag(claimFrom, this);
        Flag flagToEnter = plugin.getFlagManager().getFlag(claimTo, plugin.getFlagManager().getFlagDefinitionByName("EnterActionbar"));

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

            // moving to different claim with the same message
            Flag flagTo = plugin.getFlagManager().getFlag(claimTo, this);
            if (flagTo != null && flagTo.parameters.equals(flagFrom.parameters)) {
                if (claimFrom.getOwnerName().equals(claimTo.getOwnerName())) return;
            }
        }

        String message = flag.parameters;
        if (claimFrom != null) {
            message = message.replace("%owner%", claimFrom.getOwnerName());
        }
        message = message.replace("%name%", player.getName());

        player.sendActionBar(Util.getColString(message));
    }

    @Override
    public String getName() {
        return "ExitActionbar";
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
        return new MessageSpecifier(Messages.AddedExitMessage, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.RemovedExitMessage);
    }

}
