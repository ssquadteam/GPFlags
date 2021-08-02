# GriefPreventionFlags
GriefPrevention Flags is a plugin to allow admins to set flags for GriefPrevention claims. Either a single claim, or a global flag for all claims. It's similar to WorldGuard, but it's more performant, easier to use, and includes many more useful flags than WorldGuard does.  It also doesn't require WorldEdit to run (unlike WorldGuard).  Admins can also empower players to self-serve by giving them access to specific flags, which they can then only use on land claims they own.

This is a fork from the original author [BigScary](https://github.com/BigScary/GriefPreventionFlags) and the previous maintainers [ShaneBeee](https://github.com/ShaneBeee/GriefPreventionFlags) and [lewysDavies](https://github.com/lewysDavies/GriefPreventionFlags).

### Download
This fork will only support Spigot/Paper versions 1.13+.
Download the latest release from the [Birdflop Discord](https://discord.gg/MBdsxAR) or compile it yourself from here.


### What does this fork change?
- Added compatibility with GriefPrevention the 16.17.2 snapshots while removing compatibility for 16.17.1 and below. This means you MUST use a [GriefPrevention development build](https://ci.appveyor.com/project/RoboMWM39862/griefprevention/history) with this fork. 
- Fixed a null pointer exception with global instances of the `NoVehicle` claim flag.
- Fixed a null pointer exception with the `ReadLecterns` claim flag in admin claims.
- Fixed a bug where the NoMobDamage claimflag would also prevent players from renaming mobs.
- Removed the ability for players to set messages with the NoEnter claimflag. 
- And it includes all changes from [lewysDavies' fork](https://github.com/lewysDavies/GriefPreventionFlags).
