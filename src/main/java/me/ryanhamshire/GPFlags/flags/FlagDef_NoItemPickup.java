package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoItemPickup extends FlagDefinition {

    public FlagDef_NoItemPickup(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity());
            if (player.hasPermission("gpflags.bypass.noitempickup")) return;

            Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
            if (flag == null) return;

            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);
            if (Util.isClaimOwner(claim, player) && player.hasPermission("gpflags.bypass.noitempickup.ownclaim")) return;

            event.setCancelled(true);
        }
    }

    @Override
    public String getName() {
        return "NoItemPickup";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoItemPickup);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoItemPickup);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.SERVER, FlagType.WORLD);
    }

}
