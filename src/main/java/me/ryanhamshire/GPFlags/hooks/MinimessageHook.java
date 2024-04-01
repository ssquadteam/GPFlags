package me.ryanhamshire.GPFlags.hooks;

import me.ryanhamshire.GPFlags.GPFlags;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MinimessageHook {

    public static void sendPlayerMessage(@NotNull Player player, String message) {
        Component component = MiniMessage.miniMessage().deserialize(message);
        Audience.audience(player).sendMessage(component);
    }

    public static void sendConsoleMessage(String message) {
        String stripped = MiniMessage.miniMessage().stripTags(message);
        GPFlags.getInstance().getLogger().info(stripped);
    }

    public static void sendActionbar(Player player, String message) {
        Component component = MiniMessage.miniMessage().deserialize(message);
        Audience.audience(player).sendMessage(component);
    }

    public static String reserialize(String ampersandMessage) {
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(ampersandMessage);
        return MiniMessage.miniMessage().serialize(component);
    }
}
