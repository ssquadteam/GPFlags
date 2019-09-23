package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.event.EventHandler;

import me.ryanhamshire.GriefPrevention.events.ProtectDeathDropsEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoLootProtection extends FlagDefinition {

    @EventHandler
    public void onPlayerDeath(ProtectDeathDropsEvent event) {
        if (event.getClaim() != null) {
            Flag flag = this.GetFlagInstanceAtLocation(event.getClaim().getLesserBoundaryCorner(), null);
            if (flag == null) return;

            event.setCancelled(true);
        }
    }

    public FlagDef_NoLootProtection(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public String getName() {
        return "NoLootProtection";
    }

    @Override
	public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoLootProtection);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoLootProtection);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
