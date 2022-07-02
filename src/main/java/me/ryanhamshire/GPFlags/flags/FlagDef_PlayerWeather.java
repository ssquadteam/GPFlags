package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.SetFlagResult;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_PlayerWeather extends PlayerMovementFlagDefinition implements Listener {

    public FlagDef_PlayerWeather(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public boolean allowMovement(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (lastLocation == null) return true;
        Flag flag = this.getFlagInstanceAtLocation(to, player);
        if (flag == null) {
            if (this.getFlagInstanceAtLocation(lastLocation, player) != null) {
                player.resetPlayerWeather();
            }
            return true;
        }

        if (flag == this.getFlagInstanceAtLocation(lastLocation, player)) return true;

        String weather = flag.parameters;
        if (weather.equalsIgnoreCase("sun")) {
            player.setPlayerWeather(WeatherType.CLEAR);
        } else if (weather.equalsIgnoreCase("rain")) {
            player.setPlayerWeather(WeatherType.DOWNFALL);
        }
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);

        if (flag != null) {
            String weather = flag.parameters;
            if (weather.equalsIgnoreCase("sun")) {
                player.setPlayerWeather(WeatherType.CLEAR);
            } else if (weather.equalsIgnoreCase("rain")) {
                player.setPlayerWeather(WeatherType.DOWNFALL);
            }
        }
    }

    @Override
    public SetFlagResult validateParameters(String parameters) {
        if (parameters.isEmpty()) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.PlayerWeatherRequired));
        }
        if (!parameters.equalsIgnoreCase("sun") && !parameters.equalsIgnoreCase("rain")) {
            return new SetFlagResult(false, new MessageSpecifier(Messages.PlayerWeatherRequired));
        }
        return new SetFlagResult(true, this.getSetMessage(parameters));
    }

    @Override
    public String getName() {
        return "PlayerWeather";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.PlayerWeatherSet, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.PlayerWeatherUnSet);
    }

}
