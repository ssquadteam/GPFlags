package me.ryanhamshire.GPFlags.flags;


import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.util.MessagingUtil;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_PlayerGamemode extends PlayerMovementFlagDefinition implements Listener {

    public FlagDef_PlayerGamemode(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onChangeClaim(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        WorldSettings settings = this.settingsManager.get(player.getWorld());

        if (lastLocation == null) return;
        Flag flagTo = this.getFlagInstanceAtLocation(to, player);
        Flag flagFrom = this.getFlagInstanceAtLocation(lastLocation, player);
        if (flagTo == flagFrom) return;
        if (Util.shouldBypass(player, claimTo, this.getName())) return;

        if (flagTo == null) { // moving from something to null

            String defaultGamemode = settings.worldGamemodeDefault;
            changeGamemode(player, defaultGamemode, true);

            if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                Block block = player.getLocation().getBlock();
                Block fallTo = FlightManager.getFloor(block.getRelative(BlockFace.DOWN));
                player.teleport(fallTo.getRelative(BlockFace.UP).getLocation());
            }
            return;
        }
        // Moving from either null to something OR something to something
        String newGamemode = flagTo.parameters;
        String oldGamemode = player.getGameMode().toString();
        if (newGamemode.equalsIgnoreCase(oldGamemode)) return;
        changeGamemode(player, newGamemode, true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        Flag flag = this.getFlagInstanceAtLocation(loc, player);
        if (flag == null) return;
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc,false,null);
        if (Util.shouldBypass(player, claim, flag)) return;
        changeGamemode(player, flag.parameters, false);
    }

    public void changeGamemode(Player player, String gamemode, boolean sendMessage) {
        player.setGameMode(GameMode.valueOf(gamemode.toUpperCase()));
        if (sendMessage) {
            MessagingUtil.sendMessage(player, TextMode.Warn, Messages.PlayerGamemode, gamemode);
        }
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
        if (parameters.isEmpty()) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.PlayerGamemodeRequired));
        }
        if (!parameters.equalsIgnoreCase("survival") && !parameters.equalsIgnoreCase("creative") &&
                !parameters.equalsIgnoreCase("adventure") && !parameters.equalsIgnoreCase("spectator")) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.PlayerGamemodeRequired));
        }
        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public String getName() {
        return "PlayerGamemode";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.PlayerGamemodeSet, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.PlayerGamemodeUnSet);
    }

}
