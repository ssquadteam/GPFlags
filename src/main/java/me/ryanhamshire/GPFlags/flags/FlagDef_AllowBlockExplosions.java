package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GriefPrevention.Claim;

import java.util.Arrays;
import java.util.List;

public class FlagDef_AllowBlockExplosions extends FlagDefinition {

    public FlagDef_AllowBlockExplosions(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    public void onFlagSet(Claim claim, String string) {
        claim.areExplosivesAllowed = true;
    }

    public void onFlagUnset(Claim claim) {
        claim.areExplosivesAllowed = false;
    }

    @Override
    public String getName() {
        return "AllowBlockExplosions";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledAllowBlockExplosions);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledAllowBlockExplosions);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM);
    }

}
