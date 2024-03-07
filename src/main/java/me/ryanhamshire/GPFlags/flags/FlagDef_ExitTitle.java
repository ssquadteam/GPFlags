package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_ExitTitle extends PlayerMovementFlagDefinition {

    public FlagDef_ExitTitle(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onChangeClaim(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (lastLocation == null) return;
        Flag flag = this.getFlagInstanceAtLocation(lastLocation, player);
        if (flag == null) return;

        // get specific ExitMessage flag of origin claim and EnterMessage flag of destination claim
        Flag flagFrom = plugin.getFlagManager().getFlag(claimFrom, this);
        Flag flagTo = plugin.getFlagManager().getFlag(claimTo, this);

        // Don't repeat the exit message of a claim in certain cases
        if (claimFrom != null && claimTo != null) {
            // moving to parent claim, and the sub claim does not have its own exit message
            if (claimFrom.parent == claimTo && (flagFrom == null || !flagFrom.getSet())) {
                return;
            }
            // moving to sub-claim, and the sub claim does not have its own exit message
            if (claimTo.parent == claimFrom && (flagTo == null || !flagTo.getSet())) {
                return;
            }

            // moving to different claim with an entertitle
            Flag flagToEnter = plugin.getFlagManager().getFlag(claimTo, plugin.getFlagManager().getFlagDefinitionByName("EnterTitle"));
            if (flagToEnter != null) {
                return;
            }
        }

        final PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        final String owner = playerData.lastClaim != null ? playerData.lastClaim.getOwnerName() : "N/A";

        final Title title = Title.title(
            Component.text("Leaving Claim", NamedTextColor.RED),
            Component.text(String.format("Owned by: %s", owner), TextColor.color(204, 204, 204))
        );
        player.showTitle(title);
    }

    @Override
    public String getName() {
        return "ExitTitle";
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
//        if (parameters.isEmpty()) {
//            return new SetFlagResult(false, new MessageSpecifier(Messages.ActionbarRequired));
//        }
        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.AddedExitTitle, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.RemovedExitTitle);
    }

}
