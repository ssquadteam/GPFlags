package me.ryanhamshire.GPFlags;


import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_PlayerGamemode extends PlayerMovementFlagDefinition implements Listener {

    private WorldSettingsManager settingsManager;

    @Override
    public boolean allowMovement(Player player, Location lastLocation)
    {
        WorldSettings settings = this.settingsManager.Get(player.getWorld());

        if(lastLocation == null) return true;
        Location to = player.getLocation();
        Flag flag = this.GetFlagInstanceAtLocation(to, player);
        if(flag == null) {
            if(this.GetFlagInstanceAtLocation(lastLocation, player) == null) return true;

            String gameMode = settings.worldGamemodeDefault;
            player.setGameMode(GameMode.valueOf(gameMode.toUpperCase()));
            GPFlags.sendMessage(player, TextMode.Warn, Messages.PlayerGamemode, gameMode);

            if(player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                Block block = player.getLocation().getBlock();
                Block below = block.getRelative(BlockFace.DOWN);
                if(below.getRelative(BlockFace.DOWN).getType() != Material.AIR && block.getRelative(BlockFace.UP).getType() == Material.AIR) return true;
                while(block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER) {
                    block = block.getRelative(BlockFace.DOWN);
                }
                player.teleport(block.getRelative(BlockFace.UP).getLocation());
            }
            return true;
        }
        if(flag == this.GetFlagInstanceAtLocation(lastLocation, player)) return true;
        String gameMode = flag.parameters;
        String playerGameMode = player.getGameMode().toString();
        if(gameMode.equalsIgnoreCase(playerGameMode)) return true;
        player.setGameMode(GameMode.valueOf(gameMode.toUpperCase()));
        GPFlags.sendMessage(player, TextMode.Warn, Messages.PlayerGamemode, gameMode);
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);
        if(flag != null) {
            String gameMode = flag.parameters;
            player.setGameMode(GameMode.valueOf(gameMode.toUpperCase()));
        }
    }

    public FlagDef_PlayerGamemode(FlagManager manager, GPFlags plugin, WorldSettingsManager settingsManager) {
        super(manager, plugin);
        this.settingsManager = settingsManager;
    }

    @Override
    SetFlagResult ValidateParameters(String parameters)
    {
        if(parameters.isEmpty())
        {
            return new SetFlagResult(false, new MessageSpecifier(Messages.PlayerGamemodeRequired));
        }
        if(!parameters.equalsIgnoreCase("survival") && !parameters.equalsIgnoreCase("creative") &&
                !parameters.equalsIgnoreCase("adventure") && !parameters.equalsIgnoreCase("spectator")) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.PlayerGamemodeRequired));
        }
        return new SetFlagResult(true, this.GetSetMessage(parameters));
    }

    public void updateSettings(WorldSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    String getName() {
        return "PlayerGamemode";
    }

    @Override
    MessageSpecifier GetSetMessage(String parameters) {
        return new MessageSpecifier(Messages.PlayerGamemodeSet, parameters);
    }

    @Override
    MessageSpecifier GetUnSetMessage() {
        return new MessageSpecifier(Messages.PlayerGamemodeUnSet);
    }

}
