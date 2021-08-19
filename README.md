# GriefPreventionFlags
GriefPrevention Flags is a plugin to allow admins to set flags for GriefPrevention claims. Either a single claim, or a global flag for all claims. It's similar to WorldGuard, but it's more performant, easier to use, and includes many more useful flags than WorldGuard does.  It also doesn't require WorldEdit to run (unlike WorldGuard).  Admins can also empower players to self-serve by giving them access to specific flags, which they can then only use on land claims they own.

This is a fork from the original author [BigScary](https://github.com/BigScary/GriefPreventionFlags) and the previous maintainers [ShaneBeee](https://github.com/ShaneBeee/GriefPreventionFlags) and [lewysDavies](https://github.com/lewysDavies/GriefPreventionFlags).

### Download
This fork will only support Spigot/Paper versions 1.13+.
Download the latest release from the [Birdflop Discord](https://discord.gg/MBdsxAR) or compile it yourself from here.


### What does this fork change from the [Spigot release](https://www.spigotmc.org/resources/gpflags.55773/)?
- Added the `ViewContainers` claimflag which allows players to view, but not manipulate, any container on a claim.
- Added the `ReadLecterns` claimflag which allows players to read but not manipulate any lectern with a book on a claim.
- Fixed the `ChangeBiome` claimflag.
- Fixed the `NoFlight` claimflag.
- Fixed a null pointer exception with global instances of the `NoVehicle` claim flag.
- Fixed a null pointer exception with the `ReadLecterns` claim flag in admin claims.
- Fixed a bug where `NoMobDamage` would also prevent players from renaming mobs.
- Fixed a bug where entering/exiting a claim from beyond world build height would allow the player to bypass flags checked on claim entry/exit.
- Fixed a bug where `NoIceForm` would not prevent frostwalker's frosted ice from forming.
- Fixed a bug where `NoVineGrowth` would allow the growth of glow litchen, weeping vines, and twisting vines.
- Fixed a bug where `AllowPvP` does not apply after a server restart.
- Fixed a bug where `AllowPvP` could duplicate arrows.
- Removed the ability for players to set messages with the NoEnter claimflag. My reasoning can be found in a pinned message in the [Birdflop Discord](https://discord.gg/MBdsxAR).
