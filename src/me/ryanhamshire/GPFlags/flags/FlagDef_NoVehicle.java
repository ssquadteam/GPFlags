package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;
import java.util.List;

public class FlagDef_NoVehicle extends FlagDefinition {

	@EventHandler
	private void onPlaceVehicle(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		switch (event.getMaterial()) {
			case MINECART:
			case CHEST_MINECART:
			case COMMAND_BLOCK_MINECART:
			case FURNACE_MINECART:
			case HOPPER_MINECART:
			case TNT_MINECART:
			case BIRCH_BOAT:
			case ACACIA_BOAT:
			case DARK_OAK_BOAT:
			case JUNGLE_BOAT:
			case OAK_BOAT:
			case SPRUCE_BOAT:
				Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
				if (flag == null) return;
				event.setCancelled(true);
				GPFlags.sendMessage(player, TextMode.Err, Messages.NoPlaceVehicle);
		}
	}

	public FlagDef_NoVehicle(FlagManager manager, GPFlags plugin) {
		super(manager, plugin);
	}

	@Override
	public String getName() {
		return "NoVehicle";
	}

	@Override
	public MessageSpecifier getSetMessage(String parameters) {
		return new MessageSpecifier(Messages.EnabledNoVehicle);
	}

	@Override
	public MessageSpecifier getUnSetMessage() {
		return new MessageSpecifier(Messages.DisabledNoVehicle);
	}

	@Override
	public List<FlagType> getFlagType() {
		return Collections.singletonList(FlagType.CLAIM);
	}

}
