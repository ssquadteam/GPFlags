package me.ryanhamshire.GPFlags.commands;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.flags.FlagDef_ChangeBiome;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CommandSetClaimFlagPlayer implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission("gpflags.command.setclaimflagplayer")) {
            Util.sendMessage(commandSender, TextMode.Err, Messages.NoCommandPermission, command.toString());
            return true;
        }
        if (args.length < 2) return false;
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            Util.sendMessage(commandSender, "&c%s &7is not online", args[0]);
            return false;
        }
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);
        if (claim == null || !Util.canBuild(claim, player)) {
            Util.sendMessage(commandSender, "&cThis player is not standing in a claim they own");
            return false;
        }

        String flagName = args[1];
        GPFlags gpflags = GPFlags.getInstance();
        FlagDefinition def = gpflags.getFlagManager().getFlagDefinitionByName(flagName);
        if (def == null) {
            Util.sendMessage(commandSender, "&c%s&7 is not a valid flag", flagName);
            return false;
        }
        if (!def.getFlagType().contains(FlagDefinition.FlagType.CLAIM)) {
            Util.sendMessage(player, TextMode.Err, Messages.NoFlagInClaim);
            return true;
        }

        String[] params = new String[args.length - 2];
        System.arraycopy(args, 2, params, 0, args.length - 2);

        // SET BIOME
        if (flagName.equalsIgnoreCase("ChangeBiome")) {
            if (args.length < 3) return false;
            FlagDef_ChangeBiome flagD = ((FlagDef_ChangeBiome) gpflags.getFlagManager().getFlagDefinitionByName("changebiome"));
            String biome = params[0].toUpperCase().replace(" ", "_");
            if (!flagD.changeBiome(commandSender, claim, biome)) return true;
        }

        SetFlagResult result = gpflags.getFlagManager().setFlag(claim.getID().toString(), def, true, params);
        ChatColor color = result.isSuccess() ? TextMode.Success : TextMode.Err;
        Util.sendMessage(commandSender, color, result.getMessage().getMessageID(), result.getMessage().getMessageParams());
        if (result.isSuccess()) {
            gpflags.getFlagManager().save();
            Util.sendMessage(commandSender, "&7Flag &b%s &7successfully set in &b%s&7's claim.", def.getName(), player.getName() );
            return true;
        }

        // Kick people out if it was a NoEnter flag
        if (flagName.equalsIgnoreCase("NoEnter") && args.length >= 2) {
            World world = player.getWorld();
            for (Player p : world.getPlayers()) {
                if (claim.contains(p.getLocation(), true, false)) {
                    if (claim.getPermission(p.getName()) == null && !claim.getOwnerName().equals(p.getName())) {
                        GriefPrevention.instance.ejectPlayer(p);
                    }
                }
            }
        }
        if (flagName.equalsIgnoreCase("NoEnterPlayer") && args.length >= 2) {
            for (int i = 1; i < args.length; i++) {
                Player target = Bukkit.getPlayer(args[i]);
                if (target != null && target.getName().equalsIgnoreCase(args[i])) {
                    if (claim.contains(target.getLocation(), true, false)) {
                        if (claim.getPermission(args[i]) == null) {
                            GriefPrevention.instance.ejectPlayer(target);
                        }
                    }
                }
            }
        }

        // Turn on fly if it was a Fly flag
        if (flagName.equalsIgnoreCase("OwnerFly")) {
            player.setAllowFlight(true);
        }
        if (flagName.equalsIgnoreCase("OwnerMemberFly")) {
            player.setAllowFlight(true);
            World world = player.getWorld();
            for (Player p : world.getPlayers()) {
                if (claim.contains(p.getLocation(), true, false)) {
                    if (claim.getPermission(p.getUniqueId().toString()) == ClaimPermission.Access) {
                        p.setAllowFlight(true);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return null; // allows for tabbing players
        } else if (args.length == 2) {
            return Util.flagTab(commandSender, args[1]);
        } else if (args.length > 2) {
            String[] params = new String[args.length - 1];
            System.arraycopy(args, 1, params, 0, args.length - 1);
            return Util.paramTab(commandSender, params);
        }
        return Collections.emptyList();
    }
}
