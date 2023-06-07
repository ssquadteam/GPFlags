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
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

public class FlagDef_OwnerFly extends PlayerMovementFlagDefinition implements Listener {

    public FlagDef_OwnerFly(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onFlagSet(Claim claim, String param) {
        UUID uuid = claim.getOwnerID();
        Player owner = Bukkit.getPlayer(uuid);
        if (owner == null) return;
        if (!claim.contains(owner.getLocation(), false, false)) return;
        handleFlight(owner);
    }

    @Override
    public void onFlagUnset(Claim claim) {
        UUID uuid = claim.getOwnerID();
        Player owner = Bukkit.getPlayer(uuid);
        if (owner == null) return;
        handleFlight(owner);
    }

    @Override
    public void onChangeClaim(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claim) {
        if (lastLocation == null) return;
        Flag flag = getFlagInstanceAtLocation(to, player);
        Flag ownerMember = GPFlags.getInstance().getFlagManager().getFlag(claim, "OwnerMemberFly");

        // if you shouldn't be able to fly anymore
        if (flag == null && ownerMember == null) {

            // if youre going into a noflight claim, let that handle messages and flight
            if (claim != null) {
                Flag noFlight = GPFlags.getInstance().getFlagManager().getFlag(claim, GPFlags.getInstance().getFlagManager().getFlagDefinitionByName("NoFlight"));
                if (noFlight != null && !noFlight.getSet()) {
                    return;
                }
            }


            if (player.isFlying() && !canFly(player)) {
                Block block = player.getLocation().getBlock();
                while (block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER) {
                    block = block.getRelative(BlockFace.DOWN);
                }
                player.setAllowFlight(false);
                if (to.getY() - block.getY() >= 4) {
                    GPFlags.getInstance().getPlayerListener().addFallingPlayer(player);
                }
                Util.sendClaimMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
                return;
            }
            if (player.getAllowFlight() && !canFly(player)) {
                player.setAllowFlight(false);
                Util.sendClaimMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
            }
            return;
        }
        if (flag == this.getFlagInstanceAtLocation(lastLocation, player)) return;

        if (claim == null) return;
        if (!claim.getOwnerName().equalsIgnoreCase(player.getName())) {
            if (!canFly(player)) {
                player.setAllowFlight(false);
                Util.sendClaimMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
            }
            return;
        }

        Bukkit.getScheduler().runTaskLater(GPFlags.getInstance(), () -> {
            if (!player.getAllowFlight()) {
                Util.sendClaimMessage(player, TextMode.Success, Messages.EnterFlightEnabled);
            }
            player.setAllowFlight(true);
        }, 1);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean canFly(Player player) {
        GameMode mode = player.getGameMode();
        return mode == GameMode.SPECTATOR || mode == GameMode.CREATIVE || player.hasPermission("gpflags.bypass.fly");
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
        if (flag != null && claim != null && claim.getOwnerName() != null && claim.getOwnerName().equalsIgnoreCase(player.getName())) {
            player.setAllowFlight(true);
            Util.sendClaimMessage(player, TextMode.Success, Messages.EnterFlightEnabled);
            if (below == Material.AIR) {
                player.setFlying(true);
            }
        }
    }

    @Override
    public String getName() {
        return "OwnerFly";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.OwnerFlightEnabled);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.OwnerFlightDisabled);
    }

}

