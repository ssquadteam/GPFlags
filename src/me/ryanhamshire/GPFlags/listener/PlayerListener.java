package me.ryanhamshire.GPFlags.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;

public class PlayerListener implements Listener {

	private HashMap<Player, Boolean> fallingPlayers = new HashMap<>();

	@EventHandler
	public void onFall(EntityDamageEvent e) {
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

	public void addFallingPlayer(Player player) {
		this.fallingPlayers.put(player, true);
	}

}
