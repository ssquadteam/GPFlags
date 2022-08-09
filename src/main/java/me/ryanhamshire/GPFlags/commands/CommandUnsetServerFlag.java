package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GPFlags.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CommandUnsetServerFlag implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!sender.hasPermission("gpflags.command.unsetserverflag")) {
            Util.sendMessage(sender, TextMode.Err, Messages.NoCommandPermission, command.toString());
            return true;
        }
        if (args.length < 1) return false;

        String flagName = args[0];
        GPFlags plugin = GPFlags.getInstance();
        FlagDefinition def = plugin.getFlagManager().getFlagDefinitionByName(flagName);
        if (def == null) {
            Util.sendMessage(sender, TextMode.Err, Util.getFlagDefsMessage(sender));
            return true;
        }

        if (!sender.hasPermission("gpflags.flag." + def.getName())) {
            Util.sendMessage(sender, TextMode.Err, Messages.NoFlagPermission, def.getName());
            return true;
        }

        SetFlagResult result = plugin.getFlagManager().unSetFlag("everywhere", def, true);
        ChatColor color = result.isSuccess() ? TextMode.Success : TextMode.Err;
        if (result.isSuccess()) {
            Util.sendMessage(sender, color, Messages.ServerFlagUnSet);
            plugin.getFlagManager().save();
        } else {
            Util.sendMessage(sender, color, result.getMessage().getMessageID(), result.getMessage().getMessageParams());
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return Util.flagTab(commandSender, args[0]);
        }
        return Collections.emptyList();
    }
}
