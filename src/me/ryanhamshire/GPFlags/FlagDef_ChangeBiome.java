package me.ryanhamshire.GPFlags;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlagDef_ChangeBiome extends FlagDefinition {

    public void changeBiome(Location greater, Location lesser, Biome biome) {
        for (Block block : this.getBlocks(greater, lesser)) {
            block.setBiome(biome);
        }
    }

    public void changeBiome(Claim claim, String biome) {
        Location greater = claim.getGreaterBoundaryCorner();
        Location lesser = claim.getLesserBoundaryCorner();

        changeBiome(greater, lesser, Biome.valueOf(biome));
    }

    public void resetBiome(Long claimID) {
        Claim claim = GriefPrevention.instance.dataStore.getClaim(claimID);

        // Restore biome by matching with biome of block 2 north of claim
        Biome biome = claim.getLesserBoundaryCorner().getBlock().getRelative(BlockFace.NORTH, 2).getBiome();

        Location greater = claim.getGreaterBoundaryCorner();
        Location lesser = claim.getLesserBoundaryCorner();

        changeBiome(greater, lesser, biome);
    }

    @EventHandler
    public void onClaimDelete(ClaimDeletedEvent e) {
        if (e.getClaim().getOwnerName() == null) return; //don't restore a sub-claim
        Claim claim = e.getClaim();

        // Restore biome by matching with biome of block 2 north of claim
        Biome biome = claim.getLesserBoundaryCorner().getBlock().getRelative(BlockFace.NORTH, 2).getBiome();

        Location greater = claim.getGreaterBoundaryCorner();
        Location lesser = claim.getLesserBoundaryCorner();

        changeBiome(greater, lesser, biome);

    }

    // I didn't write this - Found it online, cause I was super duper lost
    public List<Block> getBlocks(Location greater, Location lesser) {

        Location loc1 = greater;
        loc1.setY(64);
        Location loc2 = lesser;
        loc2.setY(64);
        World w = greater.getWorld();

        //First of all, we create the list:
        List<Block> blocks = new ArrayList<Block>();

        //Next we will name each coordinate
        int x1 = loc1.getBlockX();
        int y1 = loc1.getBlockY();
        int z1 = loc1.getBlockZ();

        int x2 = loc2.getBlockX();
        int y2 = loc2.getBlockY();
        int z2 = loc2.getBlockZ();

        //Then we create the following integers
        int xMin, yMin, zMin;
        int xMax, yMax, zMax;
        int x, y, z;

        //Now we need to make sure xMin is always lower then xMax
        if (x1 > x2) { //If x1 is a higher number then x2
            xMin = x2;
            xMax = x1;
        } else {
            xMin = x1;
            xMax = x2;
        }

        //Same with Y
        if (y1 > y2) {
            yMin = y2;
            yMax = y1;
        } else {
            yMin = y1;
            yMax = y2;
        }

        //And Z
        if (z1 > z2) {
            zMin = z2;
            zMax = z1;
        } else {
            zMin = z1;
            zMax = z2;
        }

        //Now it's time for the loop
        for (x = xMin; x <= xMax; x++) {
            for (y = yMin; y <= yMax; y++) {
                for (z = zMin; z <= zMax; z++) {
                    Block b = new Location(w, x, y, z).getBlock();
                    blocks.add(b);
                }
            }
        }
        return blocks;

    }

    public FlagDef_ChangeBiome(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    String getName() {
        return "ChangeBiome";
    }

    @Override
    SetFlagResult ValidateParameters(String parameters) {
        if (parameters.isEmpty()) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.MessageRequired));
        }

        return new SetFlagResult(true, this.GetSetMessage(parameters));
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters) {
        return new MessageSpecifier(Messages.ChangeBiomeSet, parameters); // TODO CHANGE
    }

    @Override
    MessageSpecifier GetUnSetMessage() {
        return new MessageSpecifier(Messages.ChangeBiomeUnset); // TODO CHANGE
    }

    @Override
    List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }
}
