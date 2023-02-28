package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExplodeEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoRespawnAnchorExplosions extends FlagDefinition {

    public FlagDef_NoRespawnAnchorExplosions(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler
    public void onExplosion(BlockExplodeEvent e) {
        Flag flag = this.getFlagInstanceAtLocation(e.getBlock().getLocation(), null);
        if (flag == null) return;
        
        BlockState bs = e.getExplodedBlockState();
        if (bs == null) return;
        if (bs.getType() == Material.RESPAWN_ANCHOR) e.setCancelled(true);
    }

    @Override
    public String getName() {
        return "NoRespawnAnchorExplosions";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledNoRespawnAnchorExplosions);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledNoRespawnAnchorExplosions);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
