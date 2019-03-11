package me.ryanhamshire.GPFlags.util;

import org.bukkit.entity.Entity;

import java.util.ArrayList;

public interface VersionControl {

    boolean isMonster(Entity entity);

    ArrayList<String> getDefaultBiomes();

}
