package me.ryanhamshire.GPFlags.flags;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class FlagDef_NoHunger extends TimedPlayerFlagDefinition {

    private ConcurrentHashMap<UUID, Integer> lastFoodMap = new ConcurrentHashMap<>();

    @Override
    public long getPlayerCheckFrequency_Ticks() {
        return 100L;
    }

    @Override
    public void processPlayer(Player player) {
        if (player.getFoodLevel() >= 20) return;

        UUID playerID = player.getUniqueId();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if (flag != null) {
            Integer lastFoodLevel = this.lastFoodMap.get(playerID);
            if (lastFoodLevel != null && player.getFoodLevel() < lastFoodLevel) {
                player.setFoodLevel(lastFoodLevel);
            }

            int healAmount = 0;
            if (flag.parameters != null && !flag.parameters.isEmpty()) {
                try {
                    healAmount = Integer.parseInt(flag.parameters);
                } catch (NumberFormatException e) {
                    GPFlags.addLogEntry("Problem with hunger level regen amount @ " + player.getLocation().getBlock().getLocation().toString());
                }
            }

            int newFoodLevel = healAmount + player.getFoodLevel();
            player.setFoodLevel((Math.min(20, newFoodLevel)));
            player.setSaturation(player.getFoodLevel());
        }

        this.lastFoodMap.put(playerID, player.getFoodLevel());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getCause() != DamageCause.STARVATION) return;
        if (event.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;

        event.setCancelled(true);
        player.setFoodLevel(player.getFoodLevel() + 1);
        player.setSaturation(player.getFoodLevel());
    }

    public FlagDef_NoHunger(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public String getName() {
        return "NoHunger";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableNoHunger, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableNoHunger);
    }

    @Override
    public SetFlagResult ValidateParameters(String parameters) {
        if (!parameters.isEmpty()) {
            int amount;
            try {
                amount = Integer.parseInt(parameters);
                if (amount < 0) {
                    return new SetFlagResult(false, new MessageSpecifier(Messages.FoodRegenInvalid));
                }
            } catch (NumberFormatException e) {
                return new SetFlagResult(false, new MessageSpecifier(Messages.FoodRegenInvalid));
            }
        } else {
            return new SetFlagResult(false, new MessageSpecifier(Messages.FoodRegenInvalid));
        }

        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
