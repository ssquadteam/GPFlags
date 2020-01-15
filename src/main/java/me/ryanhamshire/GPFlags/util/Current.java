package me.ryanhamshire.GPFlags.util;

import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;

import java.util.ArrayList;

/**
 * <b>Internal use only</b>
 */
public class Current implements VersionControl {

    public boolean isMonster(Entity entity) {
        EntityType type = entity.getType();
        return (entity instanceof Monster || type == EntityType.GHAST || type == EntityType.MAGMA_CUBE || type == EntityType.SHULKER
                || type == EntityType.PHANTOM || type == EntityType.SLIME);
    }

    public ArrayList<String> getDefaultBiomes() {
        ArrayList<String> biomes = new ArrayList<>();
        biomes.add("MUSHROOM_FIELDS");
        biomes.add("MUSHROOM_FIELD_SHORE");
        return biomes;
    }

    @Override
    public boolean isOpenable(Block block) {
        return block.getBlockData() instanceof Openable;
    }

    @Override
    public boolean isGate(Block block) {
        return block.getBlockData() instanceof Gate;
    }

    @Override
    public boolean isDoor(Block block) {
        return block.getBlockData() instanceof Door;
    }

    @Override
    public boolean isTrapDoor(Block block) {
        return block.getBlockData() instanceof TrapDoor;
    }

}
