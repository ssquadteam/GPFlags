package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Base flag definition for time based flags
 * <p>When creating flags which require a timer extend from this class</p>
 */
public abstract class TimedPlayerFlagDefinition extends FlagDefinition implements Listener, Runnable {

    private static long tickOffset = 1L;
    private ConcurrentLinkedQueue<ConcurrentLinkedQueue<Player>> playerQueueQueue = new ConcurrentLinkedQueue<>();
    private long taskIntervalTicks;

    public TimedPlayerFlagDefinition(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    public abstract long getPlayerCheckFrequency_Ticks();

    public abstract void processPlayer(Player player);
    
    private boolean isSetup = false;

    public void firstTimeSetup() {
        super.firstTimeSetup();
        
        if (isSetup) return;
        
        this.taskIntervalTicks = this.getPlayerCheckFrequency_Ticks() / Bukkit.getServer().getMaxPlayers();
        if (this.taskIntervalTicks < 1) this.taskIntervalTicks = 1;
        GPFlags.getScheduler().getImpl().runTimer(this, 50L * TimedPlayerFlagDefinition.tickOffset++, 50 * Math.max(this.taskIntervalTicks, 1), TimeUnit.MILLISECONDS);
        isSetup = true;
    }

    @Override
    public void run() {
        ConcurrentLinkedQueue<Player> playerQueue = this.playerQueueQueue.poll();
        if (playerQueue == null) {
            long iterationsToProcessAllPlayers = this.getPlayerCheckFrequency_Ticks() / this.taskIntervalTicks;
            if (iterationsToProcessAllPlayers < 1) iterationsToProcessAllPlayers = 1;
            for (int i = 0; i < iterationsToProcessAllPlayers; i++) {
                this.playerQueueQueue.add(new ConcurrentLinkedQueue<Player>());
            }

            @SuppressWarnings("unchecked")
            Collection<Player> players = (Collection<Player>) Bukkit.getServer().getOnlinePlayers();
            for (Player player : players) {
                ConcurrentLinkedQueue<Player> queueToFill = this.playerQueueQueue.poll();
                queueToFill.add(player);
                this.playerQueueQueue.add(queueToFill);
            }

            playerQueue = this.playerQueueQueue.poll();
        }

        Player player;
        while ((player = playerQueue.poll()) != null) {
            try {
                this.processPlayer(player);
            } catch (Exception e) {
                if (player.isOnline()) {
                    e.printStackTrace();
                }
            }
        }
    }

}
