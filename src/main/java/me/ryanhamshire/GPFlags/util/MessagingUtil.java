package me.ryanhamshire.GPFlags.util;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.GPFlagsConfig;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.hooks.PlaceholderApiHook;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagingUtil {

    /**
     * Get the prefix stored in messages.yml
     *
     * @return prefix stored in messages.yml
     */
    private static String getPrefix() {
        return GPFlags.getInstance().getFlagsDataStore().getMessage(Messages.Prefix);
    }

    /**
     * Shortcut for adding color to a string
     *
     * @param string String including color codes
     * @return Formatted string
     */
    private static String getColString(String string) {
        // If you don't have hex colors, you get the basics
        if (!Util.isRunningMinecraft(1, 16)) {
            return ChatColor.translateAlternateColorCodes('&', string);
        }

        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(string);
        while (matcher.find()) {
            final String before = string.substring(0, matcher.start());
            final String after = string.substring(matcher.end());
            ChatColor hexColor = ChatColor.of(matcher.group().substring(1));
            string = before + hexColor + after;
            matcher = hexPattern.matcher(string);
        }
        string = ChatColor.translateAlternateColorCodes('&', string);
        return string;
    }

    /**
     * Fills in message params, adds formatting, and sends it to the receiver.
     */
    public static void sendMessage(@Nullable CommandSender receiver, Messages messageID, String... args) {
        String message = GPFlags.getInstance().getFlagsDataStore().getMessage(messageID, args);
        sendMessage(receiver, message);
    }

    /**
     * Send a {@link Messages Message} to a player, or console if player is null
     *
     * @param receiver  Player to send message to, or null if to console
     * @param color     Color of message
     * @param messageID Message to send
     * @param args      Message parameters
     */
    public static void sendMessage(@Nullable CommandSender receiver, String color, Messages messageID, String... args) {
        String message = GPFlags.getInstance().getFlagsDataStore().getMessage(messageID, args);
        sendMessage(receiver, color + message);
    }

    public static void sendMessage(@Nullable CommandSender receiver, String message) {
        if (!(receiver instanceof OfflinePlayer)) {
            message = PlaceholderApiHook.addPlaceholders(null, getPrefix() + message);
            message = getColString(message);
            logToConsole(message);
            return;
        }
        Player player = (Player) receiver;
        message = PlaceholderApiHook.addPlaceholders(player, message);
        message = getColString(message);
        try {
            MiniMessage mm = MiniMessage.miniMessage();
            Component component = mm.deserialize(message);
            player.sendMessage(component);
        } catch (Exception exception) {
            player.sendMessage(message);
        }
    }

    private static void logToConsole(String message) {
        try {
            MiniMessage mm = MiniMessage.miniMessage();
            Component component = mm.deserialize(message);
            Bukkit.getConsoleSender().sendMessage(component);
        } catch (Exception exception) {
            Bukkit.getLogger().info(message);
        }
    }

    public static void sendActionbar(Player player, String message) {
        try {
            MiniMessage mm = MiniMessage.miniMessage();
            Component component = mm.deserialize(message);
            player.sendActionBar(component);
        } catch (Exception exception) {
            player.sendActionBar(message);
        }
    }

    public static void logFlagCommands(String log) {
        if (GPFlagsConfig.LOG_ENTER_EXIT_COMMANDS) {
            logToConsole(getPrefix() + log);
        }
    }
}
