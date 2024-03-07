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
        Flag flag = this.getFlagInstanceAtLocation(to, player);
        if (flag == null) return;
        Flag oldFlag = this.getFlagInstanceAtLocation(lastLocation, player);
        if (flag == oldFlag) return;
        if (oldFlag != null && flag.parameters.equals(oldFlag.parameters)) {
            if (claimFrom != null && claimTo != null && claimFrom.getOwnerName().equals(claimTo.getOwnerName())) return;
        }

        final PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        final String owner = playerData.lastClaim != null ? playerData.lastClaim.getOwnerName() : "N/A";

        final Title title = Title.title(
            Component.text("Leaving Claim", NamedTextColor.GREEN),
            Component.text(String.format("Owned by: %s", owner), TextColor.color(204, 204, 204))
        );
        player.showTitle(title);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim lastClaim = playerData.lastClaim;

        final String owner = lastClaim != null ? lastClaim.getOwnerName() : "N/A";
        final Title title = Title.title(
            Component.text("Leaving Claim", NamedTextColor.GREEN),
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
