package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

import java.util.List;

/**
 * Base flag definition
 * <p>When creating a new flag, extend from this class</p>
 */
public abstract class FlagDefinition implements Listener {

    private FlagManager flagManager;
    private int instances = 0;
    protected GPFlags plugin;

    public FlagDefinition(FlagManager manager, GPFlags plugin) {
        this.flagManager = manager;
        this.plugin = plugin;
    }

    public abstract String getName();

    public SetFlagResult ValidateParameters(String parameters) {
        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    public abstract MessageSpecifier getSetMessage(String parameters);

    public abstract MessageSpecifier getUnSetMessage();

    public abstract List<FlagType> getFlagType();

    public Flag GetFlagInstanceAtLocation(Location location, Player player) {
        Flag flag = null;
        if (GriefPrevention.instance.claimsEnabledForWorld(location.getWorld())) {
            Claim cachedClaim = null;
            PlayerData playerData = null;
            if (player != null) {
                playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
                cachedClaim = playerData.lastClaim;
            }

            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, cachedClaim);
            if (claim != null) {
                if (playerData != null) {
                    playerData.lastClaim = claim;
                }

                flag = this.flagManager.getFlag(claim.getID().toString(), this);
                if (flag != null && !flag.getSet()) return null;

                if (flag == null && claim.parent != null) {
                    flag = this.flagManager.getFlag(claim.parent.getID().toString(), this);
                    if (flag != null && !flag.getSet()) return null;
                }

                if (flag == null) {
                    flag = this.flagManager.getFlag(FlagManager.DEFAULT_FLAG_ID.toString(), this);
                    if (flag != null && !flag.getSet()) return null;
                }
            }
        }

        if (flag == null) {
            flag = this.flagManager.getFlag(location.getWorld().getName(), this);
            if (flag != null && !flag.getSet()) return null;
        }

        if (flag == null) {
            flag = this.flagManager.getFlag("everywhere", this);
            if (flag != null && !flag.getSet()) return null;
        }

        return flag;
    }

    public void incrementInstances() {
        if (++this.instances == 1) {
            this.firstTimeSetup();
        }
    }

    public void firstTimeSetup() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    /**
     * Flag types <br>Defines the types of claims a flag can be set in
     */
    public enum FlagType {
        /**
         * Flag can be set in a claim
         */
        CLAIM("&aCLAIM"),
        /**
         * Flag can be set for an entire world
         */
        WORLD("&6WORLD"),
        /**
         * Flag can bet set for the entire server
         */
        SERVER("&3SERVER");

        String name;

        FlagType(String string){
            this.name = string;
        }

        @Override
        public String toString() {
            return ChatColor.translateAlternateColorCodes('&', name + "&7");
        }
    }

}
