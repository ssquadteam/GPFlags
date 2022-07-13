package me.ryanhamshire.GPFlags;

import java.util.ArrayList;

import me.ryanhamshire.GPFlags.commands.*;
import me.ryanhamshire.GPFlags.listener.RidableMoveListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.ryanhamshire.GPFlags.flags.FlagDef_ViewContainers;
import me.ryanhamshire.GPFlags.listener.PlayerListener;
import me.ryanhamshire.GPFlags.metrics.Metrics;
import me.ryanhamshire.GPFlags.util.Util;

/**
 * <b>Main GriefPrevention Flags class</b>
 */
public class GPFlags extends JavaPlugin {

    private static GPFlags instance;
    private FlagsDataStore flagsDataStore;
    private final FlagManager flagManager = new FlagManager();
    private WorldSettingsManager worldSettingsManager;

    boolean registeredFlagDefinitions = false;
    private PlayerListener playerListener;


    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;

        this.playerListener = new PlayerListener();
        Bukkit.getPluginManager().registerEvents(playerListener, this);
        try {
            Class.forName("org.purpurmc.purpur.event.entity.RidableMoveEvent");
            Bukkit.getPluginManager().registerEvents(new RidableMoveListener(), this);
        } catch (ClassNotFoundException ignored) {}
        this.flagsDataStore = new FlagsDataStore();
        reloadConfig();

        // Register Commands
        getCommand("allflags").setExecutor(new CommandAllFlags());
        getCommand("gpflags").setExecutor(new CommandGPFlags());
        getCommand("listclaimflags").setExecutor(new CommandListClaimFlags());
        getCommand("setclaimflag").setExecutor(new CommandSetClaimFlag());
        getCommand("setclaimflagplayer").setExecutor(new CommandSetClaimFlagPlayer());
        getCommand("setdefaultclaimflag").setExecutor(new CommandSetDefaultClaimFlag());
        getCommand("setserverflag").setExecutor(new CommandSetServerFlag());
        getCommand("setworldflag").setExecutor(new CommandSetWorldFlag());
        getCommand("unsetclaimflag").setExecutor(new CommandUnsetClaimFlag());
        getCommand("unsetclaimflagplayer").setExecutor(new CommandUnsetClaimFlagPlayer());
        getCommand("unsetdefaultclaimflag").setExecutor(new CommandUnsetDefaultClaimFlag());
        getCommand("unsetserverflag").setExecutor(new CommandUnsetServerFlag());
        getCommand("unsetworldflag").setExecutor(new CommandUnsetWorldFlag());
        getCommand("buyedittrust").setExecutor(new CommandBuyEditTrust());


        new Metrics(this);

        float finish = (float) (System.currentTimeMillis() - start) / 1000;
        Util.log("Successfully loaded in &b%.2f seconds", finish);
        if (getDescription().getVersion().contains("SNAPSHOT")) {
            Util.log("&eYou are running a Beta version, things may not operate as expected");
        }
    }

    public void onDisable() {
        FlagDef_ViewContainers.getViewingInventories().forEach(inv -> {
            inv.setContents(new ItemStack[inv.getSize()]);
            new ArrayList<>(inv.getViewers()).forEach(HumanEntity::closeInventory);
        });
        if (flagsDataStore != null) {
            flagsDataStore.close();
            flagsDataStore = null;
        }
        instance = null;
        playerListener = null;
    }

    /**
     * Reload the config file
     */
    public void reloadConfig() {
        this.worldSettingsManager = new WorldSettingsManager();
        new GPFlagsConfig(this);
    }

    /**
     * Get an instance of this plugin
     *
     * @return Instance of this plugin
     */
    public static GPFlags getInstance() {
        return instance;
    }

    /**
     * Get an instance of the flags data store
     *
     * @return Instance of the flags data store
     */
    public FlagsDataStore getFlagsDataStore() {
        return this.flagsDataStore;
    }

    /**
     * Get an instance of the flag manager
     *
     * @return Instance of the flag manager
     */
    public FlagManager getFlagManager() {
        return this.flagManager;
    }

    /**
     * Get an instance of the world settings manager
     *
     * @return Instance of the world settings manager
     */
    public WorldSettingsManager getWorldSettingsManager() {
        return this.worldSettingsManager;
    }

    /**
     * Get an instance of the player listener class
     *
     * @return Instance of the player listener class
     */
    public PlayerListener getPlayerListener() {
        return this.playerListener;
    }

}
