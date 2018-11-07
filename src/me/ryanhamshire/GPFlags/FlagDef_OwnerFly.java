package me.ryanhamshire.GPFlags;

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

public class FlagDef_OwnerFly extends PlayerMovementFlagDefinition implements Listener
{
    @Override
    public boolean allowMovement(Player player, Location lastLocation)
    {
        if(lastLocation == null) return true;
        Location to = player.getLocation();
        Flag flag = this.GetFlagInstanceAtLocation(to, player);
        if(flag == null) {
            GameMode mode = player.getGameMode();
            if(mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR && player.isFlying() &&
                    !player.hasPermission("gpflags.bypass.fly")) {
                Block block = player.getLocation().getBlock();
                while(block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER) {
                    block = block.getRelative(BlockFace.DOWN);
                }

                player.teleport(block.getRelative(BlockFace.UP).getLocation());
                player.setAllowFlight(false);
                GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
            }

            if(player.getAllowFlight() && mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR &&
                    !player.hasPermission("gpflags.bypass.fly")) {
                player.setAllowFlight(false);
                GPFlags.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
            }
            return true;
        }

        if(flag == this.GetFlagInstanceAtLocation(lastLocation, player)) return true;

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        if(!playerData.lastClaim.getOwnerName().equalsIgnoreCase(player.getName())) return true;

        player.setAllowFlight(true);
        GPFlags.sendMessage(player, TextMode.Success, Messages.EnterFlightEnabled);

        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        Material below = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();

        if(flag != null) {
            player.setAllowFlight(true);
            if(below == Material.AIR) {
                player.setFlying(true);
            }
        }
    }

    public FlagDef_OwnerFly(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    String getName() {
        return "OwnerFly";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters) {
        return new MessageSpecifier(Messages.OwnerFlightEnabled);
    }

    @Override
    MessageSpecifier GetUnSetMessage() {
        return new MessageSpecifier(Messages.OwnerFlightDisabled);
    }

}

