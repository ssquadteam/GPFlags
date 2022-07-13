package me.ryanhamshire.GPFlags;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class VaultHook {

    public static void giveMoney(UUID uuid, double amount) {
        Economy econ = getEconomy();
        if (econ == null) return;
        econ.depositPlayer(Bukkit.getOfflinePlayer(uuid), amount);
    }

    public static boolean takeMoney(Player player, double amount) {
        Economy econ = getEconomy();
        if (econ == null) return false;
        if (econ.getBalance(player) < amount) return false;
        econ.withdrawPlayer(player, amount);
        return true;
    }

    public static Economy getEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return null;
        return rsp.getProvider();
    }
}