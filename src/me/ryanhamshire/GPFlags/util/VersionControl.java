package me.ryanhamshire.GPFlags.util;

import me.ryanhamshire.GPFlags.GPFlags;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.ArrayList;

/**
 * Version control system
 * <p>A few util methods for checking blocks and entities which change between versions
 * <br>You can get an instance of this class with {@link GPFlags#getVersionControl()}
 */
public interface VersionControl {

    /** Check if an entity is a monster
     * @param entity Entity to check
     * @return True if entity is a monster
     */
    boolean isMonster(Entity entity);

    ArrayList<String> getDefaultBiomes();

    /** Check if a block is openable (such as a door)
     * @param block Block to check
     * @return True if block is openable
     */
    boolean isOpenable(Block block);

    /** Check if a block is a gate
     * @param block Block to check
     * @return True if block is a gate
     */
    boolean isGate(Block block);

    /** Check if a block is a door
     * @param block Block to check
     * @return True if block is a door
     */
    boolean isDoor(Block block);

    /** Check if a block is a trap door
     * @param block Block to check
     * @return True if block is a trap door
     */
    boolean isTrapDoor(Block block);

}
