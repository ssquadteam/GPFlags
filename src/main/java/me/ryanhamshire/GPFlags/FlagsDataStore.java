package me.ryanhamshire.GPFlags;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class for managing GPFlags messages and configuration.
 */
public class FlagsDataStore {

    public static final int PRIOR_CONFIG_VERSION = 0; // Example value
    public static final String dataLayerFolderPath = "plugins" + File.separator + "GPFlags";
    public static final String configFilePath = dataLayerFolderPath + File.separator + "config.yml";
    public static final String flagsFilePath = dataLayerFolderPath + File.separator + "flags.yml";
    public static final String flagsErrorFilePath = dataLayerFolderPath + File.separator + "flagsError.yml";
    private static final String messagesFilePath = dataLayerFolderPath + File.separator + "messages.yml";
    private static final int CONFIG_VERSION = 1;

    // Singleton instance
    private static FlagsDataStore instance;

    // Cached messages
    private final Map<Messages, String> messages = new HashMap<>();

    // Private constructor to enforce singleton pattern
    private FlagsDataStore() {
        loadMessages();
    }

    // Public method to get the singleton instance
    public static FlagsDataStore getInstance() {
        if (instance == null) {
            instance = new FlagsDataStore();
        }
        return instance;
    }

    // Public method to reload messages
    public void reloadMessages() {
        loadMessages(); // Call the private method internally
    }
    
    // Load messages into the cache
    private void loadMessages() {
        File file = new File(messagesFilePath);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Initialize messages with defaults
        for (Messages messageID : Messages.values()) {
            String defaultMessage = getDefaultMessage(messageID);
            String message = config.getString("Messages." + messageID.name() + ".Text", defaultMessage);
            messages.put(messageID, message);

            // Save back to file if default was used
            config.set("Messages." + messageID.name() + ".Text", message);
        }

        // Save any changes back to the file
        try {
            config.set("Version", CONFIG_VERSION);
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get a message with optional replacements
    public String getMessage(Messages messageID, String... args) {
        String message = messages.getOrDefault(messageID, "Message not found: " + messageID.name());

        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", args[i]);
        }
        return message;
    }

    // Get default message (used for initialization)
    private String getDefaultMessage(Messages messageID) {
        // Add default messages here as a fallback
        switch (messageID) {
            case NoEnderPearlInClaim:
                return "{0}, you cannot use enderpearls in {1}'s claim";
            case NoEnderPearlToClaim:
                return "{0}, you cannot use enderpearls to teleport into {1}'s claim";
            case NoEnderPearlInWorld:
                return "{0}, you cannot use enderpearls in this world";
            // Add other defaults as needed
            default:
                return "Default message for: " + messageID.name();
        }
    }
}
