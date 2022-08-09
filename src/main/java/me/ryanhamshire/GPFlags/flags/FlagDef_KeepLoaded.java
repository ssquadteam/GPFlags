package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.*;

public class FlagDef_KeepLoaded extends FlagDefinition {

    public FlagDef_KeepLoaded(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onFlagSet(Claim claim, String string) {
        ArrayList<Chunk> chunks = claim.getChunks();
        for (Chunk chunk : chunks) {
            chunk.setForceLoaded(true);
            chunk.load(true);
        }
    }

    @Override
    public void onFlagUnset(Claim claim) {
        ArrayList<Chunk> chunks = claim.getChunks();
        for (Chunk chunk : chunks) {
            chunk.setForceLoaded(false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        Collection<Claim> claims = GriefPrevention.instance.dataStore.getClaims();
        for (Claim claim : claims) {
            if (GPFlags.getInstance().getFlagManager().getFlag(claim, "KeepLoaded") != null) {
                ArrayList<Chunk> chunks = claim.getChunks();
                for (Chunk chunk : chunks) {
                    chunk.setForceLoaded(true);
                    chunk.load(true);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "KeepLoaded";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableKeepLoaded);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableKeepLoaded);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Collections.singletonList(FlagType.CLAIM);
    }

}
