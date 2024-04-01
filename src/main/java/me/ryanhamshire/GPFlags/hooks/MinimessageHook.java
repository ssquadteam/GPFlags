package me.ryanhamshire.GPFlags.hooks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MinimessageHook {

    public static void sendPlayerMessage(@NotNull Player player, String message) {
        Component component = MiniMessage.miniMessage().deserialize(message);
        player.sendMessage(component);
    }

    public static void sendConsoleMessage(String message) {
        Component component = MiniMessage.miniMessage().deserialize(message);
        Bukkit.getConsoleSender().sendMessage(component);
    }

    public static void sendActionbar(Player player, String message) {
        Component component = MiniMessage.miniMessage().deserialize(message);
        player.sendActionBar(component);
    }

    public static String reserialize(String ampersandMessage) {
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(ampersandMessage);
        return MiniMessage.miniMessage().serialize(component);
    }
}
