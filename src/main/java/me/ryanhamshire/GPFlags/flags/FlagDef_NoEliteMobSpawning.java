package me.ryanhamshire.GPFlags.flags;

import com.magmaguy.elitemobs.mobconstructor.EliteEntity;
import me.ryanhamshire.GPFlags.*;
import org.bukkit.event.EventHandler;
import com.magmaguy.elitemobs.api.EliteMobSpawnEvent;


import java.util.Arrays;
import java.util.List;

public class FlagDef_NoEliteMobSpawning extends FlagDefinition {

    public FlagDef_NoEliteMobSpawning(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler
    public void onEntitySpawn(EliteMobSpawnEvent event) {
        EliteEntity ee = event.getEliteMobEntity();
        Flag flag = this.getFlagInstanceAtLocation(ee.getLocation(), null);
        if (flag == null) return;
        event.setCancelled(true);
    }

    @Override
    public String getName() {
        return "NoEliteMobSpawning";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoEliteMobSpawning);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoEliteMobSpawning);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
