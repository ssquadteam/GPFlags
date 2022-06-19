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

public class CommandSetServerFlag implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission("gpflags.command.setserverflag")) {
            Util.sendMessage(commandSender, TextMode.Err, Messages.NoCommandPermission, command.toString());
            return true;
        }
        if (args.length < 1) return false;

        GPFlags plugin = GPFlags.getInstance();
        String flagName = args[0];
        FlagDefinition def = plugin.getFlagManager().getFlagDefinitionByName(flagName);
        if (def == null) {
            Util.sendMessage(commandSender, TextMode.Err, Util.getFlagDefsMessage(commandSender));
            return true;
        }

        if (!commandSender.hasPermission("gpflags.flag." + def.getName())) {
            Util.sendMessage(commandSender, TextMode.Err, Messages.NoFlagPermission);
            return true;
        }

        if (!def.getFlagType().contains(FlagDefinition.FlagType.SERVER)) {
            Util.sendMessage(commandSender, TextMode.Err, Messages.NoFlagInServer);
            return true;
        }

        String[] params = new String[args.length - 1];
        System.arraycopy(args, 1, params, 0, args.length - 1);

        SetFlagResult result = plugin.getFlagManager().setFlag("everywhere", def, true, params);
        ChatColor color = result.isSuccess() ? TextMode.Success : TextMode.Err;
        if (result.isSuccess()) {
            Util.sendMessage(commandSender, color, Messages.ServerFlagSet);
            plugin.getFlagManager().save();
        } else {
            Util.sendMessage(commandSender, color, result.getMessage().getMessageID(), result.getMessage().getMessageParams());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return Util.flagTab(commandSender, args[0]);
        } else if (args.length == 2) {
            return Util.paramTab(commandSender, args);
        }
        return Collections.emptyList();
    }
}
