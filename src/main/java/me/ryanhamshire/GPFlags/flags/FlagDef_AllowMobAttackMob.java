package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;
import java.util.List;

public class FlagDef_AllowMobAttackMob extends FlagDefinition {

    public FlagDef_AllowMobAttackMob(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity attacked = event.getEntity();
        if (attacked.getType() == EntityType.PLAYER) return;

        Flag flag = this.getFlagInstanceAtLocation(attacked.getLocation(), null);
        if (flag == null) return;

        Entity damager = event.getDamager();
        if (damager.getType() == EntityType.PLAYER) return;

        event.setCancelled(false);

    }

    @Override
    public String getName() {
        return "AllowMobAttackMob";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledAllowMobAttackMob);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledAllowMobAttackMob);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.SERVER, FlagType.WORLD);
    }

}
