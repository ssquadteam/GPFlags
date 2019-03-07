package me.ryanhamshire.GPFlags.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class Legacy implements VersionControl {

    public boolean isMonster(Entity entity) {
        EntityType type = entity.getType();
        return (type == EntityType.GHAST || type == EntityType.MAGMA_CUBE ||
                type == EntityType.SHULKER || type == EntityType.SLIME);
    }

}
