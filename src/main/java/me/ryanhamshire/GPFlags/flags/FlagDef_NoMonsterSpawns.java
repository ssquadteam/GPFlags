package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.VersionControl;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoMonsterSpawns extends FlagDefinition {

    private WorldSettingsManager settingsManager;

    private final String ALLOW_TARGET_TAG = "GPF_AllowTarget";
    private final VersionControl vc = GPFlags.getInstance().getVersionControl();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (!vc.isMonster(entity)) return;

        SpawnReason reason = event.getSpawnReason();

        WorldSettings settings = this.settingsManager.get(event.getEntity().getWorld());
        if (settings.noMonsterSpawnIgnoreSpawners && (reason == SpawnReason.SPAWNER || reason == SpawnReason.SPAWNER_EGG)) {
            entity.setMetadata(this.ALLOW_TARGET_TAG, new FixedMetadataValue(GPFlags.getInstance(), Boolean.TRUE));
            return;
        }

        Flag flag = this.GetFlagInstanceAtLocation(event.getLocation(), null);
        if (flag == null) return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        Entity target = event.getTarget();
        if (target == null) return;

        Entity entity = event.getEntity();
        if (!vc.isMonster(entity)) return;
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
        if (!vc.isMonster(damager)) return;
        if (damager instanceof Player) return;
        if (!(damager instanceof LivingEntity)) return;
        if (!(target instanceof Player)) return;
        if (damager.hasMetadata(this.ALLOW_TARGET_TAG)) return;

        Flag flag = this.GetFlagInstanceAtLocation(target.getLocation(), null);
        if (flag == null) return;

        event.setCancelled(true);
        damager.remove();
    }

    @EventHandler
    private void onPoison(EntityPotionEffectEvent event) {
        if (event.getCause() != Cause.ATTACK) return;
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Flag flag = this.GetFlagInstanceAtLocation(entity.getLocation(), null);
            if (flag == null) return;
            event.setCancelled(true);
        }
    }

    public FlagDef_NoMonsterSpawns(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
        this.settingsManager = plugin.getWorldSettingsManager();
    }

    public void updateSettings(WorldSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public String getName() {
        return "NoMonsterSpawns";
    }

    @Override
	public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.DisableMonsterSpawns);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.EnableMonsterSpawns);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
