package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Arrays;
import java.util.List;

public class FlagDef_InfiniteArrows extends FlagDefinition {

    public FlagDef_InfiniteArrows(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        ItemStack bow = event.getBow();
        if (bow == null) return;
        if (bow.getType() == Material.CROSSBOW) return;

        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) return;
        Player player = (Player) livingEntity;

        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;

        Entity projectile = event.getProjectile();
        if (!(projectile instanceof Arrow)) return;
        Arrow arrow = (Arrow) projectile;

        event.setConsumeItem(false);
        player.updateInventory();
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
    }

    @Override
    public String getName() {
        return "InfiniteArrows";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableInfiniteArrows);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableInfiniteArrows);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
