package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoFireDamage extends FlagDefinition {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFireSpread(BlockBurnEvent e) {
        Block fire = e.getBlock();
        Flag flag = this.GetFlagInstanceAtLocation(fire.getLocation(), null);
        if (flag == null) return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockIgnite(BlockIgniteEvent e) {
        Block fire = e.getBlock();
        Flag flag = this.GetFlagInstanceAtLocation(fire.getLocation(), null);
        if (flag == null) return;
        if (e.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) return;

        e.setCancelled(true);
    }

    public FlagDef_NoFireDamage(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }

    @Override
    public String getName()
    {
        return "NoFireDamage";
    }

    @Override
	public MessageSpecifier getSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnableNoFireDamage);
    }

    @Override
    public MessageSpecifier getUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisableNoFireDamage);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
