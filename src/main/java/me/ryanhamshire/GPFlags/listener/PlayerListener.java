package me.ryanhamshire.GPFlags.listener;

import me.ryanhamshire.GPFlags.event.PlayerClaimBorderEvent;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

public class PlayerListener implements Listener {

	private HashMap<Player, Boolean> fallingPlayers = new HashMap<>();
	private DataStore dataStore = GriefPrevention.instance.dataStore;

	@EventHandler
	private void onFall(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		Player p = ((Player) e.getEntity());
		EntityDamageEvent.DamageCause cause = e.getCause();
		if (cause != EntityDamageEvent.DamageCause.FALL) return;
		Boolean val = fallingPlayers.get(p);
		if (val != null && val) {
			e.setCancelled(true);
			fallingPlayers.remove(p);
		}
	}

	/** Add a player to prevent fall damage under certain conditions
	 * @param player Player to add
	 */
	public void addFallingPlayer(Player player) {
		this.fallingPlayers.put(player, true);
	}

	@EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        if (event.getTo() == null) return;
        Location locTo = event.getTo();
        Location locFrom = event.getFrom();
        if (locTo.getBlockX() == locFrom.getBlockX() && locTo.getBlockY() == locFrom.getBlockY() && locTo.getBlockZ() == locFrom.getBlockZ()) return;
        Player player = event.getPlayer();
        Claim claimTo = dataStore.getClaimAt(locTo, false, null);
        Claim claimFrom = dataStore.getClaimAt(locFrom, false, null);
        if (claimTo == null && claimFrom == null) return;
        if (claimTo == claimFrom) return;
        PlayerClaimBorderEvent playerClaimBorderEvent = new PlayerClaimBorderEvent(player, claimFrom, claimTo, locFrom, locTo);
        Bukkit.getPluginManager().callEvent(playerClaimBorderEvent);
        event.setCancelled(playerClaimBorderEvent.isCancelled());
    }

}
