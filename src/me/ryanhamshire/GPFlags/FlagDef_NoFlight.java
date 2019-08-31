package me.ryanhamshire.GPFlags;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoFlight extends TimedPlayerFlagDefinition {

    @Override
    long getPlayerCheckFrequency_Ticks() {
        return 30L;
    }

    @Override
    void processPlayer(Player player) {
        if (!player.isFlying()) return;
        if (player.hasPermission("gpflags.bypass")) return;

        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;

        Flag ownerFly = GPFlags.instance.flagManager.GetFlagDefinitionByName("OwnerFly").GetFlagInstanceAtLocation(player.getLocation(), player);
        Flag ownerMember = GPFlags.instance.flagManager.GetFlagDefinitionByName("OwnerMemberFly").GetFlagInstanceAtLocation(player.getLocation(), player);
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);
        if (ownerFly != null && claim.ownerID.toString().equalsIgnoreCase(player.getUniqueId().toString())) return;
        if (ownerMember != null && claim.allowAccess(player) == null) return;

        GPFlags.sendMessage(player, TextMode.Err, Messages.CantFlyHere);
        player.setFlying(false);
        GameMode mode = player.getGameMode();
        if (mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR) {
            Block block = player.getLocation().getBlock();
            while (block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER) {
                block = block.getRelative(BlockFace.DOWN);
            }

            player.setAllowFlight(false);
            if (player.getLocation().getY() - block.getY() >= 4) {
                GPFlags.instance.getPlayerListener().addFallingPlayer(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.isFlying()) return;
        if (player.hasPermission("gpflags.bypass")) return;

        Flag ownerFly = GPFlags.instance.flagManager.GetFlagDefinitionByName("OwnerFly").GetFlagInstanceAtLocation(player.getLocation(), player);
        Flag ownerMember = GPFlags.instance.flagManager.GetFlagDefinitionByName("OwnerMemberFly").GetFlagInstanceAtLocation(player.getLocation(), player);
        if (ownerFly != null || ownerMember != null) return;

        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;

        GPFlags.sendMessage(player, TextMode.Err, Messages.CantFlyHere);
        event.setCancelled(true);
        player.setAllowFlight(false);
    }

    FlagDef_NoFlight(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    String getName() {
        return "NoFlight";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoFlight);
    }

    @Override
    MessageSpecifier GetUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoFlight);
    }

    @Override
    List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
