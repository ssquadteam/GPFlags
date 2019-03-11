package me.ryanhamshire.GPFlags;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FlagManager implements TabCompleter {
    private ConcurrentHashMap<String, FlagDefinition> definitions;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Flag>> flags;
    
    static final String DEFAULT_FLAG_ID = "-2";
    
    public FlagManager() {
        this.definitions = new ConcurrentHashMap<String, FlagDefinition>();
        this.flags = new ConcurrentHashMap<String, ConcurrentHashMap<String, Flag>>();
    }
    
    public void RegisterFlagDefinition(FlagDefinition def) {
        String name = def.getName();
        this.definitions.put(name.toLowerCase(),  def);
    }
    
    public FlagDefinition GetFlagDefinitionByName(String name)
    {
        return this.definitions.get(name.toLowerCase());
    }
    
    public Collection<FlagDefinition> GetFlagDefinitions() {
        Collection<FlagDefinition> definitions = new ArrayList<FlagDefinition>(this.definitions.values());
        return definitions;
    }
    
    public SetFlagResult SetFlag(String id, FlagDefinition def, boolean isActive, String... args) {
        String parameters = "";
        for(String arg : args) {
            parameters += arg + " ";
        }
        parameters = parameters.trim();
        
        SetFlagResult result;
        if(isActive) {
            result = def.ValidateParameters(parameters);
            if(!result.success) return result;
        }
        else {
            result = new SetFlagResult(true, def.GetUnSetMessage());
        }
        
        Flag flag = new Flag(def, parameters);
        flag.setSet(isActive);
        ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(id);
        if(claimFlags == null) {
            claimFlags = new ConcurrentHashMap<String, Flag>();
            this.flags.put(id, claimFlags);
        }
        
        String key = def.getName().toLowerCase();
        if(!claimFlags.containsKey(key) && isActive) {
            def.incrementInstances();
        }
        claimFlags.put(key, flag);
        return result;
    }

    public Flag GetFlag(String id, FlagDefinition flagDef)
    {
        return this.GetFlag(id,  flagDef.getName());
    }
    
    public Flag GetFlag(String id, String flag) {
        ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(id);
        if(claimFlags == null) return null;
        Flag claimFlag = claimFlags.get(flag.toLowerCase());
        return claimFlag;
    }

    public Collection<Flag> GetFlags(String id) {
        ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(id);
        if(claimFlags == null)
        {
            return new ArrayList<Flag>();
        }
        else
        {
            return new ArrayList<Flag>(claimFlags.values());
        }
    }

    public SetFlagResult UnSetFlag(String id, FlagDefinition def) {
        ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(id);
        if(claimFlags == null || !claimFlags.containsKey(def.getName().toLowerCase()))
        {
            return this.SetFlag(id, def, false);
        }
        else
        {
            claimFlags.remove(def.getName().toLowerCase());
            return new SetFlagResult(true, def.GetUnSetMessage());
        }
    }
    
    List<MessageSpecifier> Load(String input) throws InvalidConfigurationException {
        this.flags.clear();
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.loadFromString(input);

        ArrayList<MessageSpecifier> errors = new ArrayList<MessageSpecifier>();
        Set<String> claimIDs = yaml.getKeys(false);
        for(String claimID : claimIDs) {
            Set<String> flagNames = yaml.getConfigurationSection(claimID).getKeys(false);
            for(String flagName : flagNames) {
                String paramsDefault = yaml.getString(claimID + "." + flagName);
                String params = yaml.getString(claimID + "." + flagName + ".params", paramsDefault);
                boolean set = yaml.getBoolean(claimID + "." + flagName + ".value", true);
                FlagDefinition def = this.GetFlagDefinitionByName(flagName);
                if(def != null) {
                    SetFlagResult result = this.SetFlag(claimID, def, set, params);
                    if(!result.success) {
                        errors.add(result.message);
                    }
                }
            }
        }
        
        return errors;
    }
    
    public void Save() {
        try
        {
            this.Save(FlagsDataStore.flagsFilePath);
        }
        catch(Exception e)
        {
            GPFlags.AddLogEntry("Failed to save flag data.  Details:");
            e.printStackTrace();
        }
    }
    
    public String FlagsToString() {
        YamlConfiguration yaml = new YamlConfiguration();
        
        Set<String> claimIDs = this.flags.keySet();
        for(String claimID : claimIDs)
        {
            String claimPath = claimID.toString();
            ConcurrentHashMap<String, Flag> claimFlags = this.flags.get(claimID);
            Set<String> flagNames = claimFlags.keySet();
            for(String flagName : flagNames)
            {
                Flag flag = claimFlags.get(flagName);
                String paramsPath = claimPath + "." + flagName + ".params";
                yaml.set(paramsPath, flag.parameters);
                String valuePath = claimPath + "." + flagName + ".value";
                yaml.set(valuePath, flag.getSet());
            }
        }
        
        return yaml.saveToString();
    }
    
    public void Save(String filepath) throws UnsupportedEncodingException, IOException {
        String fileContent = this.FlagsToString();
        File file = new File(filepath);
        file.getParentFile().mkdirs();
        file.createNewFile();
        Files.write(fileContent.getBytes("UTF-8"), file);
    }
    
    public List<MessageSpecifier> Load(File file) throws IOException, InvalidConfigurationException {
        if(!file.exists()) return this.Load("");
        
        List<String> lines = Files.readLines(file, Charset.forName("UTF-8"));
        StringBuilder builder = new StringBuilder();
        for(String line : lines)
        {
            builder.append(line).append('\n');
        }
        
        return this.Load(builder.toString());
    }

    public void clear() {
        this.flags.clear();
    }

    void removeExceptClaimIDs(HashSet<String> validClaimIDs) {
        HashSet<String> toRemove = new HashSet<String>();
        for(String key : this.flags.keySet())
        {
            //if not a valid claim ID (maybe that claim was deleted)
            if(!validClaimIDs.contains(key))
            {
                try
                {
                    int numericalValue = Integer.parseInt(key);
                    
                    //if not a special value like default claims ID, remove
                    if(numericalValue >= 0) toRemove.add(key);
                }
                catch(NumberFormatException e){ } //non-numbers represent server or world flags, so ignore those
            }
        }
        
        for(String key : toRemove)
        {
            this.flags.remove(key);
        }
        
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) throws IllegalArgumentException
    {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");
        if (args.length == 0)
        {
                return ImmutableList.of();
        }
        
        StringBuilder builder = new StringBuilder();
        for(String arg : args)
        {
            builder.append(arg + " ");
        }
        
        String arg = builder.toString().trim();
        ArrayList<String> matches = new ArrayList<String>();
        for (String name : this.definitions.keySet())
        {
            if (StringUtil.startsWithIgnoreCase(name, arg))
            {
                matches.add(name);
            }
        }

        WorldSettings settings = GPFlags.instance.worldSettingsManager.Get(((Player) sender).getWorld());


        if (args[0].equalsIgnoreCase("ChangeBiome")) {
            if (args.length != 2) return null;
            if (!(command.getName().equalsIgnoreCase("setclaimflag"))) return null;
            ArrayList<String> biomes = new ArrayList<String>();
            for (Biome biome : Biome.values()) {
                if (StringUtil.startsWithIgnoreCase(biome.toString(), args[1])) {
                    if (!(settings.biomeBlackList.contains(biome.toString()))) {
                        biomes.add(biome.toString());
                    }

                }
            }
            Collections.sort(biomes, String.CASE_INSENSITIVE_ORDER);
            return biomes;
        }
        Collections.sort(matches, String.CASE_INSENSITIVE_ORDER);
        return matches;
    }

}
