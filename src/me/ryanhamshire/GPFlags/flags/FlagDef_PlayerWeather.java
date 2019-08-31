package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_PlayerWeather extends PlayerMovementFlagDefinition implements Listener {

    @Override
    public boolean allowMovement(Player player, Location lastLocation)
    {
        if(lastLocation == null) return true;
        Location to = player.getLocation();
        Flag flag = this.GetFlagInstanceAtLocation(to, player);
        if(flag == null) {
            if (this.GetFlagInstanceAtLocation(lastLocation, player) != null) {
                player.resetPlayerWeather();
            }
            return true;
        }

        if(flag == this.GetFlagInstanceAtLocation(lastLocation, player)) return true;

        String weather = flag.parameters;
        if(weather.equalsIgnoreCase("sun")) {
            player.setPlayerWeather(WeatherType.CLEAR);
        } else if(weather.equalsIgnoreCase("rain")) {
            player.setPlayerWeather(WeatherType.DOWNFALL);
        }
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Flag flag = this.GetFlagInstanceAtLocation(player.getLocation(), player);

        if(flag != null) {
            String weather = flag.parameters;
            if(weather.equalsIgnoreCase("sun")) {
                player.setPlayerWeather(WeatherType.CLEAR);
            } else if(weather.equalsIgnoreCase("rain")) {
                player.setPlayerWeather(WeatherType.DOWNFALL);
            }
        }
    }

    public FlagDef_PlayerWeather(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public SetFlagResult ValidateParameters(String parameters)
    {
        if(parameters.isEmpty())
        {
            return new SetFlagResult(false, new MessageSpecifier(Messages.PlayerWeatherRequired));
        }
        if(!parameters.equalsIgnoreCase("sun") && !parameters.equalsIgnoreCase("rain")) {
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
