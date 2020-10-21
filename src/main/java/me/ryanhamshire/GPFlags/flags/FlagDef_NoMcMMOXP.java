package me.ryanhamshire.GPFlags.flags;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoMcMMOXP extends FlagDefinition {

    public FlagDef_NoMcMMOXP(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerGainXP(McMMOPlayerXpGainEvent event) {
        this.handleEvent(event.getPlayer(), event);
    }

    private void handleEvent(Player player, Cancellable event) {
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if (flag != null) {
            event.setCancelled(true);
        }
    }

    @Override
    public String getName() {
        return "NoMcMMOXPGain";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledNoMcMMOXP);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledNoMcMMOXP);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
