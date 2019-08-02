package me.ryanhamshire.GPFlags;

import me.ryanhamshire.GriefPrevention.EntityEventHandler;
import me.ryanhamshire.GriefPrevention.events.PreventPvPEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FlagDef_AllowPvP extends PlayerMovementFlagDefinition {
    private WorldSettingsManager settingsManager;

    @Override
    public boolean allowMovement(Player player, Location lastLocation) {
        if (lastLocation == null) return true;
        Location to = player.getLocation();
        Flag flag = this.GetFlagInstanceAtLocation(to, player);
        WorldSettings settings = this.settingsManager.Get(player.getWorld());
        if (flag == null) {
            if (this.GetFlagInstanceAtLocation(lastLocation, player) != null) {
                if (!settings.pvpRequiresClaimFlag) return true;
                if (!settings.pvpExitClaimMessageEnabled) return true;
                GPFlags.sendMessage(player, TextMode.Success, settings.pvpExitClaimMessage);
            }
            return true;
        }
        if (flag == this.GetFlagInstanceAtLocation(lastLocation, player)) return true;
        if (this.GetFlagInstanceAtLocation(lastLocation, player) != null) return true;

        if (!settings.pvpRequiresClaimFlag) return true;
        if (!settings.pvpEnterClaimMessageEnabled) return true;

        GPFlags.sendMessage(player, TextMode.Warn, settings.pvpEnterClaimMessage);
        return true;
    }

    // bandaid
    private boolean hasJoined;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (hasJoined) {
            hasJoined = false;
            return;
        }
        hasJoined = true;
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), null);
        if (flag == null) return;
        WorldSettings settings = this.settingsManager.Get(player.getWorld());
        if (!settings.pvpRequiresClaimFlag) return;
        if (!settings.pvpEnterClaimMessageEnabled) return;
        GPFlags.sendMessage(player, TextMode.Warn, settings.pvpEnterClaimMessage);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreventPvP(PreventPvPEvent event) {
        Flag flag = this.GetFlagInstanceAtLocation(event.getClaim().getLesserBoundaryCorner(), null);
        if (flag == null) return;
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPotionSplash(PotionSplashEvent event) {
        //ignore potions not thrown by players
        ThrownPotion potion = event.getPotion();
        ProjectileSource projectileSource = potion.getShooter();
        if (!(projectileSource instanceof Player)) return;
        Player thrower = (Player) projectileSource;

        //ignore positive potions
        Collection<PotionEffect> effects = potion.getEffects();
        boolean hasNegativeEffect = false;
        for (PotionEffect effect : effects) {
            if (!EntityEventHandler.positiveEffects.contains(effect.getType())) {
                hasNegativeEffect = true;
                break;
            }
        }

        if (!hasNegativeEffect) return;

        //if not in a no-pvp world, we don't care
        WorldSettings settings = this.settingsManager.Get(potion.getWorld());
        if (!settings.pvpRequiresClaimFlag) return;

        //ignore potions not effecting players or pets
        boolean hasProtectableTarget = false;
        for (LivingEntity effected : event.getAffectedEntities()) {
            if (effected instanceof Player && effected != thrower) {
                hasProtectableTarget = true;
                break;
            } else if (effected instanceof Tameable) {
                Tameable pet = (Tameable) effected;
                if (pet.isTamed() && pet.getOwner() != null) {
                    hasProtectableTarget = true;
                    break;
                }
            }
        }

        if (!hasProtectableTarget) return;

        //if in a flagged-for-pvp area, allow
        Flag flag = this.GetFlagInstanceAtLocation(thrower.getLocation(), thrower);
        if (flag != null) return;

        //otherwise disallow
        event.setCancelled(true);
        GPFlags.sendMessage(thrower, TextMode.Err, settings.pvpDeniedMessage);
    }

    //when an entity is set on fire
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
        //handle it just like we would an entity damge by entity event, except don't send player messages to avoid double messages
        //in cases like attacking with a flame sword or flame arrow, which would ALSO trigger the direct damage event handler
        EntityDamageByEntityEvent eventWrapper = new EntityDamageByEntityEvent(event.getCombuster(), event.getEntity(),
                DamageCause.FIRE_TICK, event.getDuration());
        this.handleEntityDamageEvent(eventWrapper, false);
        event.setCancelled(eventWrapper.isCancelled());
    }

    private void handleEntityDamageEvent(EntityDamageByEntityEvent event, boolean sendErrorMessagesToPlayers) {
        //if the pet is not tamed, we don't care
        if (event.getEntityType() != EntityType.PLAYER) {
            Entity entity = event.getEntity();
            if (entity instanceof Tameable) {
                Tameable pet = (Tameable) entity;
                if (!pet.isTamed() || pet.getOwner() == null) return;
            }
        }

        Entity damager = event.getDamager();

        //if not in a no-pvp world, we don't care
        WorldSettings settings = this.settingsManager.Get(damager.getWorld());
        if (!settings.pvpRequiresClaimFlag) return;

        Projectile projectile = null;
        if (damager instanceof Projectile) {
            projectile = (Projectile) damager;
            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (damager.getType() != EntityType.PLAYER && damager.getType() != EntityType.AREA_EFFECT_CLOUD) return;

        //if in a flagged-for-pvp area, allow
        Flag flag = this.GetFlagInstanceAtLocation(damager.getLocation(), null);
        Flag flag2 = this.GetFlagInstanceAtLocation(event.getEntity().getLocation(), null);
        if (flag != null && flag2 != null) return;

        //if damaged entity is not a player, ignore, this is a PVP flag
        if (event.getEntityType() != EntityType.PLAYER) return;
        // Enderpearl are considered as FALL with event.getEntityType() = player...
        if (event.getCause() == DamageCause.FALL) return;

        //otherwise disallow
        event.setCancelled(true);

        // give the shooter back their projectile
        if (projectile != null) {
            if (projectile.hasMetadata("item-stack")) {
                MetadataValue meta = projectile.getMetadata("item-stack").get(0);
                if (meta != null) {
                    ItemStack item = ((ItemStack) meta.value());
                    assert item != null;
                    if (item.getType() != Material.AIR) {
                        item.setAmount(1);
                        ((Player) damager).getInventory().addItem(item);
                    }
                }
            }
            projectile.remove();
        }
        if (sendErrorMessagesToPlayers && damager instanceof Player)
            GPFlags.sendMessage(damager, TextMode.Err, settings.pvpDeniedMessage);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        this.handleEntityDamageEvent(event, true);
    }

    @EventHandler
    private void onShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity());
            ItemStack projectile;
            ItemStack bow = event.getBow();
            assert bow != null;
            if (Util.isRunningMinecraft(1, 14) && bow.getType() == Material.CROSSBOW) {
                if (bow.getItemMeta() != null) {
                    projectile = ((CrossbowMeta) bow.getItemMeta()).getChargedProjectiles().get(0);
                    event.getProjectile().setMetadata("item-stack", new FixedMetadataValue(GPFlags.instance, projectile.clone()));
                    return;
                }
            }
            if (isProjectile(player.getInventory().getItemInOffHand())) {
                projectile = player.getInventory().getItemInOffHand();
                event.getProjectile().setMetadata("item-stack", new FixedMetadataValue(GPFlags.instance, projectile.clone()));
                return;
            }
            for (ItemStack item : player.getInventory()) {
                if (item != null && isProjectile(item)) {
                    event.getProjectile().setMetadata("item-stack", new FixedMetadataValue(GPFlags.instance, item.clone()));
                    return;
                }
            }
        }
    }

    private boolean isProjectile(ItemStack item) {
        switch (item.getType()) {
            case ARROW:
            case SPECTRAL_ARROW:
            case TIPPED_ARROW:
            case FIREWORK_ROCKET:
                return true;
        }
        return false;
    }

    FlagDef_AllowPvP(FlagManager manager, GPFlags plugin, WorldSettingsManager settingsManager) {
        super(manager, plugin);
        this.settingsManager = settingsManager;
    }

    void updateSettings(WorldSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    String getName() {
        return "AllowPvP";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters) {
        return new MessageSpecifier(Messages.AddEnablePvP);
    }

    @Override
    MessageSpecifier GetUnSetMessage() {
        return new MessageSpecifier(Messages.RemoveEnabledPvP);
    }

    @Override
    List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }
}
