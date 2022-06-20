package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoElytra extends PlayerMovementFlagDefinition {


    public FlagDef_NoElytra(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public boolean allowMovement(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (lastLocation == null) return true;
        Flag flag = this.getFlagInstanceAtLocation(to, player);
        if (flag == null) return true;
        if (!player.isGliding()) return true;
        player.setGliding(false);
        return true;
    }

    @EventHandler
    private void onToggleElytra(EntityToggleGlideEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (player.hasPermission("gpflags.bypass.noelytra")) return;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);
        if (claim.getOwnerID().equals(player.getUniqueId()) && player.hasPermission("gpflags.bypass.noelytra.ownclaim")) return;

        if (this.getFlagInstanceAtLocation(entity.getLocation(), null) == null) return;

        if (event.isGliding()) {
            event.setCancelled(true);
        }
    }

    @Override
    public String getName() {
        return "NoElytra";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoElytra, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoElytra);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
