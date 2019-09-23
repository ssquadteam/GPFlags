package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoFluidFlow extends FlagDefinition {

    private Location previousLocation = null;
    private boolean previousWasCancelled = false;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Location location = event.getBlock().getLocation();
        if (this.previousLocation != null && location.equals(this.previousLocation)) {
            if (!this.previousWasCancelled) return;
            event.setCancelled(true);
            return;
        }

        Flag flag = this.GetFlagInstanceAtLocation(location, null);
        boolean cancel = (flag != null);

        this.previousLocation = location;
        this.previousWasCancelled = cancel;
        if (cancel) {
            event.setCancelled(true);
        }
    }

    public FlagDef_NoFluidFlow(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public String getName() {
        return "NoFluidFlow";
    }

    @Override
	public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoFluidFlow);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoFluidFlow);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
