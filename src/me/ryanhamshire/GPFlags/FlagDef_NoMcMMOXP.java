package me.ryanhamshire.GPFlags;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class FlagDef_NoMcMMOXP extends FlagDefinition
{
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerGainXP(McMMOPlayerXpGainEvent event) {
        this.handleEvent(event.getPlayer(), event);
    }

    private void handleEvent(Player player, Cancellable event)
    {
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if(flag != null)
        {
            event.setCancelled(true);
        }
    }

    public FlagDef_NoMcMMOXP(FlagManager manager, GPFlags plugin)
    {
        super(manager, plugin);
    }

    @Override
    String getName()
    {
        return "NoMcMMOXPGain";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters)
    {
        return new MessageSpecifier(Messages.EnabledNoMcMMOXP);
    }

    @Override
    MessageSpecifier GetUnSetMessage()
    {
        return new MessageSpecifier(Messages.DisabledNoMcMMOXP);
    }
}
