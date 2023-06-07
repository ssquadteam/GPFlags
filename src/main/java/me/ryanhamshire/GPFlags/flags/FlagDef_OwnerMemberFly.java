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
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class FlagDef_OwnerMemberFly extends PlayerMovementFlagDefinition implements Listener {

    public FlagDef_OwnerMemberFly(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onFlagSet(Claim claim, String param) {
        for (Player p : Util.getPlayersIn(claim)) {
            handleFlight(p);
        }
    }

    @Override
    public void onFlagUnset(Claim claim) {
        for (Player p : Util.getPlayersIn(claim)) {
            if (!Util.canFly(p)) {
                if (p.isFlying()) {
                    Block block = p.getLocation().getBlock();
                    while (block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER) {
                        block = block.getRelative(BlockFace.DOWN);
                    }
                    if (p.getLocation().getY() - block.getY() >= 4) {
                        GPFlags.getInstance().getPlayerListener().addFallingPlayer(p);
                    }
                }
                p.setAllowFlight(false);
                Util.sendClaimMessage(p, TextMode.Warn, Messages.ExitFlightDisabled);
            }
        }
    }

    @Override
    public void onChangeClaim(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claim) {
        if (lastLocation == null) return;
        Flag flag = getFlagInstanceAtLocation(to, player);
        Flag ownerFly = GPFlags.getInstance().getFlagManager().getFlag(claim, "OwnerFly");
        if (Util.canFly(player)) return;

        // When entering a new region without the flags set
        if (flag == null && ownerFly == null) {
            // If noflight flag exists here, then let that handle disabling flight.
            if (claim != null) {
                Flag noFlight = GPFlags.getInstance().getFlagManager().getFlag(claim, GPFlags.getInstance().getFlagManager().getFlagDefinitionByName("NoFlight"));
                if (noFlight != null && !noFlight.getSet()) {
                    return;
                }
            }

            // If flying, drop the player
            if (player.isFlying()) {
                Block block = player.getLocation().getBlock();
                while (block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER) {
                    block = block.getRelative(BlockFace.DOWN);
                }
                player.setAllowFlight(false);
                if (player.getLocation().getY() - block.getY() >= 4) {
                    GPFlags.getInstance().getPlayerListener().addFallingPlayer(player);
                }
                Util.sendClaimMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
                return;
            }
            // Disable their flight
            if (player.getAllowFlight()) {
                player.setAllowFlight(false);
                Util.sendClaimMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
            }
            return;
        }

        if (flag == this.getFlagInstanceAtLocation(lastLocation, player)) return;
        if (flag == null) return;
        if (claim == null) return;

        // If you have trust in the claim, enable flight
        if (Util.canAccess(claim, player)) {
            Bukkit.getScheduler().runTaskLater(GPFlags.getInstance(), () -> {
                if (!player.getAllowFlight()) {
                    Util.sendClaimMessage(player, TextMode.Success, Messages.EnterFlightEnabled);
                }
                player.setAllowFlight(true);
            }, 1);
            return;
        }

        // If you don't have trust in the claim, disable flight
        if (player.isFlying()) {
            GPFlags.getInstance().getPlayerListener().addFallingPlayer(player);
            player.setAllowFlight(false);
            Util.sendClaimMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
        }
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            Util.sendClaimMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onJoin(PlayerJoinEvent event) {
        handleFlight(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> handleFlight(player), 1);
    }

    private void handleFlight(Player player) {
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        Material below = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);

        if (flag == null) return;
        if (claim == null) return;
        if (!Util.canAccess(claim, player)) return;

        if (!player.getAllowFlight()) {
            Util.sendClaimMessage(player, TextMode.Success, Messages.EnterFlightEnabled);
        }
        player.setAllowFlight(true);
        if (below == Material.AIR) {
            player.setFlying(true);
        }
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
