package me.ryanhamshire.GPFlags;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class FlagDef_NoFallDamage extends FlagDefinition {

    @EventHandler
    public void onFall(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        Flag flag = this.GetFlagInstanceAtLocation(e.getEntity().getLocation(), null);
        if (flag == null) return;

        e.setCancelled(true);
    }

    public FlagDef_NoFallDamage(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    String getName() {
        return "NoFallDamage";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledNoFallDamage);
    }

    @Override
    MessageSpecifier GetUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledNoFallDamage);
    }

}
