package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Chunk;

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
