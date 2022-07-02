package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoFlight extends TimedPlayerFlagDefinition {

    public FlagDef_NoFlight(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public long getPlayerCheckFrequency_Ticks() {
        return 30L;
    }

    @Override
    public void processPlayer(Player player) {
        if(! player.isFlying()) return;
        handleFlyAttempt(player);
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        event.setCancelled(handleFlyAttempt(event.getPlayer()));
    }
    
    @EventHandler
    public void onFlyCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;
        
        String[] args = event.getMessage().split(" ");
        
        if (args.length == 1 && args[0].contains("fly")) {
            event.setCancelled(handleFlyAttempt(event.getPlayer()));
        }
    }
    
    /**
     * @param player
     * @return True if attempt was stopped
     */
    private boolean handleFlyAttempt(Player player) {
        if (player.getGameMode() == GameMode.SPECTATOR) return false;
        
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) {
            return false;
        }
        
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);

        if (Util.shouldBypass(player, claim, flag)) return false;

            Flag ownerFly = GPFlags.getInstance().getFlagManager()
                .getFlagDefinitionByName("OwnerFly")
                .getFlagInstanceAtLocation(player.getLocation(), player);
        Flag ownerMember = GPFlags.getInstance().getFlagManager()
                .getFlagDefinitionByName("OwnerMemberFly")
                .getFlagInstanceAtLocation(player.getLocation(), player);
        
        if (ownerFly != null && claim.ownerID.equals(player.getUniqueId())) {
            return false;
        }
        
        if (ownerMember != null)  {
            return false;
        }
        
        Util.sendClaimMessage(player, TextMode.Err, Messages.CantFlyHere);
        
        player.setFlying(false);
        teleportToFloor(player);
        
        return true;
    }
    
    private void teleportToFloor(Player player) {
        Location floor = player.getLocation();
        while (floor.getY() > 2 && (! floor.getBlock().getType().isSolid()) && floor.getBlock().getType() != Material.WATER) {
            floor = floor.subtract(0, 0.4, 0);
        }
        player.setFallDistance(0);
        player.teleport(floor.add(0, 0.5, 0));
    }

    @Override
    public String getName() {
        return "NoFlight";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoFlight);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoFlight);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }
}
