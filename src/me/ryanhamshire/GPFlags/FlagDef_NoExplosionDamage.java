package me.ryanhamshire.GPFlags;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class FlagDef_NoExplosionDamage extends FlagDefinition {

    @EventHandler
    public void onFall(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
        e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {

            Flag flag = this.GetFlagInstanceAtLocation(e.getEntity().getLocation(), null);
            if (flag == null) return;

            e.setCancelled(true);
        }
    }

    public FlagDef_NoExplosionDamage(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    String getName() {
        return "NoExplosionDamage";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledNoExplosionDamage);
    }

    @Override
    MessageSpecifier GetUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledNoExplosionDamage);
    }

}
