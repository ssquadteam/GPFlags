package me.ryanhamshire.GPFlags;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoItemDamage extends FlagDefinition {

	@EventHandler
	private void onItemDamage(PlayerItemDamageEvent event) {
		Flag flag = this.GetFlagInstanceAtLocation(event.getPlayer().getLocation(), null);
		if (flag == null) return;
		event.setCancelled(true);
	}

	FlagDef_NoItemDamage(FlagManager manager, GPFlags plugin) {
		super(manager, plugin);
	}

	@Override
	String getName() {
		return "NoItemDamage";
	}

	@Override
	MessageSpecifier GetSetMessage(String parameters) {
		return new MessageSpecifier(Messages.EnabledNoItemDamage);
	}

	@Override
	MessageSpecifier GetUnSetMessage() {
		return new MessageSpecifier(Messages.DisabledNoItemDamage);
	}

	@Override
	List<FlagType> getFlagType() {
		return Arrays.asList(FlagType.WORLD, FlagType.CLAIM, FlagType.SERVER);
	}
}
