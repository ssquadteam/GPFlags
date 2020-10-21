package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
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

    private final VersionControl vc;

    public FlagDef_NoOpenDoors(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
        vc = plugin.getVersionControl();
    }

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

                String[] params = null;
                if (!flag.parameters.isEmpty()) {
                    params = flag.parameters.split(",");
                }

                if (claim.allowAccess(player) != null) {
                    if (params != null) {
                        for (String param : params) {
                            if (param.equalsIgnoreCase("doors") && vc.isDoor(block)) {
                                e.setCancelled(true);
                                GPFlags.sendMessage(player, TextMode.Err, Messages.NoOpenDoorMessage, param);
                            }
                            if (param.equalsIgnoreCase("trapdoors") && vc.isTrapDoor(block)) {
                                e.setCancelled(true);
                                GPFlags.sendMessage(player, TextMode.Err, Messages.NoOpenDoorMessage, param);
                            }
                            if (param.equalsIgnoreCase("gates") && vc.isGate(block)) {
                                e.setCancelled(true);
                                GPFlags.sendMessage(player, TextMode.Err, Messages.NoOpenDoorMessage, param);
                            }
                        }
                    } else {
                        e.setCancelled(true);
                        GPFlags.sendMessage(player, TextMode.Err, Messages.NoOpenDoorMessage, "doors");
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return "NoOpenDoors";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        if (parameters.isEmpty()) {
            return new MessageSpecifier(Messages.EnableNoOpenDoor);
        } else {
            return new MessageSpecifier(Messages.EnableNoOpenDoor, parameters);
        }
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoOpenDoor);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }

}
