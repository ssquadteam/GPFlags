package me.ryanhamshire.GPFlags;

import me.ryanhamshire.GPFlags.util.VersionControl;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoMonsterSpawns extends FlagDefinition {

    private final String ALLOW_TARGET_TAG = "GPF_AllowTarget";
    private VersionControl vc = GPFlags.getVersionControl();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (!this.isMonster(entity)) return;

        SpawnReason reason = event.getSpawnReason();
        if (reason == SpawnReason.SPAWNER || reason == SpawnReason.SPAWNER_EGG) {
            entity.setMetadata(this.ALLOW_TARGET_TAG, new FixedMetadataValue(GPFlags.instance, new Boolean(true)));
            return;
        }

        Flag flag = this.GetFlagInstanceAtLocation(event.getLocation(), null);
        if (flag == null) return;

        event.setCancelled(true);
    }

    private boolean isMonster(Entity entity) {
        if (entity instanceof Monster) return true;

        if (vc.isMonster(entity)) return true;

        if (entity.getType() == EntityType.RABBIT) {
            Rabbit rabbit = (Rabbit) entity;
            if (rabbit.getRabbitType() == Rabbit.Type.THE_KILLER_BUNNY) return true;
        }

        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        Entity target = event.getTarget();
        if (target == null) return;

        Entity entity = event.getEntity();
        if (!this.isMonster(entity)) return;
        if (entity.hasMetadata(this.ALLOW_TARGET_TAG)) return;

        Flag flag = this.GetFlagInstanceAtLocation(target.getLocation(), null);
        if (flag == null) return;

        event.setCancelled(true);
        entity.remove();
    }

    @EventHandler
    private void onMobDamage(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();
        Entity damager = event.getDamager();
        if (damager instanceof Player) return;
        if (damager.hasMetadata(this.ALLOW_TARGET_TAG)) return;

        Flag flag = this.GetFlagInstanceAtLocation(target.getLocation(), null);
        if (flag == null) return;

        event.setCancelled(true);
        damager.remove();
    }

    public FlagDef_NoMonsterSpawns(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    String getName() {
        return "NoMonsterSpawns";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters) {
        return new MessageSpecifier(Messages.DisableMonsterSpawns);
    }

    @Override
    MessageSpecifier GetUnSetMessage() {
        return new MessageSpecifier(Messages.EnableMonsterSpawns);
    }

    @Override
    List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
