package me.ryanhamshire.GPFlags.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.material.Door;
import org.bukkit.material.Gate;
import org.bukkit.material.Openable;
import org.bukkit.material.TrapDoor;

import java.util.ArrayList;

public class Legacy implements VersionControl {

    public boolean isMonster(Entity entity) {
        EntityType type = entity.getType();
        return (type == EntityType.GHAST || type == EntityType.MAGMA_CUBE ||
                type == EntityType.SHULKER || type == EntityType.SLIME);
    }

    public ArrayList<String> getDefaultBiomes() {
        ArrayList<String> biomes = new ArrayList<>();
        biomes.add("MUSHROOM_ISLAND");
        biomes.add("MUSHROOM_ISLAND_SHORE");
        return biomes;
    }

    @Override
    public boolean isOpenable(Block block) {
        return block.getState().getData() instanceof Openable;
    }

    @Override
    public boolean isGate(Block block) {
        return block.getState().getData() instanceof Gate;
    }

    @Override
    public boolean isDoor(Block block) {
        return block.getState().getData() instanceof Door;
    }

    @Override
    public boolean isTrapDoor(Block block) {
        return block.getState().getData() instanceof TrapDoor;
    }

}
