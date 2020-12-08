package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.WorldSettings;
import me.ryanhamshire.GPFlags.WorldSettingsManager;
import me.ryanhamshire.GPFlags.util.VersionControl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.Arrays;
import java.util.List;

public class FlagDef_NoMonsterSpawns extends FlagDefinition {

    private WorldSettingsManager settingsManager;
    private final VersionControl vc = GPFlags.getInstance().getVersionControl();

    public FlagDef_NoMonsterSpawns(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
        this.settingsManager = plugin.getWorldSettingsManager();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntitySpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (!vc.isMonster(entity)) return;

        SpawnReason reason = event.getSpawnReason();

        WorldSettings settings = this.settingsManager.get(event.getEntity().getWorld());
        if (settings.noMonsterSpawnIgnoreSpawners && (reason == SpawnReason.SPAWNER || reason == SpawnReason.SPAWNER_EGG)) {
            return;
        }

        Flag flag = this.getFlagInstanceAtLocation(event.getLocation(), null);
        if (flag == null) return;

        event.setCancelled(true);
    }

    @Override
    public String getName() {
        return "NoMonsterSpawns";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.DisableMonsterSpawns);
    }

    public void updateSettings(WorldSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.EnableMonsterSpawns);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM, FlagType.WORLD, FlagType.SERVER);
    }

}
