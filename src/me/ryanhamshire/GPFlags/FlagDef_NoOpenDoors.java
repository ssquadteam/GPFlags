package me.ryanhamshire.GPFlags;

import me.ryanhamshire.GPFlags.util.VersionControl;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;
import java.util.List;

public class FlagDef_NoOpenDoors extends FlagDefinition {

	private VersionControl vc = GPFlags.getVersionControl();

	@EventHandler
	public void onDoorOpen(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();
			assert block != null;
			Flag flag = this.GetFlagInstanceAtLocation(block.getLocation(), player);
			if (flag == null) return;

			if (vc.isOpenable(block)) {

				PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
				Claim claim = GriefPrevention.instance.dataStore.getClaimAt(block.getLocation(), true, playerData.lastClaim);

				if (claim.allowAccess(player) != null) {
					e.setCancelled(true);
					GPFlags.sendMessage(player, TextMode.Err, Messages.NoOpenDoorMessage);
				}
			}
		}
	}

	FlagDef_NoOpenDoors(FlagManager manager, GPFlags plugin) {
		super(manager, plugin);
	}

	@Override
	String getName() {
		return "NoOpenDoors";
	}

	@Override
	MessageSpecifier GetSetMessage(String parameters) {
		return new MessageSpecifier(Messages.EnableNoOpenDoor);
	}

	@Override
	MessageSpecifier GetUnSetMessage() {
		return new MessageSpecifier(Messages.DisableNoOpenDoor);
	}

	@Override
	List<FlagType> getFlagType() {
		return Collections.singletonList(FlagType.CLAIM);
	}

}
