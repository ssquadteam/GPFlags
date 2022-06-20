package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandListClaimFlags implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission("gpflags.command.listclaimflags")) {
            Util.sendMessage(commandSender, TextMode.Err, Messages.NoCommandPermission, command.toString());
            return true;
        }
        if (!(commandSender instanceof Player)) {
            Util.sendMessage(commandSender, TextMode.Warn, Messages.PlayerOnlyCommand, command.toString());
            return true;
        }

        GPFlags gpflags = GPFlags.getInstance();
        Player player = ((Player) commandSender);
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());

        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);

        Collection<Flag> flags;
        boolean flagsFound = false;
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        StringBuilder builder3 = new StringBuilder();
        if (claim != null) {
            flags = gpflags.getFlagManager().getFlags(claim.getID().toString());
            for (Flag flag : flags) {
                flagsFound = true;
                builder1.append(flag.getSet() ? "&a" : "&c").append(flag.getFlagDefinition().getName()).append(flag.parameters.length() > 0 ? "&7(" + flag.parameters + "&7)" : "").append(" ");
            }

            if (claim.parent != null) {
                flags = gpflags.getFlagManager().getFlags(claim.parent.getID().toString());
                for (Flag flag : flags) {
                    flagsFound = true;
                    builder2.append(flag.getSet() ? "&a" : "&c").append(flag.getFlagDefinition().getName()).append(flag.parameters.length() > 0 ? "&7(" + flag.parameters + "&7)" : "").append(" ");
                }
            }

            flags = gpflags.getFlagManager().getFlags(FlagManager.DEFAULT_FLAG_ID);
            for (Flag flag2 : flags) {
                flagsFound = true;
                builder3.append(flag2.getSet() ? "&a" : "&c").append(flag2.getFlagDefinition().getName()).append(flag2.parameters.length() > 0 ? "&7(" + flag2.parameters + "&7)" : "").append(" ");
            }
        }

        StringBuilder builder4 = new StringBuilder();
        flags = gpflags.getFlagManager().getFlags(player.getWorld().getName());
        for (Flag flag3 : flags) {
            flagsFound = true;
            builder4.append(flag3.getSet() ? "&a" : "&c").append(flag3.getFlagDefinition().getName()).append(flag3.parameters.length() > 0 ? "&7(" + flag3.parameters + "&7)" : "").append(" ");
        }

        StringBuilder builder5 = new StringBuilder();
        flags = gpflags.getFlagManager().getFlags("everywhere");
        for (Flag flag4 : flags) {
            flagsFound = true;
            builder5.append(flag4.getSet() ? "&a" : "&c").append(flag4.getFlagDefinition().getName()).append(flag4.parameters.length() > 0 ? "&7(" + flag4.parameters + "&7)" : "").append(" ");
        }

        if (builder1.length() > 0)
            Util.sendMessage(player, TextMode.Info, Messages.FlagsClaim, builder1.toString());
        if (builder2.length() > 0)
            Util.sendMessage(player, TextMode.Info, Messages.FlagsParent, builder2.toString());
        if (builder3.length() > 0)
            Util.sendMessage(player, TextMode.Info, Messages.FlagsDefault, builder3.toString());
        if (builder4.length() > 0)
            Util.sendMessage(player, TextMode.Info, Messages.FlagsWorld, builder4.toString());
        if (builder5.length() > 0)
            Util.sendMessage(player, TextMode.Info, Messages.FlagsServer, builder5.toString());

        if (!flagsFound) {
            Util.sendMessage(player, TextMode.Info, Messages.NoFlagsHere);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("verbose");
        return StringUtil.copyPartialMatches(args[0], list, new ArrayList<>());
    }
}
