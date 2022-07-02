package me.ryanhamshire.GPFlags.util;

import me.ryanhamshire.GPFlags.*;
import me.ryanhamshire.GPFlags.flags.FlagDefinition;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;

@SuppressWarnings("WeakerAccess")
public class Util {

    /**
     * Check if server is running a minimum Minecraft version
     *
     * @param major Major version to check (Most likely just going to be 1)
     * @param minor Minor version to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor) {
        return isRunningMinecraft(major, minor, 0);
    }

    /**
     * Check if server is running a minimum Minecraft version
     *
     * @param major    Major version to check (Most likely just going to be 1)
     * @param minor    Minor version to check
     * @param revision Revision to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor, int revision) {
        String[] version = getMinecraftVersion().split("\\.");
        int maj = Integer.parseInt(version[0]);
        int min = Integer.parseInt(version[1]);
        int rev;
        try {
            rev = Integer.parseInt(version[2]);
        } catch (Exception ignore) {
            rev = 0;
        }
        return maj > major || min > minor || (min == minor && rev >= revision);
    }

    /**
     * Get the Minecraft version the server is running
     *
     * @return Minecraft version the server is running
     */
    public static String getMinecraftVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    /**
     * Get the prefix stored in messages.yml
     *
     * @return prefix stored in messages.yml
     */
    private static String getPrefix() {
        return getColString(GPFlags.getInstance().getFlagsDataStore().getMessage(Messages.Prefix));
    }

    /**
     * Disable the flight mode of a player whom cant fly
     * <p>This is mainly used when a player deletes their claim.</p>
     *
     * @param player Player to disable flight for
     */
    public static void disableFlight(Player player) {
        if (player.isFlying() && !canFly(player)) {
            Location loc = player.getLocation();
            Block block = loc.getBlock();
            while (block.getY() > 2 && !block.getType().isSolid() && block.getType() != Material.WATER) {
                block = block.getRelative(BlockFace.DOWN);
            }
            player.setAllowFlight(false);
            if (loc.getY() - block.getY() >= 4) {
                GPFlags.getInstance().getPlayerListener().addFallingPlayer(player);
            }
            Util.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
        }
        if (player.getAllowFlight() && !canFly(player)) {
            player.setAllowFlight(false);
            Util.sendMessage(player, TextMode.Warn, Messages.ExitFlightDisabled);
        }
    }

    private static boolean canFly(Player player) {
        GameMode mode = player.getGameMode();
        return mode == GameMode.SPECTATOR || mode == GameMode.CREATIVE || player.hasPermission("gpflags.bypass.fly");
    }

    /**
     * Get the ItemStack form of a vehicle
     * <p>Specifically a boat or minecart</p>
     *
     * @param vehicle Vehicle to get item from
     * @return ItemStack that matches vehicle
     */
    public static ItemStack getItemFromVehicle(Vehicle vehicle) {
        Material material = null;
        if (vehicle instanceof Boat) {
            switch (((Boat) vehicle).getWoodType()) {
                case BIRCH:
                    material = Material.BIRCH_BOAT;
                    break;
                case ACACIA:
                    material = Material.ACACIA_BOAT;
                    break;
                case JUNGLE:
                    material = Material.JUNGLE_BOAT;
                    break;
                case REDWOOD:
                    material = Material.SPRUCE_BOAT;
                    break;
                case DARK_OAK:
                    material = Material.DARK_OAK_BOAT;
                    break;
                default:
                    material = Material.OAK_BOAT;
            }
        } else if (vehicle instanceof RideableMinecart) {
            material = Material.MINECART;
        } else if (vehicle instanceof StorageMinecart) {
            material = Material.CHEST_MINECART;
        } else if (vehicle instanceof CommandMinecart) {
            material = Material.COMMAND_BLOCK_MINECART;
        } else if (vehicle instanceof ExplosiveMinecart) {
            material = Material.TNT_MINECART;
        } else if (vehicle instanceof HopperMinecart) {
            material = Material.HOPPER_MINECART;
        } else if (vehicle instanceof PoweredMinecart) {
            material = Material.FURNACE_MINECART;
        }
        if (material != null) {
            return new ItemStack(material);
        }
        return null;
    }

    /**
     * Check if an ItemStack is a vehicle
     * <p>Specifically a boat or minecart</p>
     *
     * @param itemStack ItemStack to check
     * @return True if item is a vehicle
     */
    public static boolean isAVehicle(ItemStack itemStack) {
        switch (itemStack.getType()) {
            case MINECART:
            case CHEST_MINECART:
            case COMMAND_BLOCK_MINECART:
            case FURNACE_MINECART:
            case HOPPER_MINECART:
            case TNT_MINECART:
            case BIRCH_BOAT:
            case ACACIA_BOAT:
            case DARK_OAK_BOAT:
            case JUNGLE_BOAT:
            case OAK_BOAT:
            case SPRUCE_BOAT:
                return true;
        }
        return false;
    }

    public static boolean isMonster(Entity entity) {
        EntityType type = entity.getType();
        return (entity instanceof Monster || type == EntityType.GHAST || type == EntityType.MAGMA_CUBE || type == EntityType.SHULKER
                || type == EntityType.PHANTOM || type == EntityType.SLIME);
    }

    /**
     * Shortcut for adding color to a string
     *
     * @param string String including color codes
     * @return Formatted string
     */
    public static String getColString(String string) {
        if (isRunningMinecraft(1, 16)) {
            string = string.replace(COLOR_CHAR, '&');

            Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
            Matcher matcher = hexPattern.matcher(string);
            while (matcher.find()) {
                final String before = string.substring(0, matcher.start());
                final String after = string.substring(matcher.end());
                ChatColor hexColor = ChatColor.of(matcher.group().substring(1));
                string = before + hexColor + after;
                matcher = hexPattern.matcher(string);
            }

            Pattern hexPattern2 = Pattern.compile("<#([A-Fa-f0-9]){6}>");
            matcher = hexPattern2.matcher(string);
            while (matcher.find()) {
                ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
                final String before = string.substring(0, matcher.start());
                final String after = string.substring(matcher.end());
                string = before + hexColor + after;
                matcher = hexPattern2.matcher(string);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Send a {@link MessageSpecifier} to a player, or console if player is null
     *
     * @param player    Player to send message to, or null if to console
     * @param color     Color of message
     * @param specifier Message specifier to send
     */
    public static void sendMessage(@Nullable CommandSender player, org.bukkit.ChatColor color, MessageSpecifier specifier) {
        sendMessage(player, color, specifier.getMessageID(), specifier.getMessageParams());
    }

    /**
     * Send a {@link Messages Message} to a player, or console if player is null
     *
     * @param player    Player to send message to, or null if to console
     * @param color     Color of message
     * @param messageID Message to send
     * @param args      Message parameters
     */
    public static void sendMessage(@Nullable CommandSender player, org.bukkit.ChatColor color, Messages messageID, String... args) {
        String message = GPFlags.getInstance().getFlagsDataStore().getMessage(messageID, args);
        sendMessage(player != null ? player : Bukkit.getConsoleSender(), color + message);
    }

    public static void sendMessage(@Nullable CommandSender player, org.bukkit.ChatColor color, String message) {
        sendMessage(player, color + message);
    }

    public static void sendMessage(@Nullable CommandSender receiver, String format, Object... objects) {
        sendMessage(receiver, String.format(format, objects));
    }

    public static void sendMessage(@Nullable CommandSender receiver, String message) {
        if (receiver != null) {
            receiver.sendMessage(getColString(getPrefix() + message));
        } else {
            log(message);
        }
    }

    public static void sendClaimMessage(@Nullable CommandSender player, org.bukkit.ChatColor color, MessageSpecifier specifier) {
        sendClaimMessage(player, color, specifier.getMessageID(), specifier.getMessageParams());
    }

    public static void sendClaimMessage(@Nullable CommandSender player, org.bukkit.ChatColor color, Messages messageID, String... args) {
        String message = GPFlags.getInstance().getFlagsDataStore().getMessage(messageID, args);
        sendClaimMessage(player, color, message);
    }

    public static void sendClaimMessage(@Nullable CommandSender player, org.bukkit.ChatColor color, String message) {
        sendClaimMessage(player, color + message);
    }

    public static void sendClaimMessage(@Nullable CommandSender receiver, String format, Object... args) {
        sendClaimMessage(receiver, String.format(format, args));
    }

    public static void sendClaimMessage(@Nullable CommandSender receiver, String message) {
        if (receiver != null) {
            receiver.sendMessage(Util.getColString(message));
        } else {
            log(Util.getColString(message));
        }
    }

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(getColString(getPrefix() + message));
    }

    public static void log(String format, Object... objects) {
        log(String.format(format, objects));
    }

    public static void logFlagCommands(String log) {
        if (GPFlagsConfig.LOG_ENTER_EXIT_COMMANDS) {
            Util.log(log);
        }
    }

    public static boolean canBuild(Claim claim, Player player) {
        try {
            return claim.checkPermission(player, ClaimPermission.Edit, null) == null;
        } catch (NoSuchFieldError e) {
            return claim.allowEdit(player) == null;
        }
    }

    public static boolean canInventory(Claim claim, Player player) {
        try {
            return claim.checkPermission(player, ClaimPermission.Inventory, null) == null;
        } catch (NoSuchFieldError e) {
            return claim.allowContainers(player) == null;
        }
    }

    public static boolean canManage(Claim claim, Player player) {
        try {
            return claim.checkPermission(player, ClaimPermission.Manage, null) == null;
        } catch (NoSuchFieldError e) {
            return claim.allowGrantPermission(player) == null;
        }
    }

    public static boolean canAccess(Claim claim, Player player) {
        try {
            return claim.checkPermission(player, ClaimPermission.Access, null) == null;
        } catch (NoSuchMethodError e) {
            return claim.allowAccess(player) == null;
        }
    }

    public static MessageSpecifier getFlagDefsMessage(Permissible player) {
        StringBuilder flagDefsList = new StringBuilder();
        Collection<FlagDefinition> defs = GPFlags.getInstance().getFlagManager().getFlagDefinitions();
        flagDefsList.append("&b");
        for (FlagDefinition def : defs) {
            if (player.hasPermission("gpflags.flag." + def.getName())) {
                flagDefsList.append(def.getName()).append("&7,&b ");
            }
        }
        String def = flagDefsList.toString();
        if (def.length() > 5) {
            def = def.substring(0, def.length() - 4);
        }
        return new MessageSpecifier(Messages.InvalidFlagDefName, def);
    }

    public static List<String> flagTab(CommandSender sender, String arg) {
        List<String> flags = new ArrayList<>();
        GPFlags.getInstance().getFlagManager().getFlagDefinitions().forEach(flagDefinition -> {
            if (sender.hasPermission("gpflags.flag." + flagDefinition.getName())) {
                flags.add(flagDefinition.getName());
            }
        });
        return StringUtil.copyPartialMatches(arg, flags, new ArrayList<>());
    }

    public static List<String> paramTab(CommandSender sender, String[] args) {
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "noenterplayer":
            case "commandblacklist":
            case "commandwhitelist":
            case "entercommand":
            case "entercommand_members":
            case "entercommand_owner":
            case "exitcommand":
            case "exitcommand_members":
            case "exitcommand_owner":
            case "entermessage":
            case "exitmessage":
                List<String> params = new ArrayList<>();
                if (!(sender instanceof Player)) return null;
                Player p = (Player) sender;
                FlagDefinition flagD = (GPFlags.getInstance().getFlagManager().getFlagDefinitionByName("noenterplayer"));
                Flag flag = flagD.getFlagInstanceAtLocation(p.getLocation(), p);
                if (flag == null) return null;
                String flagParams = flag.parameters;
                if (flagParams != null) {
                    params.add(flagParams);
                }
                return StringUtil.copyPartialMatches(args[1], params, new ArrayList<>());

            case "nomobspawnstype":
                List<String> entityTypes = new ArrayList<>();
                for (EntityType entityType : EntityType.values()) {
                    String type = entityType.toString();
                    if (sender.hasPermission("gpflags.flag.nomobspawnstype." + type)) {
                        String arg = args[1];
                        if (arg.contains(";")) {
                            if (arg.charAt(arg.length() - 1) != ';') {
                                arg = arg.substring(0, arg.lastIndexOf(';') + 1);
                            }
                            entityTypes.add(arg + type);
                        } else {
                            entityTypes.add(type);
                        }
                    }
                }
                return StringUtil.copyPartialMatches(args[1], entityTypes, new ArrayList<>());

            case "changebiome":
                ArrayList<String> biomes = new ArrayList<>();
                for (Biome biome : Biome.values()) {
                    if (sender.hasPermission("gpflags.flag.changebiome." + biome)) {
                        biomes.add(biome.toString());
                    }
                }
                biomes.sort(String.CASE_INSENSITIVE_ORDER);
                return StringUtil.copyPartialMatches(args[1], biomes, new ArrayList<>());

            case "noopendoors":
                if (args.length != 2) return null;
                List<String> doorType = Arrays.asList("doors", "trapdoors", "gates");
                return StringUtil.copyPartialMatches(args[1], doorType, new ArrayList<>());
        }
        return Collections.emptyList();
    }

    public static Location getInBoundsLocation(Player p) {
        Location loc = p.getLocation();
        World world = loc.getWorld();
        if (loc.getBlockY() >= world.getMaxHeight()) {
            loc.setY(world.getMaxHeight() - 1);
        }
        return loc;
    }

    public static boolean isClaimOwner(Claim c, Player p) {
        if (c == null) return false;
        if (c.getOwnerID() == null) return false;
        return c.getOwnerID().equals(p.getUniqueId());
    }

    public static boolean shouldBypass(Player p, Claim c, String basePerm) {
        if (p.hasPermission(basePerm)) return true;
        if (c == null) return p.hasPermission(basePerm + ".nonclaim");
        if (c.getOwnerID() == null && p.hasPermission(basePerm + ".adminclaim")) return true;
        if (isClaimOwner(c, p) && p.hasPermission(basePerm + ".ownclaim")) return true;
        if (isManageTrusted(p, c) && p.hasPermission(basePerm + ".manage")) return true;
        if (isBuildTrusted(p, c) && p.hasPermission(basePerm + ".edit")) return true;
        if (isContainerTrusted(p, c) && p.hasPermission(basePerm + ".inventory")) return true;
        if (isAccessTrusted(p, c) && p.hasPermission(basePerm + ".access")) return true;
        return false;
    }

    public static boolean shouldBypass(Player p, Claim c, Flag f) {
        String basePerm = "gpflags.bypass." + f.getFlagDefinition().getName();
        return shouldBypass(p, c, basePerm);
    }

    public static boolean isManageTrusted(Player p, @NotNull Claim c) {
        return Util.canManage(c, p);
    }

    public static boolean isBuildTrusted(Player p, @NotNull Claim c) {
        return Util.canBuild(c, p);
    }

    public static boolean isContainerTrusted(Player p, @NotNull Claim c) {
        return Util.canInventory(c, p);
    }

    public static boolean isAccessTrusted(Player p, @NotNull Claim c) {
        return Util.canAccess(c, p);
    }


}
