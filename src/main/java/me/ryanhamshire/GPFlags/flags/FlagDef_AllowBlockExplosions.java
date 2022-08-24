package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.ClaimsMode;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        Flag flag = this.getFlagInstanceAtLocation(block.getLocation(), null);
        if (flag == null) return;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getBlock().getLocation(), false, null);
        if (event.getEntityType() == EntityType.WITHER) {
            event.setCancelled(false);
            return;
        }
        // Handle projectiles changing blocks: TNT ignition, tridents knocking down pointed dripstone, etc.
        if (event.getEntity() instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) event.getEntity()).getShooter();

            if (shooter == null) {
                event.setCancelled(false);
                return;
            }

            if (shooter instanceof Player) {
                Supplier<String> denial = claim.checkPermission((Player) shooter, ClaimPermission.Build, event);

                if (denial == null) event.setCancelled(false);
            }
        }
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
