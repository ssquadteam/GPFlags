package me.ryanhamshire.GPFlags;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GriefPrevention.Claim;
import org.apache.commons.lang.Validate;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager for flags
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FlagManager implements TabCompleter {

    private ConcurrentHashMap<String, FlagDefinition> definitions;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Flag>> flags;
    
    public static final String DEFAULT_FLAG_ID = "-2";
    
    public FlagManager() {
        this.definitions = new ConcurrentHashMap<>();
        this.flags = new ConcurrentHashMap<>();
    }

	/** Register a new flag definition
	 * @param def Flag Definition to register
	 */
    public void registerFlagDefinition(FlagDefinition def) {
        String name = def.getName();
        this.definitions.put(name.toLowerCase(),  def);
    }

	/** Get a flag definition by name
	 * @param name Name of the flag to get
	 * @return Flag definition by name
	 */
    public FlagDefinition getFlagDefinitionByName(String name)
    {
        return this.definitions.get(name.toLowerCase());
    }

	/** Get a collection of all registered flag definitions
	 * @return All registered flag definitions
	 */
    public Collection<FlagDefinition> getFlagDefinitions() {
        return new ArrayList<>(this.definitions.values());
    }
    
    public SetFlagResult setFlag(String id, FlagDefinition def, boolean isActive, String... args) {
        StringBuilder parameters = new StringBuilder();
        for(String arg : args) {
            parameters.append(arg).append(" ");
        }
        parameters = new StringBuilder(parameters.toString().trim());
        
        SetFlagResult result;
        if(isActive) {
            result = def.ValidateParameters(parameters.toString());
            if(!result.success) return result;
        } else {
            result = new SetFlagResult(true, def.getUnSetMessage());
        }
        
        Flag flag = new Flag(def, parameters.toString());
        flag.setSet(isActive);
        ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(id);
        if(claimFlags == null) {
            claimFlags = new ConcurrentHashMap<>();
            this.flags.put(id, claimFlags);
        }
        
        String key = def.getName().toLowerCase();
        if(!claimFlags.containsKey(key) && isActive) {
            def.incrementInstances();
        }
        claimFlags.put(key, flag);
        return result;
    }

	/** Get a registered flag in a claim
	 * @param claim Claim to get a flag from
	 * @param flagDef Flag definition to get
	 * @return Instance of flag
	 */
    public Flag getFlag(Claim claim, FlagDefinition flagDef) {
    	return getFlag(claim.getID().toString(), flagDef);
	}

	/** Get a registered flag in a claim
	 * @param claimID ID of claim
	 * @param flagDef Flag definition to get
	 * @return Instance of flag
	 */
    public Flag getFlag(String claimID, FlagDefinition flagDef)
    {
        return this.getFlag(claimID,  flagDef.getName());
    }

	/** Get a registered flag in a claim
	 * @param claim Claim to get a flag from
	 * @param flag Name of flag definition to get
	 * @return Instance of flag
	 */
    public Flag getFlag(Claim claim, String flag) {
    	return getFlag(claim.getID().toString(), flag);
	}

	/** Get a registered flag in a claim
	 * @param claimID ID of claim
	 * @param flag Name of flag definition to get
	 * @return Instance of flag
	 */
    public Flag getFlag(String claimID, String flag) {
        ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(claimID);
        if(claimFlags == null) return null;
        return claimFlags.get(flag.toLowerCase());
    }

	/** Get all flags in a claim
	 * @param claim Claim to get flags from
	 * @return All flags in this claim
	 */
    public Collection<Flag> getFlags(Claim claim) {
    	return getFlags(claim.getID().toString());
	}

	/** Get all flags in a claim
	 * @param claimID ID of claim
	 * @return All flags in this claim
	 */
    public Collection<Flag> getFlags(String claimID) {
        ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(claimID);
        if(claimFlags == null) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(claimFlags.values());
        }
    }

	/** Unset a flag in a claim
	 * @param claim Claim to remove flag from
	 * @param def Flag definition to remove
	 * @return Flag result
	 */
    public SetFlagResult unSetFlag(Claim claim, FlagDefinition def) {
    	return unSetFlag(claim.getID().toString(), def);
	}

	/** Unset a flag in a claim
	 * @param claimID ID of claim
	 * @param def Flag definition to remove
	 * @return Flag result
	 */
    public SetFlagResult unSetFlag(String claimID, FlagDefinition def) {
        ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(claimID);
        if(claimFlags == null || !claimFlags.containsKey(def.getName().toLowerCase())) {
            return this.setFlag(claimID, def, false);
        } else {
            claimFlags.remove(def.getName().toLowerCase());
            return new SetFlagResult(true, def.getUnSetMessage());
        }
    }
    
    List<MessageSpecifier> load(String input) throws InvalidConfigurationException {
        this.flags.clear();
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.loadFromString(input);

        ArrayList<MessageSpecifier> errors = new ArrayList<>();
        Set<String> claimIDs = yaml.getKeys(false);
        for(String claimID : claimIDs) {
            Set<String> flagNames = yaml.getConfigurationSection(claimID).getKeys(false);
            for(String flagName : flagNames) {
                String paramsDefault = yaml.getString(claimID + "." + flagName);
                String params = yaml.getString(claimID + "." + flagName + ".params", paramsDefault);
                boolean set = yaml.getBoolean(claimID + "." + flagName + ".value", true);
                FlagDefinition def = this.getFlagDefinitionByName(flagName);
                if(def != null) {
                    SetFlagResult result = this.setFlag(claimID, def, set, params);
                    if(!result.success) {
                        errors.add(result.message);
                    }
                }
            }
        }
        
        return errors;
    }
    
    public void save() {
        try {
            this.save(FlagsDataStore.flagsFilePath);
        }
        catch(Exception e) {
            GPFlags.addLogEntry("Failed to save flag data.  Details:");
            e.printStackTrace();
        }
    }
    
    public String flagsToString() {
        YamlConfiguration yaml = new YamlConfiguration();
        
        Set<String> claimIDs = this.flags.keySet();
        for(String claimID : claimIDs) {
            ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(claimID);
            Set<String> flagNames = claimFlags.keySet();
            for(String flagName : flagNames) {
                Flag flag = claimFlags.get(flagName);
                String paramsPath = claimID + "." + flagName + ".params";
                yaml.set(paramsPath, flag.parameters);
                String valuePath = claimID + "." + flagName + ".value";
                yaml.set(valuePath, flag.getSet());
            }
        }
        
        return yaml.saveToString();
    }
    
    public void save(String filepath) throws IOException {
        String fileContent = this.flagsToString();
        File file = new File(filepath);
        file.getParentFile().mkdirs();
        file.createNewFile();
        Files.write(fileContent.getBytes("UTF-8"), file);
    }
    
    public List<MessageSpecifier> load(File file) throws IOException, InvalidConfigurationException {
        if(!file.exists()) return this.load("");
        
        List<String> lines = Files.readLines(file, Charset.forName("UTF-8"));
        StringBuilder builder = new StringBuilder();
        for(String line : lines) {
            builder.append(line).append('\n');
        }
        
        return this.load(builder.toString());
    }

    public void clear() {
        this.flags.clear();
    }

    void removeExceptClaimIDs(HashSet<String> validClaimIDs) {
        HashSet<String> toRemove = new HashSet<>();
        for(String key : this.flags.keySet()) {
            //if not a valid claim ID (maybe that claim was deleted)
            if(!validClaimIDs.contains(key)) {
                try {
                    int numericalValue = Integer.parseInt(key);
                    
                    //if not a special value like default claims ID, remove
                    if(numericalValue >= 0) toRemove.add(key);
                }
                catch(NumberFormatException ignore){ } //non-numbers represent server or world flags, so ignore those
            }
        }
        for(String key : toRemove) {
            this.flags.remove(key);
        }
    }


    @Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) throws IllegalArgumentException {
		Validate.notNull(sender, "Sender cannot be null");
		Validate.notNull(args, "Arguments cannot be null");
		Validate.notNull(alias, "Alias cannot be null");
		if (args.length == 0) {
			return ImmutableList.of();
		}

		StringBuilder builder = new StringBuilder();
		for (String arg : args) {
			builder.append(arg).append(" ");
		}

		String arg = builder.toString().trim();
		ArrayList<String> matches = new ArrayList<String>();
		String cmd = command.getName();
		for (String name : this.definitions.keySet()) {
			if (StringUtil.startsWithIgnoreCase(name, arg)) {
				if (cmd.equalsIgnoreCase("setclaimflag") || cmd.equalsIgnoreCase("setdefaultclaimflag")
						|| cmd.equalsIgnoreCase("unsetclaimflag") || cmd.equalsIgnoreCase("unsetdefaultclaimflag")) {
					if (GPFlags.getInstance().getFlagManager().getFlagDefinitionByName(name).getFlagType().contains(FlagDefinition.FlagType.CLAIM)) {
						if (sender.hasPermission("gpflags." + name))
							matches.add(name);
					}
				}
				if (cmd.equalsIgnoreCase("setworldflag") || cmd.equalsIgnoreCase("unsetworldflag")) {
					if (GPFlags.getInstance().getFlagManager().getFlagDefinitionByName(name).getFlagType().contains(FlagDefinition.FlagType.WORLD)) {
						if (sender.hasPermission("gpflags." + name))
							matches.add(name);
					}
				}
				if (cmd.equalsIgnoreCase("setserverflag") || cmd.equalsIgnoreCase("unsetserverflag")) {
					if (GPFlags.getInstance().getFlagManager().getFlagDefinitionByName(name).getFlagType().contains(FlagDefinition.FlagType.SERVER)) {
						if (sender.hasPermission("gpflags." + name))
							matches.add(name);
					}
				}

			}
		}

		if (sender instanceof Player) {
			WorldSettings settings = GPFlags.getInstance().getWorldSettingsManager().get(((Player) sender).getWorld());


			// TabCompleter for Biomes in ChangeBiome flag
			if (args[0].equalsIgnoreCase("ChangeBiome")) {
				if (args.length != 2) return null;
				if (!(command.getName().equalsIgnoreCase("setclaimflag"))) return null;
				ArrayList<String> biomes = new ArrayList<>();
				for (Biome biome : Biome.values()) {
					if (StringUtil.startsWithIgnoreCase(biome.toString(), args[1])) {
						if (!(settings.biomeBlackList.contains(biome.toString()))) {
							biomes.add(biome.toString());
						} else if (sender.hasPermission("gpflags.bypass")) {
							biomes.add(biome.toString());
						}
					}
				}
				biomes.sort(String.CASE_INSENSITIVE_ORDER);
				return biomes;
			}
		}
		if (args[0].equalsIgnoreCase("noOpenDoors")) {
			if (args.length != 2) return null;
			List<String> doortype = Arrays.asList("doors", "trapdoors", "gates");
			ArrayList<String> types = new ArrayList<>();
			for (String type : doortype) {
				if (StringUtil.startsWithIgnoreCase(type, args[1])) {
					types.add(type);
				}
			}
			types.sort(String.CASE_INSENSITIVE_ORDER);
			return types;
		}
		matches.sort(String.CASE_INSENSITIVE_ORDER);
		return matches;
	}

}
