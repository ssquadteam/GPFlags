# GriefPreventionFlags
GriefPrevention Flags is a plugin to allow admins to set flags for GriefPrevention claims. Either a single claim, or a global flag for all claims. It's similar to WorldGuard, but it's more performant, easier to use, and includes many more useful flags than WorldGuard does.  It also doesn't require WorldEdit to run (unlike WorldGuard).  Admins can also empower players to self-serve by giving them access to specific flags, which they can then only use on land claims they own.

This is a fork from the original author [BigScary](https://github.com/BigScary/GriefPreventionFlags) and the previous maintainer [ShaneBeee](https://github.com/ShaneBeee/GriefPreventionFlags).

### Download
This fork will only support Spigot/Paper versions 1.13+
Download the latest release from [the releases page](https://github.com/lewysDavies/GriefPreventionFlags/releases).

### Why this fork?
#### New Features
- Added the `ViewContainers` claim flag. This allows players to view but not manipulate, any container on a claim.
- Added the `ReadLecterns` claim flag. This allows players to read but not manipulate, any lectern with a book on a claim.
- Overhauled and fixed the `NoFlight` flag.
#### Bug Fixes
- Fixed `AllowPvP` not working after a server restart.
- Fixed `FlagDef_AllowPvP` projectile duplication glitches.
- Fixed a hardcoded prefix being appended to some messages.
#### Continued Support
Have a feature suggestion or need to report a bug/issue? See the [Issues](https://github.com/lewysDavies/GriefPreventionFlags/issues) page.
This fork will continue to support the latest Spigot/Paper versions along with any new features.
