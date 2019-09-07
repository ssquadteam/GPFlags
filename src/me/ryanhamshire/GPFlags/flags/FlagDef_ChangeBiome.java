package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;

import java.util.Collections;
import java.util.List;

public class FlagDef_ChangeBiome extends FlagDefinition {

    @SuppressWarnings("deprecation")
    private void changeBiome(Location greater, Location lesser, Biome biome) {
        int lX = (int) lesser.getX();
        int lZ = (int) lesser.getZ();
        int gX = (int) greater.getX();
        int gZ = (int) greater.getZ();
        for (int x = lX; x < gX; x++) {
            for (int z = lZ; z < gZ; z++) {
                greater.getWorld().getBlockAt(x, 60, z).setBiome(biome);
                greater.getWorld().refreshChunk(x >> 4, z >> 4);
            }
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
