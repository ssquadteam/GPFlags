package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockSpreadEvent;

import java.util.Arrays;
import java.util.List;

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
    public String getName()
    {
        return "NoFireSpread";
    }

    @Override
	public MessageSpecifier getSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnabledNoFireSpread);
    }

    @Override
    public MessageSpecifier getUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisabledNoFireSpread);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
