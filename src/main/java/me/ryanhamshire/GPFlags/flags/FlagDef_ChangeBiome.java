package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlagDef_ChangeBiome extends FlagDefinition {

    @SuppressWarnings("deprecation")
    private void changeBiome(Location greater, Location lesser, Biome biome) {
        List<Chunk> chunks = new ArrayList<>();
        int lX = (int) lesser.getX();
        int lZ = (int) lesser.getZ();
        int gX = (int) greater.getX();
        int gZ = (int) greater.getZ();
        World world = lesser.getWorld();
        assert world != null;
        for (int x = lX; x < gX; x++) {
            for (int z = lZ; z < gZ; z++) {
                for (int y = 0; y <= 255; y++) {
                    world.setBiome(x, y, z, biome);
                }
                Chunk chunk = world.getBlockAt(x, 0, z).getChunk();
                if (!chunks.contains(chunk)) {
                    chunks.add(chunk);
                }
            }
        }
        for (Chunk chunk : chunks) {
            if (!chunk.isLoaded()) continue;
            int x = chunk.getX();
            int z = chunk.getZ();
            world.refreshChunk(x, z);
        }
    }

    private void changeBiome(Claim claim, Biome biome) {
        Location greater = claim.getGreaterBoundaryCorner();
        Location lesser = claim.getLesserBoundaryCorner();
        changeBiome(greater, lesser, biome);
    }

    public boolean changeBiome(CommandSender sender, Claim claim, String biome) {
        Biome b;
        try {
            b = Biome.valueOf(biome);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Invalid biome");
            return false;
        }
        World world = claim.getLesserBoundaryCorner().getWorld();
        if (GPFlags.getInstance().getWorldSettingsManager().get(world).biomeBlackList.contains(biome)) {
            if (!(sender.hasPermission("gpflags.bypass"))) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&cThe biome &b" + biome + " &chas been blacklisted in this world"));
                return false;
            }
        }
        changeBiome(claim, b);
        return true;
    }

    public void resetBiome(Long claimID) {
        resetBiome(GriefPrevention.instance.dataStore.getClaim(claimID));
    }

    public void resetBiome(Claim claim) {
        // Restore biome by matching with biome of block 2 north of claim
        Biome biome = claim.getLesserBoundaryCorner().getBlock().getRelative(BlockFace.NORTH, 6).getBiome();

        Location greater = claim.getGreaterBoundaryCorner();
        Location lesser = claim.getLesserBoundaryCorner();

        changeBiome(greater, lesser, biome);
    }

    @EventHandler
    public void onClaimDelete(ClaimDeletedEvent e) {
        if (e.getClaim().getOwnerName() == null) return; //don't restore a sub-claim
        Claim claim = e.getClaim();

        if (GPFlags.getInstance().getFlagManager().getFlag(claim, this) == null) return; // Return if flag is non existent

        resetBiome(claim);
    }

    public FlagDef_ChangeBiome(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public String getName() {
        return "ChangeBiome";
    }

    @Override
    public SetFlagResult ValidateParameters(String parameters) {
        if (parameters.isEmpty()) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.MessageRequired));
        }

        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
	public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.ChangeBiomeSet, parameters); // TODO CHANGE
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.ChangeBiomeUnset); // TODO CHANGE
    }

    @Override
    public List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }
}
