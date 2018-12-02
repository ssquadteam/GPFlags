package me.ryanhamshire.GPFlags;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockSpreadEvent;

public class FlagDef_NoFireSpread extends FlagDefinition {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFireSpread(BlockSpreadEvent e)
    {
        Block fire = e.getSource();
        if(fire.getType() != Material.FIRE) return;

        Flag flag = this.GetFlagInstanceAtLocation(fire.getLocation(), null);
        if(flag == null) return;

        e.setCancelled(true);
    }

    public FlagDef_NoFireSpread(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }

    @Override
    String getName()
    {
        return "NoFireSpread";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnabledNoFireSpread);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisabledNoFireSpread);
    }
}
