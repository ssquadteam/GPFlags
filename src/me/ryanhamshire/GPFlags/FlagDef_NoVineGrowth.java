package me.ryanhamshire.GPFlags;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockSpreadEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoVineGrowth extends FlagDefinition {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGrowth(BlockSpreadEvent event) {
        Block block = event.getBlock();

        Flag flag = this.GetFlagInstanceAtLocation(block.getLocation(), null);
        if (flag == null) return;

        if (event.getSource().getType() != Material.VINE) return;
        event.setCancelled(true);
    }

    FlagDef_NoVineGrowth(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    String getName() {
        return "NoVineGrowth";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoVineGrowth);
    }

    @Override
    MessageSpecifier GetUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoVineGrowth);
    }

    @Override
    List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
