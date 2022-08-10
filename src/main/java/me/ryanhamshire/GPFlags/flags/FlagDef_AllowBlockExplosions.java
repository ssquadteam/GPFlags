package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExplodeEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_AllowBlockExplosions extends FlagDefinition {

    public FlagDef_AllowBlockExplosions(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onBlockExplode(BlockExplodeEvent e) {
        Block block = e.getBlock();
        Flag flag = this.getFlagInstanceAtLocation(block.getLocation(), null);
        if (flag == null) return;
        e.setCancelled(false);
    }

    @Override
    public String getName() {
        return "AllowBlockExplosions";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledAllowBlockExplosions);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledAllowBlockExplosions);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
