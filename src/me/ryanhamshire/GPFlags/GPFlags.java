package me.ryanhamshire.GPFlags;

import com.google.common.io.Files;
import me.ryanhamshire.GPFlags.metrics.Metrics;
import me.ryanhamshire.GPFlags.util.Current;
import me.ryanhamshire.GPFlags.util.Legacy;
import me.ryanhamshire.GPFlags.util.VersionControl;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class GPFlags extends JavaPlugin {
    private static VersionControl vc;

    //for convenience, a reference to the instance of this plugin
    static GPFlags instance;

    //for logging to the console and log file
    private static Logger log = Logger.getLogger("Minecraft");

    //this handles customizable messages
    public FlagsDataStore flagsDataStore;

    //this handles flags
    public FlagManager flagManager = new FlagManager();

    //this handles worldwide settings (aka global flags)
    public WorldSettingsManager worldSettingsManager = new WorldSettingsManager();

    private boolean registeredFlagDefinitions = false;

    //adds a server log entry
    static synchronized void AddLogEntry(String entry) {
        log.info(ChatColor.translateAlternateColorCodes('&', "&7[&bGPFlags&7] " + entry));
    }

    public void onEnable() {
        int ver = Integer.valueOf(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].split("_")[1]);

        // Check if server is running MC 1.13+ (API Changes)
        if (ver >= 13) {
            vc = new Current();
            AddLogEntry(ChatColor.GREEN + "1.13+ Version Loaded");
        } else {
            vc = new Legacy();
            AddLogEntry(ChatColor.GREEN + "Legacy Version Loaded");
        }

        instance = this;

        this.loadConfig();

        @SuppressWarnings("unused")
        Metrics metrics = new Metrics(this);

        AddLogEntry("Boot finished.");
        if (getDescription().getVersion().contains("Beta")) {
            AddLogEntry(ChatColor.YELLOW + "You are running a Beta version, things may not operate as expected");
        }
    }

    private void loadConfig() {
        this.flagManager.clear();
        this.worldSettingsManager = new WorldSettingsManager();

        //load the config if it exists
        FileConfiguration inConfig = YamlConfiguration.loadConfiguration(new File(FlagsDataStore.configFilePath));
        FileConfiguration outConfig = new YamlConfiguration();

        List<World> worlds = this.getServer().getWorlds();
        ArrayList<String> worldSettingsKeys = new ArrayList<>();
        for (World world : worlds) {
            worldSettingsKeys.add(world.getName());
        }
        // As a test I am removing this, I don't even know why its in here, since GPFlags supports multiverse worlds
        //worldSettingsKeys.add(this.worldSettingsManager.OtherWorldsKey);

        for (String worldName : worldSettingsKeys) {
            WorldSettings settings = this.worldSettingsManager.Create(worldName);

            settings.worldGamemodeDefault = inConfig.getString("World Flags." + worldName + ".Default Gamemode", "survival");
            if (!settings.worldGamemodeDefault.equalsIgnoreCase("survival") && !settings.worldGamemodeDefault.equalsIgnoreCase("creative") &&
                    !settings.worldGamemodeDefault.equalsIgnoreCase("adventure") && !settings.worldGamemodeDefault.equalsIgnoreCase("spectator")) {
                settings.worldGamemodeDefault = "survival";
            }
            outConfig.set("World Flags." + worldName + ".Default Gamemode", settings.worldGamemodeDefault);

            settings.pvpRequiresClaimFlag = inConfig.getBoolean("World Flags." + worldName + ".PvP Only In PvP-Flagged Claims", false);
            outConfig.set("World Flags." + worldName + ".PvP Only In PvP-Flagged Claims", settings.pvpRequiresClaimFlag);

            settings.pvpDeniedMessage = inConfig.getString("World Flags." + worldName + ".Message To Send When PvP Is Denied",
                    "Player vs. player combat is restricted to designated areas.");
            outConfig.set("World Flags." + worldName + ".Message To Send When PvP Is Denied", settings.pvpDeniedMessage);

            settings.pvpEnterClaimMessageEnabled = inConfig.getBoolean("World Flags." + worldName + ".Send Message On Enter PvP Enabled Claim", false);
            outConfig.set("World Flags." + worldName + ".Send Message On Enter PvP Enabled Claim", settings.pvpEnterClaimMessageEnabled);

            settings.pvpEnterClaimMessage = inConfig.getString("World Flags." + worldName + ".Message",
                    "PvP is enabled in this claim, be careful");
            outConfig.set("World Flags." + worldName + ".Message", settings.pvpEnterClaimMessage);

            settings.pvpExitClaimMessageEnabled = inConfig.getBoolean("World Flags." + worldName + ".Send Message On Exit PvP Enabled Claim", false);
            outConfig.set("World Flags." + worldName + ".Send Message On Exit PvP Enabled Claim", settings.pvpExitClaimMessageEnabled);

            settings.pvpExitClaimMessage = inConfig.getString("World Flags." + worldName + ".ExitMessage", "PvP is disabled in this area, you are now safe");
            outConfig.set("World Flags." + worldName + ".ExitMessage", settings.pvpExitClaimMessage);

            // Adds default biomes to be ignored in the ChangeBiome flag
            settings.biomeBlackList = inConfig.getList("World Flags." + worldName + ".Biomes.Blacklist", getVersionControl().getDefaultBiomes());
            outConfig.set("World Flags." + worldName + ".Biomes.Blacklist", settings.biomeBlackList);

            outConfig.options().header("GriefPrevention Flags\n" + "Plugin Version: " + this.getDescription().getVersion() +
                    "\nServer Version: " + getServer().getVersion() + "\n\n");
        }

        try {
            outConfig.save(FlagsDataStore.configFilePath);
            AddLogEntry("Finished loading configuration.");
        } catch (IOException exception) {
            AddLogEntry("Unable to write to the configuration file at \"" + FlagsDataStore.configFilePath + "\"");
        }

        //register flag definitions
        if (!this.registeredFlagDefinitions) {
            this.registeredFlagDefinitions = true;
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoMonsterSpawns(this.flagManager, this));
            FlagDef_AllowPvP allowPvPDef = new FlagDef_AllowPvP(this.flagManager, this, this.worldSettingsManager);
            allowPvPDef.FirstTimeSetup();
            this.flagManager.RegisterFlagDefinition(allowPvPDef);
            this.flagManager.RegisterFlagDefinition(new FlagDef_EnterMessage(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_ExitMessage(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_EnterCommand(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_EnterPlayerCommand(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_ExitCommand(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_ExitPlayerCommand(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_RespawnLocation(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_KeepInventory(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_InfiniteArrows(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_KeepLevel(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NetherPortalPlayerCommand(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NetherPortalConsoleCommand(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoCombatLoot(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoMobSpawns(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoPlayerDamage(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoEnter(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoMobDamage(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoFluidFlow(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_HealthRegen(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoHunger(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_CommandWhiteList(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_CommandBlackList(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoFlight(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_TrappedDestination(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoLootProtection(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoEnderPearl(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoExpiration(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoLeafDecay(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoPetDamage(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoWeatherChange(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoItemPickup(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoChorusFruit(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_SpleefArena(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoItemDrop(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoGrowth(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_OwnerFly(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_OwnerMemberFly(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoEnterPlayer(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_PlayerWeather(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_PlayerTime(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_PlayerGamemode(this.flagManager, this, this.worldSettingsManager));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoVineGrowth(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoSnowForm(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoIceForm(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoFireSpread(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoFireDamage(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoFallDamage(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_EnterCommand_Owner(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_EnterCommand_Members(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_ExitCommand_Owner(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_ExitCommand_Members(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoExplosionDamage(this.flagManager, this));

            this.flagManager.RegisterFlagDefinition(new FlagDef_ChangeBiome(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoOpenDoors(this.flagManager, this));
            this.flagManager.RegisterFlagDefinition(new FlagDef_NoVehicle(this.flagManager, this));

            //try to hook into mcMMO
            try {
                if (Bukkit.getPluginManager().getPlugin("mcMMO") != null) {
                    this.flagManager.RegisterFlagDefinition(new FlagDef_NoMcMMOSkills(this.flagManager, this));
                    this.flagManager.RegisterFlagDefinition(new FlagDef_NoMcMMODeathPenalty(this.flagManager, this));
                    // Experimental
                    this.flagManager.RegisterFlagDefinition(new FlagDef_NoMcMMOXP(this.flagManager, this));
                }
            }
            //if failed, we just won't have those flags available
            catch (NoClassDefFoundError ignore) {
            }
        } else {
            ((FlagDef_PlayerGamemode) this.flagManager.GetFlagDefinitionByName("PlayerGamemode")).updateSettings(this.worldSettingsManager);
            ((FlagDef_AllowPvP) this.flagManager.GetFlagDefinitionByName("AllowPvP")).updateSettings(this.worldSettingsManager);
        }

        try {
            this.flagsDataStore = new FlagsDataStore();

            File flagsFile = new File(FlagsDataStore.flagsFilePath);
            List<MessageSpecifier> errors = this.flagManager.Load(flagsFile);
            if (errors.size() > 0) {
                File errorFile = new File(FlagsDataStore.flagsErrorFilePath);
                Files.copy(flagsFile, errorFile);
                for (MessageSpecifier error : errors) {
                    GPFlags.AddLogEntry("Load Error: " + this.flagsDataStore.getMessage(error.messageID, error.messageParams));
                }
                GPFlags.AddLogEntry("Problems encountered reading the flags data file! " +
                        "Please share this log and your 'flagsError.yml' file with the developer.");
            }
        } catch (Exception e) {
            GPFlags.AddLogEntry("Unable to initialize the file system data store.  Details:");
            GPFlags.AddLogEntry(e.getMessage());
            e.printStackTrace();
        }

        //drop any flags which no longer correspond to existing land claims (maybe they were deleted)
        Collection<Claim> topLevelClaims = GriefPrevention.instance.dataStore.getClaims();
        HashSet<String> validIDs = new HashSet<String>();
        for (Claim claim : topLevelClaims) {
            validIDs.add(claim.getID().toString());
            for (Claim subclaim : claim.children) {
                validIDs.add(subclaim.getID().toString());
            }
        }
        this.flagManager.removeExceptClaimIDs(validIDs);
        AddLogEntry("Finished loading data.");
    }

    //handles slash commands
    @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = null;
        if (cmd.getName().equalsIgnoreCase("allflags")) {
            for (FlagDefinition flag : this.flagManager.GetFlagDefinitions()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        flag.getName() + " &7" + flag.getFlagType()));
            }
            return true;
        }
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            if (!cmd.getLabel().contains("server") && !cmd.getName().equalsIgnoreCase("GPFReload") &&
                    !cmd.getName().equalsIgnoreCase("SetClaimFlagPlayer")) {
                getLogger().info(ChatColor.RED + "This command can only be issued by a player");
                return true;
            }
        }
        if (cmd.getName().equalsIgnoreCase("GPFReload")) {
            this.loadConfig();
            GPFlags.sendMessage(player, TextMode.Success, Messages.ReloadComplete);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("SetDefaultClaimFlag")) {
            if (args.length < 1) return false;

            String flagName = args[0];

            FlagDefinition def = this.flagManager.GetFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, this.getFlagDefsMessage(player));
                return true;
            }

            if (!this.playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            if (!def.getFlagType().contains(FlagDefinition.FlagType.CLAIM)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagInClaim);
                return true;
            }

            String[] params = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                params[i - 1] = args[i];
            }

            SetFlagResult result = this.flagManager.SetFlag(FlagManager.DEFAULT_FLAG_ID, def, true, params);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            if (result.success) {
                GPFlags.sendMessage(player, color, Messages.DefaultFlagSet);
                this.flagManager.Save();
            } else {
                GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            }

            return true;
        } else if (cmd.getName().equalsIgnoreCase("UnSetDefaultClaimFlag")) {
            if (args.length < 1) return false;

            String flagName = args[0];
            FlagDefinition def = this.flagManager.GetFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, this.getFlagDefsMessage(player));
                return true;
            }

            if (!this.playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            SetFlagResult result = this.flagManager.UnSetFlag(FlagManager.DEFAULT_FLAG_ID, def);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            if (result.success) {
                GPFlags.sendMessage(player, color, Messages.DefaultFlagUnSet);
                this.flagManager.Save();
            } else {
                GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            }

            return true;
        } else if (cmd.getName().equalsIgnoreCase("SetServerFlag")) {
            if (args.length < 1) return false;

            String flagName = args[0];
            FlagDefinition def = this.flagManager.GetFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, this.getFlagDefsMessage(player));
                return true;
            }

            if (!this.playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            if (!def.getFlagType().contains(FlagDefinition.FlagType.SERVER)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagInServer);
                return true;
            }

            String[] params = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                params[i - 1] = args[i];
            }

            SetFlagResult result = this.flagManager.SetFlag("everywhere", def, true, params);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            if (result.success) {
                GPFlags.sendMessage(player, color, Messages.ServerFlagSet);
                this.flagManager.Save();
            } else {
                GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            }

            return true;
        } else if (cmd.getName().equalsIgnoreCase("UnSetServerFlag")) {
            if (args.length < 1) return false;

            String flagName = args[0];
            FlagDefinition def = this.flagManager.GetFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, this.getFlagDefsMessage(player));
                return true;
            }

            if (!this.playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            SetFlagResult result = this.flagManager.UnSetFlag("everywhere", def);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            if (result.success) {
                GPFlags.sendMessage(player, color, Messages.ServerFlagUnSet);
                this.flagManager.Save();
            } else {
                GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            }

            return true;
        }

        // Set a claimFlag for a player from console
        if (cmd.getName().equalsIgnoreCase("SetClaimFlagPlayer")) {
            if (args.length < 2) return false;
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sendMessage(sender, "&c" + args[0] + " &7is not online");
                return false;
            }
            PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);
            if (claim == null || claim.allowEdit(player) != null) {
                sendMessage(sender, "&cThis player is not standing in a claim they own");
                return false;
            }

            String flagName = args[1];
            FlagDefinition def = this.flagManager.GetFlagDefinitionByName(flagName);
            if (def == null) {
                sendMessage(sender, "&c" + args[1] + "&7 is not a valid flag");
                return false;
            }
            if (!def.getFlagType().contains(FlagDefinition.FlagType.CLAIM)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagInClaim);
                return true;
            }

            String[] params = new String[args.length - 2];
            for (int i = 2; i < args.length; i++) {
                params[i - 2] = args[i];
            }

            SetFlagResult result = this.flagManager.SetFlag(claim.getID().toString(), def, true, params);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            GPFlags.sendMessage(sender, color, result.message.messageID, result.message.messageParams);
            if (result.success) {
                this.flagManager.Save();
                sendMessage(sender, "&7Flag &b" + def.getName() + " &7successfully set in &b" + player.getName() + "&7's claim.");
                return true;
            }

            return true;
        }

        if (player == null) {
            GPFlags.AddLogEntry("You must be logged into the game to use that command.");
        }

        if (cmd.getName().equalsIgnoreCase("SetWorldFlag") && player != null) {
            if (args.length < 1) return false;

            String flagName = args[0];
            FlagDefinition def = this.flagManager.GetFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, this.getFlagDefsMessage(player));
                return true;
            }

            if (!this.playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            if (!def.getFlagType().contains(FlagDefinition.FlagType.WORLD)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagInWorld);
                return true;
            }

            String[] params = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                params[i - 1] = args[i];
            }

            SetFlagResult result = this.flagManager.SetFlag(player.getWorld().getName(), def, true, params);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            if (result.success) {
                GPFlags.sendMessage(player, color, Messages.WorldFlagSet);
                this.flagManager.Save();
            } else {
                GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            }

            return true;
        } else if (cmd.getName().equalsIgnoreCase("UnSetWorldFlag") && player != null) {
            if (args.length < 1) return false;

            String flagName = args[0];
            FlagDefinition def = this.flagManager.GetFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, this.getFlagDefsMessage(player));
                return true;
            }

            if (!this.playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            SetFlagResult result = this.flagManager.UnSetFlag(player.getWorld().getName(), def);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            if (result.success) {
                GPFlags.sendMessage(player, color, Messages.WorldFlagUnSet);
                this.flagManager.Save();
            } else {
                GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            }

            return true;
        } else if (cmd.getName().equalsIgnoreCase("ListClaimFlags") && player != null) {
            PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());

            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);

            Collection<Flag> flags;
            boolean flagsFound = false;
            StringBuilder builder1 = new StringBuilder();
            StringBuilder builder2 = new StringBuilder();
            StringBuilder builder3 = new StringBuilder();
            if (claim != null) {
                flags = this.flagManager.GetFlags(claim.getID().toString());
                for (Flag flag : flags) {
                    flagsFound = true;
                    builder1.append((flag.getSet() ? "+" : "-") + flag.flagDefinition.getName()).append(" ");
                }

                if (claim.parent != null) {
                    flags = this.flagManager.GetFlags(claim.parent.getID().toString());
                    for (Flag flag : flags) {
                        flagsFound = true;
                        builder2.append((flag.getSet() ? "+" : "-") + flag.flagDefinition.getName()).append(" ");
                    }
                }

                flags = this.flagManager.GetFlags(FlagManager.DEFAULT_FLAG_ID);
                for (Flag flag2 : flags) {
                    flagsFound = true;
                    builder3.append((flag2.getSet() ? "+" : "-") + flag2.flagDefinition.getName()).append(" ");
                }
            }

            StringBuilder builder4 = new StringBuilder();
            flags = this.flagManager.GetFlags(player.getWorld().getName());
            for (Flag flag3 : flags) {
                flagsFound = true;
                builder4.append((flag3.getSet() ? "+" : "-") + flag3.flagDefinition.getName()).append(" ");
            }

            StringBuilder builder5 = new StringBuilder();
            flags = this.flagManager.GetFlags("everywhere");
            for (Flag flag4 : flags) {
                flagsFound = true;
                builder5.append((flag4.getSet() ? "+" : "-") + flag4.flagDefinition.getName()).append(" ");
            }

            if (builder1.length() > 0)
                GPFlags.sendMessage(player, TextMode.Info, Messages.FlagsClaim, builder1.toString());
            if (builder2.length() > 0)
                GPFlags.sendMessage(player, TextMode.Info, Messages.FlagsParent, builder2.toString());
            if (builder3.length() > 0)
                GPFlags.sendMessage(player, TextMode.Info, Messages.FlagsDefault, builder3.toString());
            if (builder4.length() > 0)
                GPFlags.sendMessage(player, TextMode.Info, Messages.FlagsWorld, builder4.toString());
            if (builder5.length() > 0)
                GPFlags.sendMessage(player, TextMode.Info, Messages.FlagsServer, builder5.toString());

            if (!flagsFound) {
                GPFlags.sendMessage(player, TextMode.Info, Messages.NoFlagsHere);
            }

            return true;
        }

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);

        if (claim == null) {
            GPFlags.sendMessage(player, TextMode.Err, Messages.StandInAClaim);
            return true;
        }

        Long claimID = claim.getID();
        if (claimID == null || claimID == -1) {
            GPFlags.sendMessage(player, TextMode.Err, Messages.UpdateGPForSubdivisionFlags);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("SetClaimFlag") && player != null) {
            if (args.length < 1) return false;

            String flagName = args[0];
            FlagDefinition def = this.flagManager.GetFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, this.getFlagDefsMessage(player));
                return true;
            }

            if (!this.playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            if (!def.getFlagType().contains(FlagDefinition.FlagType.CLAIM)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagInClaim);
                return true;
            }

            if (claim.allowEdit(player) != null) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NotYourClaim);
                return true;
            }

            String[] params = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                params[i - 1] = args[i];
            }

            //TODO stop owner/ownermember fly flags from joining
            Collection<Flag> flags;
            flags = this.flagManager.GetFlags(claim.getID().toString());
            for (Flag flag : flags) {
                if (args[0].equalsIgnoreCase("OwnerFly")) {
                    if (flag.flagDefinition.getName().equalsIgnoreCase("OwnerMemberFly")) {
                        GPFlags.sendMessage(player, TextMode.Warn, Messages.NoOwnerFlag);
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("OwnerMemberFly")) {
                    if (flag.flagDefinition.getName().equalsIgnoreCase("OwnerFly")) {
                        GPFlags.sendMessage(player, TextMode.Warn, Messages.NoOwnerFlag);
                        return true;
                    }
                }
            }

            // TODO SET BIOME
            if (flagName.equalsIgnoreCase("ChangeBiome")) {
                FlagDef_ChangeBiome flagD = new FlagDef_ChangeBiome(flagManager, this);
                Biome biome;
                try {
                    biome = Biome.valueOf(params[0].toUpperCase().replace(" ", "_"));
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "Invalid biome");
                    return true;
                }
                if (worldSettingsManager.Get(player.getWorld()).biomeBlackList.contains(biome.toString())) {
                    if (!(player.hasPermission("gpflags.bypass"))) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&cThe biome &b" + biome + " &chas been blacklisted in this world"));
                        return true;
                    }
                }
                flagD.changeBiome(claim, biome.toString());

            }

            SetFlagResult result = this.flagManager.SetFlag(claimID.toString(), def, true, params);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            if (result.success) this.flagManager.Save();

            return true;
        } else if (cmd.getName().equalsIgnoreCase("UnSetClaimFlag") && player != null) {
            if (args.length < 1) return false;

            String flagName = args[0];

            FlagDefinition def = this.flagManager.GetFlagDefinitionByName(flagName);
            if (def == null) {
                GPFlags.sendMessage(player, TextMode.Err, this.getFlagDefsMessage(player));
                return true;
            }

            if (!this.playerHasPermissionForFlag(def, player)) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NoFlagPermission);
                return true;
            }

            if (claim.allowEdit(player) != null) {
                GPFlags.sendMessage(player, TextMode.Err, Messages.NotYourClaim);
                return true;
            }

            // TODO RESET BIOME
            if (flagName.equalsIgnoreCase("ChangeBiome")) {
                FlagDef_ChangeBiome flagD = new FlagDef_ChangeBiome(flagManager, this);
                flagD.resetBiome(claim.getID());
            }

            SetFlagResult result = this.flagManager.UnSetFlag(claimID.toString(), def);
            ChatColor color = result.success ? TextMode.Success : TextMode.Err;
            GPFlags.sendMessage(player, color, result.message.messageID, result.message.messageParams);
            if (result.success) this.flagManager.Save();

            return true;
        }

        return false;
    }

    private boolean playerHasPermissionForFlag(FlagDefinition flagDef, Permissible player) {
        String flagName = flagDef.getName();
        return player == null || player.hasPermission("gpflags.allflags") || player.hasPermission("gpflags." +
                flagName) || player.hasPermission("gpflags." + flagName.toLowerCase());
    }

    public void onDisable() {
        this.flagsDataStore.close();
    }

    private static void sendMessage(CommandSender player, ChatColor color, MessageSpecifier specifier) {
        sendMessage(player, color, specifier.messageID, specifier.messageParams);
    }

    //sends a formatted message to a player
    static void sendMessage(CommandSender player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    //sends a color-coded message to a player
    static void sendMessage(CommandSender player, ChatColor color, Messages messageID, String... args) {
        sendMessage(player, color, messageID, 0, args);
    }

    //sends a color-coded message to a player
    static void sendMessage(CommandSender player, ChatColor color, Messages messageID, long delayInTicks, String... args) {
        String message = GPFlags.instance.flagsDataStore.getMessage(messageID, args);
        sendMessage(player, color, message, delayInTicks);
    }

    //sends a color-coded message to a player
    static void sendMessage(CommandSender player, ChatColor color, String message) {
        if (message == null || message.length() == 0) return;

        if (player == null) {
            GPFlags.AddLogEntry(color + message);
        } else {
            player.sendMessage(color + message);
        }
    }

    static void sendMessage(CommandSender player, ChatColor color, String message, long delayInTicks) {
        SendPlayerMessageTask task = new SendPlayerMessageTask(player, color, message);
        if (delayInTicks > 0) {
            GPFlags.instance.getServer().getScheduler().runTaskLater(GPFlags.instance, task, delayInTicks);
        } else {
            task.run();
        }
    }

    private MessageSpecifier getFlagDefsMessage(Permissible player) {
        StringBuilder flagDefsList = new StringBuilder();
        Collection<FlagDefinition> defs = this.flagManager.GetFlagDefinitions();
        for (FlagDefinition def : defs) {
            if (this.playerHasPermissionForFlag(def, player)) {
                flagDefsList.append(def.getName() + " ");
            }
        }

        return new MessageSpecifier(Messages.InvalidFlagDefName, flagDefsList.toString());
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) throws IllegalArgumentException {
        return this.flagManager.onTabComplete(sender, command, alias, args);
    }

    public static VersionControl getVersionControl() {
        return vc;
    }

}