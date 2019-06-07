package me.ryanhamshire.GPFlags;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFormEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoSnowForm extends FlagDefinition {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGrowth(BlockFormEvent event)
    {
        Block block = event.getBlock();

        Flag flag = this.GetFlagInstanceAtLocation(block.getLocation(), null);
        if(flag == null) return;

        if(event.getNewState().getType() != Material.SNOW) return;
        event.setCancelled(true);
    }

    FlagDef_NoSnowForm(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }

    @Override
    String getName()
    {
        return "NoSnowForm";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnableNoSnowForm);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisableNoSnowForm);
    }

    @Override
    List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}