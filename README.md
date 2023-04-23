# GPFlags
GPFlags is a plugin to allow players to set flags for GriefPrevention claims. Either a single claim or a global flag for all claims. It's similar to WorldGuard, but it's more performant, easier to use, and includes many more useful flags than WorldGuard does. It also doesn't require WorldEdit to run (unlike WorldGuard). Admins can also empower players to self-serve by giving them access to specific flags, which they can then only use on land claims they own.

## Dependencies
[GriefPrevention](https://www.spigotmc.org/resources/griefprevention.1884/)

## Flags
There are over 80 flags and I frequently add more. A few of the more popular flags are NoMonsterSpawns, EnterMessage, AllowPvP, and NoEnter. If there's a flag you don't see here, contact me through the support Discord and I'll see what I can do.
<details>
    <summary>Click to see all flags</summary>
    <span>
<table>
  <tbody>
    <tr>
      <th>Flag</th>
      <th>Description</th>
    </tr>
    <tr>
      <td>AllowBlockExplosions</td>
      <td>Similar to the GP <code>/claimexplosions</code> command but this one will persist through restarts.</td>
    </tr>
    <tr>
      <td>AllowPvP</td>
      <td>If in the config "PvP Only In PvP-Flagged Claims" is set to true, PvP will be off in the world, and you can then add a flag to a claim to allow PvP in specific claims. There is also an optional message to be sent to players when they enter these claims (can be changed in the config).</td>
    </tr>
    <tr>
      <td>AllowWitherDamage</td>
      <td>Allows withers to deal damage in the claim. Recommended for player usage.</td>
    </tr>
    <tr>
      <td>BuyAccessTrust</td>
      <td>Allows players to buy access trust in the claim. They can use /buyaccesstrust while standing in the claim to buy access trust. The price is determined when setting the flag.</td>
    </tr>
    <tr>
      <td>BuyContainerTrust</td>
      <td>Allows players to buy container trust in the claim. They can use /buycontainertrust while standing in the claim to buy container trust. The price is determined when setting the flag.</td>
    </tr>
    <tr>
      <td>BuyBuildTrust</td>
      <td>Allows players to buy build trust in the claim. They can use /buybuildtrust while standing in the claim to buy build trust. The price is determined when setting the flag.</td>
    </tr>
    <tr>
      <td>BuySubclaim</td>
      <td>Allows players to buy the subclaim. Buying the subclaim will give the buyer all levels of trust and will disable the flag to prevent other players from buying the subclaim after that. </td>
    </tr>
    <tr>
      <td>ChangeBiome</td>
      <td>You can now change the biome in claims. You can also give players permission to set flags, and they can change the biomes in their own claims.  When you are using the command to set a biome, it will give you a scrollable list of available biomes. Make sure to type them in exactly. I made the biomes that a player could use permission-based so you can stop players from using certain biomes. Give players the permission <code>gpflags.flag.changebiome.<biomename></code> for the biomes they are allowed to use. You need to use the BukkitAPI names for biomes, which can be found <a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/Biome.html">here</a>. When a claim is deleted or a ChangeBiome flag is unset, the biome will revert back to its previous state to make sure your world stays looking clean. This will prevent players from running around the world, claiming land, changing the biome, abandoning the claim, and making your world look patchy. Note that removing the flag converts the claim into the biome located just north of the claim which is only sometimes the original claim.</td>
    </tr>
    <tr>
      <td>CommandBlackList</td>
      <td>Blacklist commands. Prevents players from using any commands you list in the flagged area unless they have the <code>gpflags.bypass.commandblacklist permission</code>. <br>Example usage: <code>/SetClaimFlag CommandBlackList sethome;setwarp;kit</code>  </td>
    </tr>
    <tr>
      <td>CommandWhiteList </td>
      <td>Whitelist commands. The flag prevents players from using any commands except those you list in the flagged area unless they have the <code>gpflags.bypass.commandwhitelist</code> permission. <br>Example usage: <code>/SetClaimFlag CommandWhiteList tell;me;home</code></td>
    </tr>
    <tr>
      <td>EnterCommand </td>
      <td>Runs a console command when the player enters this area (Variables: %owner% = Owner of claim in flag, %name% = player in event, %uuid% = uuid of player in event) For multiple commands, separate with a semicolon (;) (make sure to not have spaces after the semicolon).</td>
    </tr>
    <tr>
      <td>EnterCommand-Owner</td>
      <td>Runs a console command when the owner of this claim enters the claim (Variables: %name% = player in event, %uuid% = uuid of player in event) For multiple commands, separate with a semicolon (;) (make sure to not have spaces after the semicolon)</td>
    </tr>
    <tr>
      <td>EnterCommand-Members</td>
      <td>Runs a console command when a member of this claim enters the claim (Variables: %name% = player in event, %uuid% = uuid of player in event) For multiple commands, separate with a semicolon (;) (make sure to not have spaces after the semicolon).</td>
    </tr>
    <tr>
      <td>EnterPlayerCommand</td>
      <td>The same as the EnterCommand flag, except this one will run commands from a player vs from console. (Variables: %owner% = Owner of claim in flag, %name% = player in event, %uuid% = uuid of player in event) For multiple commands, separate with a semicolon (;) (make sure to not have spaces after the semicolon).</td>
    </tr>
    <tr>
      <td>EnterMessage </td>
      <td>Sends a message to the player when they enter this area (Variables: %owner% = Owner of claim in flag, %name% = player in event).</td>
    </tr>
    <tr>
      <td>ExitCommand </td>
      <td>Runs a console command when a player exits this area (Variables: %owner% = Owner of claim in flag, %name% = player in event, %uuid% = uuid of player in event) For multiple commands, separate with a semicolon (;) (make sure to not have spaces after the semicolon).</td>
    </tr>
    <tr>
      <td>ExitCommand-Owner</td>
      <td>Runs a console command when the owner of this claim exits the claim (Variables: %name% = player in event, %uuid% = uuid of player in event) For multiple commands, separate with a semicolon (;) (make sure to not have spaces after the semicolon).</td>
    </tr>
    <tr>
      <td>ExitCommand-Members</td>
      <td>Runs a console command when a member of this claim exits the claim (Variables: %name% = player in event, %uuid% = uuid of player in event) For multiple commands, separate with a semicolon (;) (make sure to not have spaces after the semicolon).</td>
    </tr>
    <tr>
      <td>ExitPlayerCommand </td>
      <td>Same as ExitCommand except this will run the command from player vs from console (Variables: %owner% = Owner of claim in flag, %name% = player in event, %uuid% = uuid of player in event) For multiple commands, separate with a semicolon (;) (make sure to not have spaces after the semicolon).</td>
    </tr>
    <tr>
      <td>ExitMessage </td>
      <td>Sends a message to the player when they leave this area (Variables: %owner% = Owner of claim in flag, %name% = player in event).</td>
    </tr>
    <tr>
      <td>HealthRegen </td>
      <td>Health is regenerated when the player is in this area.</td>
    </tr>
    <tr>
      <td>InfiniteArrows </td>
      <td>Arrows shot into this area will be given back to the player after it makes contact.</td>
    </tr>
    <tr>
      <td>KeepInventory </td>
      <td>A player will keep their inventory if they die in this area.</td>
    </tr>
    <tr>
      <td>KeepLevel </td>
      <td>Prevents players' xp from dropping when they die in this area.</td>
    </tr>
    <tr>
      <td>KeepLoaded </td>
      <td>Will keep the claim loaded.</td>
    </tr>
    <tr>
      <td>NetherPortalConsoleCommand </td>
      <td>Executes console command when entering a Portal. Runs one or more console commands when a player steps through a nether portal in the flagged area. Use %name% or %uuid% placeholders to target the player stepping through the portal, and separate multiple command lines with semicolons (;). If your in-game command entry box is too short for all your commands, consider backing-up your flags.yml file and then modifying it with a text editor to get more command lines in for a single portal, then using /GPFReload to load your edited file. <br>Example usage: <code>/SetClaimFlag NetherPortalConsoleCommand tp %name% 0, 65, 0;xp 10L %name%</code></td>
    </tr>
    <tr>
      <td>NetherPortalPlayerCommand </td>
      <td>Executes player command when entering a Portal. Causes any players who walk into a nether portal in the area where the flag is applied to automatically run a command line instead of teleporting (it runs as the player, not as a console command). Helpful to give players a /home portal or random wilderness teleport portal, for example.</td>
    </tr>
    <tr>
      <td>NoAnvilDamage </td>
      <td>Prevents anvils from being damaged from being used. This flag is only supported on Paper and forks of Paper.</td>
    </tr>
    <tr>
      <td>NoBlockForm </td>
      <td>Prevents blocks from forming or spreading based on world conditions. <br>Examples: snow forming due to a snow storm, ice forming in a snowy biome like taiga or tundra, obsidian/cobblestone forming due to contact with water, and concrete forming due to mixing of concrete powder and water.</td>
    </tr>
    <tr>
      <td>NoBlockGravity </td>
      <td>Prevents blocks like sand and gravel from falling.</td>
    </tr>
    <tr>
      <td>NoChorusFruit </td>
      <td>Prevents players from teleporting when they eat chorus fruit in this area.</td>
    </tr>
    <tr>
      <td>NoCombatLoot </td>
      <td>Clears drops on entity death. When a mob (except for players) dies in an area with this flag active, no loot will drop. Using this, you can create combat challenges where players can keep their inventories and experience (with other flags above), but prevent players from abusing those flags to farm loot. Player death loot is controlled by the above KeepInventory flag.</td>
    </tr>
    <tr>
      <td>NoElytra</td>
      <td>Prevents players from using elytra.</td>
    </tr>
    <tr>
      <td>NoEnderPearl</td>
      <td>Prevents players from teleporting when they throw an ender pearl in this area.</td>
    </tr>
    <tr>
      <td>NoEnter</td>
      <td>Prevents all players from entering this area.</td>
    </tr>
    <tr>
      <td>NoEnterPlayer</td>
      <td>Blocks specific players from entering this area.</td>
    </tr>
    <tr>
      <td>NoExpiration</td>
      <td>Disables claim expiration.</td>
    </tr>
    <tr>
      <td>NoExplosionDamage</td>
      <td>Disables damage caused by explosions (ie: creepers & tnt).</td>
    </tr>
    <tr>
      <td>NoFallDamage</td>
      <td>Prevents players from taking fall damage in this area.</td>
    </tr>
    <tr>
      <td>NoFireDamage</td>
      <td>Prevent fire from damaging blocks in this area.Requires FireDamage in the GP config to be enabled!</td>
    </tr>
    <tr>
      <td>NoFireSpread</td>
      <td>Prevent fire from spreading in this area. Requires FireSpread in the GP config to be enabled!</td>
    </tr>
    <tr>
      <td>NoFlight</td>
      <td>Prevents players from flying in this area.</td>
    </tr>
    <tr>
      <td>NoFluidFlow</td>
      <td>Prevents fluid from flowing in this area.</td>
    </tr>
    <tr>
      <td>NoGrowth</td>
      <td>Stop plants from growing (crops) and blocks from spreading (podzol, grass, seagrass, kelp) in this area.</td>
    </tr>
    <tr>
      <td>NoHunger</td>
      <td>Prevents hunger loss for all players who enter this area.</td>
    </tr>
    <tr>
      <td>NoIceForm</td>
      <td>Stops ice from forming in this area.</td>
    </tr>
    <tr>
      <td>NoItemDamage</td>
      <td>Prevents players' items from taking durability hits.</td>
    </tr>
    <tr>
      <td>NoItemDrop</td>
      <td>Prevents players from dropping items in this area.</td>
    </tr>
    <tr>
      <td>NoItemPickup</td>
      <td>Prevents players from picking up items in this area.</td>
    </tr>
    <tr>
      <td>NoLeafDecay</td>
      <td>Prevents leaves from decaying in this area.</td>
    </tr>
    <tr>
      <td>NoLootProtection</td>
      <td>Disables loot protection on player death. Disables GriefPrevention's player death loot "anti-theft" feature in the flagged area, allowing any player to pick up the items a player drops when he or she dies in that area. Useful for competitive areas where loot can be a reward, like PvP arenas.</td>
    </tr>
    <tr>
      <td>NoMcMMODeathPenalty</td>
      <td>Disables McMMO death penalty - cancels McMMODeathPenalties when a player dies in a flagged area.</td>
    </tr>
    <tr>
      <td>NoMcMMOSkills</td>
      <td>Prevents mcMMO skill usage (activated skills, secondary skills, disarms, etc) in the flagged area. You might use this to create PvE challenge areas or specialized PvP arenas where mcMMO won't give some players an advantage over others.</td>
    </tr>
    <tr>
      <td>NoMcMMoXPGain</td>
      <td>Disables McMMO experience gaining in the claim.</td>
    </tr>
    <tr>
      <td>NoMobDamage</td>
      <td>Prevents mob damage in this area. This does not affect players, this will only prevent entities from damaging passive mobs. (ex: wolves cant hurt sheep)</td>
    </tr>
    <tr>
      <td>NoMobSpawns</td>
      <td>Prevents ALL mobs from spawning in this area, good or bad!</td>
    </tr>
    <tr>
      <td>NoMobSpawnsType</td>
      <td>Prevents specific types of mobs from spawning in this area. Can support multiple types. Must use Spigot EntityType enums. To add multiple types, separate with a semi-colon ; <br>Example usage: /setclaimflag NoMobSpawnsType creeper;cow;zombie;wandering_trader;phantom Permissions for this flag are per mob type, for example, <code>gpflags.flag.nomobspawnstype.cow.</code></td>
    </tr>
    <tr>
      <td>NoMonsters</td>
      <td>Prevents all bad mobs from spawning in this area, and will also prevent them from entering the area. If they do, they will be removed.</td>
    </tr>
    <tr>
      <td>NoMonsterSpawns</td>
      <td>Prevents all bad mobs from spawning in this area. If they spawn outside of the area, they will be able to walk in.</td>
    </tr>
    <tr>
      <td>NoOpenDoors</td>
      <td>Prevents players from opening doors/gates/trapdoors in a claim. This flag will follow GP's permissions, meaning an owner and a member with access trust or higher will be able to open doors. Supports parameters (doors, trapdoors, gates), to use multiple parameters, separate with a comma.</td>
    </tr>
    <tr>
      <td>NoPetDamage</td>
      <td>Prevents players from damaging pets in this area</td>
    </tr>
    <tr>
      <td>NoPlayerDamage</td>
      <td>Prevents players from taking any damage in this area</td>
    </tr>
    <tr>
      <td>NoPlayerDamageByMonster</td>
      <td>Prevents players from taking damage from monsters in this area</td>
    </tr>
    <tr>
      <td>NoSnowForm</td>
      <td>Prevents snow from forming in this area</td>
    </tr>
    <tr>
      <td>NoVehicle</td>
      <td>Stops players from placing and using vehicles (boats/minecarts) in claims</td>
    </tr>
    <tr>
      <td>NoVineGrowth</td>
      <td>Stops vines from growing in this area</td>
    </tr>
    <tr>
      <td>NoWeatherChange</td>
      <td>Disables weather change - prevents weather from changing in a world, even by operators using commands. If you change your mind about the weather in a world, you have to first disable the flag, then change the weather, then re-enable the flag. You should use this only with <code>/setserverflag</code> or <code>/setworldflag</code>, because it has no effect on individual land claims or subdivisions. Note: If you lock the weather during a thunderstorm, it will never end. If you lock the weather when there is no thunder, a storm will never come.</td>
    </tr>
    <tr>
      <td>NotifyEnter</td>
      <td>Sends the claim owner a message when a player enters the claim.</td>
    </tr>
    <tr>
      <td>NotifyExit</td>
      <td>Sends the claim owner a message when a player exits the claim.</td>
    </tr>
    <tr>
      <td>OwnerFly</td>
      <td>Allows an owner of a claim to fly within their own claim</td>
    </tr>
    <tr>
      <td>OwnerMemberFly</td>
      <td>Allows owners of the claim and members with access trust or higher to fly within the claim</td>
    </tr>
    <tr>
      <td>PlayerTime</td>
      <td>You can set the time in a claim for a player. When the player enters, their time will be set to one of the 4 options. When they leave the claim, it will reset to match world time. Usage: <code>/setclaimflag playertime <day/noon/night/midnight></code></td>
    </tr>
    <tr>
      <td>PlayerWeather</td>
      <td>You can set the weather a player will see when they enter a claim. This is client-side and will not affect the rest of the world. <code>/setclaimflag playerweather <sun/rain></code>. Thunderstorms are not supported.</td>
    </tr>
    <tr>
      <td>ProtectNamedMobs</td>
      <td>This will protect mobs that have a name (ie: using a name tag or via commands). Players with container trust will still be able to harm/kill said mob, but visitors to your claim will not be able to harm them.</td>
    </tr>
    <tr>
      <td>RaidMemberOnly</td>
      <td>Prevent non-members of claims from triggering raids. (This may be a temporary flag if GP decides to put something directly in GP)</td>
    </tr>
    <tr>
      <td>ReadLecterns</td>
      <td>Allows players to read but not manipulate, any lectern with a book on a claim. This flag has <a href="https://github.com/akdukaan/GPFlags/issues/35">bugs</a> that I do not plan to work on.</td>
    </tr>
    <tr>
      <td>RespawnLocation</td>
      <td>Sets spawn location for the claim (Useful for PvP arenas). Overrides the usual respawn rules to respawn the player in a specific location who dies in the flagged area. For example, consider respawning a player at the beginning of a parkour challenge or just outside a pvp arena. You may optionally specify pitch and yaw (facing direction) as well. <br>Example usages: <code>/SetFlag RespawnLocation world 112.5 68 265.5</code> or <code>/SetFlag RespawnLocation world 112.5 68 265.5 90 45</code></td>
    </tr>
    <tr>
      <td>SpawnReasonWhitelist</td>
      <td>Denies all mob spawns unless the spawn reason is whitelisted.</td>
    </tr>
    <tr>
      <td>SpleefArena</td>
      <td>Complex flag to create spleef arenas. SpleefArena - Completely automates a Spleef minigame (players compete to remove blocks out from under each other until someone falls) in the flagged area. <br>Example usage: <code>/SetFlag SpleefArena minecraft:snow_block minecraft:bricks 20</code>. <br>The above example will generate a snow block (minecraft:snow_block) 20 blocks above every bricks block (minecraft:bricks) in the flagged area every time a player dies in the flagged area. It will also allow ONLY snow blocks to be broken by any player even without build permission, and won't drop those blocks as items when they're broken. To set up a spleef arena, first flag the claim or subdivision as shown above. Then dig down underneath where the breakable arena surface (snow block in the above example) will be and use your marker blocks (bricks in the above example) to indicate the shape of your arena, which does NOT have to be flat, rectangular, or single-block thick. The y offset (the last flag parameter) dictates how far down you have to place the marker blocks from where you want the arena surface to generate. To test your settings, use the Vanilla /kill command while standing in the flagged area. Your death will trigger the arena surface to be built per your specifications.</td>
    </tr>
    <tr>
      <td>TrappedDestination</td>
      <td>Sets trapped destination for the claim (Useful for admin claims). Allows players to use GriefPrevention's /trapped command in administrative land claims by specifying where the player will go when he gets "unstuck". Ordinarily, administrative land claims don't allow players to use the command at all.</td>
    </tr>
    <tr>
      <td>ViewContainers</td>
      <td>Allows players to view but not manipulate, any container on a claim.</td>
    </tr>
  </tbody>
</table>
</span>
</details>


## Commands and Permissions
See the [wiki](https://github.com/akdukaan/GPFlags/wiki/Commands-and-Permissions) for all available commands and permissions.

## Dev Builds
Development builds can be found on [Jenkins](https://jenkins.akiradev.xyz/job/GPFlags/) and in the [support Discord](https://discord.com/invite/MBdsxAR).

## Usage Stats
<img src="https://bstats.org/signatures/bukkit/GPFlags.svg/">
See more stats <a href="https://bstats.org/plugin/bukkit/GPFlags/17786">here</a>.
