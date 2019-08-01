package me.ryanhamshire.GPFlags;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoMobSpawnsType extends FlagDefinition {

	private final String ALLOW_TARGET_TAG = "GPF_AllowTarget";

	@EventHandler
	private void onMobSpawn(CreatureSpawnEvent event) {
		Flag flag = this.GetFlagInstanceAtLocation(event.getLocation(), null);
		if (flag == null) return;

		String[] entityTypes = flag.parameters.split(";");

		EntityType type = event.getEntityType();
		if (type == EntityType.PLAYER) return;
		if (type == EntityType.ARMOR_STAND) return;

		CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
		if (reason == CreatureSpawnEvent.SpawnReason.SPAWNER || reason == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
			event.getEntity().setMetadata(this.ALLOW_TARGET_TAG, new FixedMetadataValue(GPFlags.instance, Boolean.TRUE));
			return;
		}

		for (String entityType : entityTypes) {
			if (event.getEntityType().toString().equalsIgnoreCase(entityType)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	private void onMobTarget(EntityTargetEvent event) {
		Entity target = event.getTarget();
		if (target == null) return;

		Entity entity = event.getEntity();
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

	FlagDef_NoMobSpawnsType(FlagManager manager, GPFlags plugin) {
		super(manager, plugin);
	}

	@Override
	SetFlagResult ValidateParameters(String parameters) {
		if (parameters.isEmpty()) {
			return new SetFlagResult(false, new MessageSpecifier(Messages.MobTypeRequired));
		}
		return new SetFlagResult(true, this.GetSetMessage(parameters));
	}

	@Override
	String getName() {
		return "NoMobSpawnsType";
	}

	@Override
	MessageSpecifier GetSetMessage(String parameters) {
		return new MessageSpecifier(Messages.EnabledNoMobSpawnsType, parameters);
	}

	@Override
	MessageSpecifier GetUnSetMessage() {
		return new MessageSpecifier(Messages.DisabledNoMobSpawnsType);
	}

	@Override
	List<FlagType> getFlagType() {
		return Arrays.asList(FlagType.WORLD, FlagType.CLAIM, FlagType.SERVER);
	}

}
