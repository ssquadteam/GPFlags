package me.ryanhamshire.GPFlags.util;

import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.GPFlagsConfig;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.hooks.MinimessageHook;
import me.ryanhamshire.GPFlags.hooks.PlaceholderApiHook;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
        if (!(receiver instanceof Player)) {
            logToConsole(getPrefix() + message);
            return;
        }
        Player player = (Player) receiver;
        try {
            message = PlaceholderApiHook.addPlaceholders(player, message);
        } catch (Throwable ignored) {}
        try {
            MinimessageHook.sendPlayerMessage(player, message);
        } catch (Throwable e) {
            player.sendMessage(message);
        }
    }

    private static void logToConsole(String message) {
        try {
            MinimessageHook.sendConsoleMessage(message);
        } catch (Throwable e) {
            Bukkit.getLogger().info(message);
        }
    }

    public static void sendActionbar(Player player, String message) {
        try {
            MinimessageHook.sendActionbar(player, message);
        } catch (Throwable e) {
            player.sendActionBar(message);
        }
    }

    public static void logFlagCommands(String log) {
        if (GPFlagsConfig.LOG_ENTER_EXIT_COMMANDS) {
            logToConsole(getPrefix() + log);
        }
    }
}
