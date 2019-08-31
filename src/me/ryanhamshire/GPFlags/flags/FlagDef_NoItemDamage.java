package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
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

	public FlagDef_NoItemDamage(FlagManager manager, GPFlags plugin) {
		super(manager, plugin);
	}

	@Override
	public String getName() {
		return "NoItemDamage";
	}

	@Override
	public MessageSpecifier getSetMessage(String parameters) {
		return new MessageSpecifier(Messages.EnabledNoItemDamage);
	}

	@Override
	public MessageSpecifier getUnSetMessage() {
		return new MessageSpecifier(Messages.DisabledNoItemDamage);
	}

	@Override
	public List<FlagType> getFlagType() {
		return Arrays.asList(FlagType.WORLD, FlagType.CLAIM, FlagType.SERVER);
	}
}
