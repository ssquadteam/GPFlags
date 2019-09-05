package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_OwnerMemberFly extends PlayerMovementFlagDefinition implements Listener {

    @Override
    public boolean allowMovement(Player player, Location lastLocation) {
        if (lastLocation == null) return true;
        Location to = player.getLocation();
        Flag flag = this.GetFlagInstanceAtLocation(to, player);
        Flag ownerFly = GPFlags.getInstance().getFlagManager().getFlagDefinitionByName("OwnerFly").GetFlagInstanceAtLocation(to, player);

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(to, false, playerData.lastClaim);

        if (flag == null && ownerFly == null) {
            if (claim != null) {
                Flag noFlight = GPFlags.getInstance().getFlagManager().getFlag(claim, GPFlags.getInstance().getFlagManager().getFlagDefinitionByName("NoFlight"));
                if (noFlight != null && !noFlight.getSet()) {
                    return true;
                }
            }
            GameMode mode = player.getGameMode();
            if (mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR && player.isFlying() &&
                    !player.hasPermission("gpflags.bypass.fly")) {
                Block block = player.getLocation().getBlock();
                while (block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER) {
                    block = block.getRelative(BlockFace.DOWN);
                }
                player.setAllowFlight(false);
                if (player.getLocation().getY() - block.getY() >= 4) {
                    GPFlags.getInstance().getPlayerListener().addFallingPlayer(player);
                }
                GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
                return true;
            }
            if (player.getAllowFlight() && mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR &&
                    !player.hasPermission("gpflags.bypass.fly")) {
                player.setAllowFlight(false);
                GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
            }
            return true;
        }
        if (flag == this.GetFlagInstanceAtLocation(lastLocation, player)) return true;
        if (flag == null) return true;
        if (claim == null) return true;

        if (claim.allowAccess(player) == null) {
            player.setAllowFlight(true);
            GPFlags.sendMessage(player, TextMode.Success, Messages.EnterFlightEnabled);
            return true;
        } else {
            GameMode mode = player.getGameMode();
            if (mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR && player.isFlying() &&
                    !player.hasPermission("gpflags.bypass.fly")) {
                GPFlags.getInstance().getPlayerListener().addFallingPlayer(player);
                player.setAllowFlight(false);
                GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
            }
            if (player.getAllowFlight() && mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR &&
                    !player.hasPermission("gpflags.bypass.fly")) {
                player.setAllowFlight(false);
                GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
            }
        }
        return true;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        Material below = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);

        if (flag != null && claim.allowAccess(player) == null) {
            player.setAllowFlight(true);
            if (below == Material.AIR) {
                player.setFlying(true);
            }
        }
    }

    public FlagDef_OwnerMemberFly(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }


    @Override
    public String getName() {
        return "OwnerMemberFly";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.OwnerMemberFlightEnabled);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.OwnerMemberFlightDisabled);
    }

}
