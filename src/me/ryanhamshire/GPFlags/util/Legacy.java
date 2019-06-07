package me.ryanhamshire.GPFlags.util;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.material.Openable;

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

}
