package me.ryanhamshire.GPFlags.util;

import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;

public class Current implements VersionControl {

    public boolean isMonster(Entity entity) {
        EntityType type = entity.getType();
        return (type == EntityType.GHAST || type == EntityType.MAGMA_CUBE || type == EntityType.SHULKER
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

}
