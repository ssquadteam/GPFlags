package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoMobSpawns extends FlagDefinition {

    public FlagDef_NoMobSpawns(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        EntityType type = event.getEntityType();
        if (type == EntityType.PLAYER || type == EntityType.ARMOR_STAND) return;

        SpawnReason reason = event.getSpawnReason();
        if (reason == SpawnReason.SPAWNER || reason == SpawnReason.SPAWNER_EGG) return;


        Flag flag = this.GetFlagInstanceAtLocation(event.getLocation(), null);
        if (flag == null) return;

        event.setCancelled(true);
    }

    @Override
    public String getName() {
        return "NoMobSpawns";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.DisableMobSpawns);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.EnableMobSpawns);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.WORLD, FlagType.CLAIM, FlagType.SERVER);
    }
}
