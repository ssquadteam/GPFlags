package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ListClaimFlagsCmd extends BaseCmd {

    ListClaimFlagsCmd(GPFlags plugin) {
        super(plugin);
        command = "ListClaimFlags";
        usage = "";
        requirePlayer = true;
    }

    @Override
    boolean execute(CommandSender sender, String[] args) {
        Player player = ((Player) sender);
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);

        Collection<Flag> flags;
        boolean flagsFound = false;
        TextComponent component1 = null;
        TextComponent component2 = null;
        TextComponent component3 = null;
        TextComponent component4 = null;
        TextComponent component5 = null;
        TextColor componentColor;
        if (claim != null) {
            flags = PLUGIN.getFlagManager().getFlags(claim.getID().toString());
            if (flags.size() > 0) component1 = Component.empty();
            for (Flag flag : flags) {
                flagsFound = true;
                if (flag.getSet()) componentColor = TextColor.color(5635925);
                else componentColor = TextColor.color(16733525);
                component1 = component1.append(Component.text(flag.getFlagDefinition().getName())
                        .color(componentColor)
                        .hoverEvent(HoverEvent.showText(Component.text(flag.parameters))))
                                .append(Component.text(" "));
            }

            if (claim.parent != null) {
                flags = PLUGIN.getFlagManager().getFlags(claim.parent.getID().toString());
                if (flags.size() > 0) component2 = Component.empty();
                for (Flag flag : flags) {
                    flagsFound = true;
                    if (flag.getSet()) componentColor = TextColor.color(5635925);
                    else componentColor = TextColor.color(16733525);
                    component2= component2.append(Component.text(flag.getFlagDefinition().getName())
                                    .color(componentColor)
                                    .hoverEvent(HoverEvent.showText(Component.text(flag.parameters))))
                            .append(Component.text(" "));
                }
            }

            flags = PLUGIN.getFlagManager().getFlags(FlagManager.DEFAULT_FLAG_ID);
            if (flags.size() > 0) component3 = Component.empty();
            for (Flag flag2 : flags) {
                flagsFound = true;
                if (flag2.getSet()) componentColor = TextColor.color(5635925);
                else componentColor = TextColor.color(16733525);
                component3 = component3.append(Component.text(flag2.getFlagDefinition().getName())
                                .color(componentColor)
                                .hoverEvent(HoverEvent.showText(Component.text(flag2.parameters))))
                        .append(Component.text(" "));
            }
        }

        flags = PLUGIN.getFlagManager().getFlags(player.getWorld().getName());
        if (flags.size() > 0) component4 = Component.empty();
        for (Flag flag3 : flags) {
            flagsFound = true;
            if (flag3.getSet()) componentColor = TextColor.color(5635925);
            else componentColor = TextColor.color(16733525);
            component4 = component4.append(Component.text(flag3.getFlagDefinition().getName())
                            .color(componentColor)
                            .hoverEvent(HoverEvent.showText(Component.text(flag3.parameters))))
                    .append(Component.text(" "));
        }

        flags = PLUGIN.getFlagManager().getFlags("everywhere");
        if (flags.size() > 0) component5 = Component.empty();
        for (Flag flag4 : flags) {
            flagsFound = true;
            if (flag4.getSet()) componentColor = TextColor.color(5635925);
            else componentColor = TextColor.color(16733525);
            component5 = component5.append(Component.text(flag4.getFlagDefinition().getName())
                            .color(componentColor)
                            .hoverEvent(HoverEvent.showText(Component.text(flag4.parameters))))
                    .append(Component.text(" "));
        }

        if (component1 != null)
            Util.sendComponent(player, Messages.FlagsClaim, component1);
        if (component2 != null)
            Util.sendComponent(player, Messages.FlagsParent, component2);
        if (component3 != null)
            Util.sendComponent(player, Messages.FlagsDefault, component3);
        if (component4 != null)
            Util.sendComponent(player, Messages.FlagsWorld, component4);
        if (component5 != null)
            Util.sendComponent(player, Messages.FlagsServer, component5);

        if (!flagsFound) {
            Util.sendMessage(player, TextMode.Info, Messages.NoFlagsHere);
        }

        return true;
    }

}
