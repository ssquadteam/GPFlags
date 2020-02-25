package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FlagDef_ExitMessage extends PlayerMovementFlagDefinition {

    private String prefix;

    public FlagDef_ExitMessage(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
        this.prefix = plugin.getFlagsDataStore().getMessage(Messages.EnterExitPrefix);
    }

    @Override
    public boolean allowMovement(Player player, Location lastLocation, Location to) {
        if (lastLocation == null) return true;
        Flag flag = this.GetFlagInstanceAtLocation(lastLocation, player);
        if (flag == null) return true;

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(lastLocation, true, null);
        String message = flag.parameters;
        if (claim != null) {
            message = message.replace("%owner%", claim.getOwnerName()).replace("%name%", player.getName());
        }

        GPFlags.sendMessage(player, TextMode.Info, prefix + message);

        return true;
    }

    @Override
    public String getName() {
        return "ExitMessage";
    }

    @Override
    public SetFlagResult ValidateParameters(String parameters) {
        if (parameters.isEmpty()) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.MessageRequired));
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
