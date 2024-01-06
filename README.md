# PaperUtils

Lightweight commands to make a vanilla paper server a bit more friendly, without the need for enormous and complicated plugins and APIs

# Installing

Either download this repo and compile it yourself using maven (requires Java 17) - or download the latest release from the [releases tab](https://github.com/ElementalMP4/PaperUtils/releases/)

# Commands

All permissions are granted by default, but can be customised if required. Using a plugin such as LuckPerms is recommended for this.

## Homes

**Permission** `sebutils.homes`

| Command  | Aliases | Description                         |
|----------|-----|-----------------------------------------|
| /home    | /h  | Takes you to a home location            |
| /sethome | /sh | Sets a home to your current location    |
| /delhome | /dh | Deletes a home                          |
| /homes | none | Lists your home locations                |

## Teleportation

**Permission** `sebutils.tpa`

| Command   | Aliases | Description                           |
|-----------|---------|---------------------------------------|
| /tpa      | none    | Ask to teleport to another player     |
| /tpahere  | none    | Ask to teleport another player to you |
| /tpaccept | none    | Accept a teleport request             |
| /tpdeny   | none    | Deny a teleport request               |

## Chat Customisation

**Permission** `sebutils.nicknames`

| Command     | Aliases | Description              |
|-------------|---------|--------------------------|
| /namecolour | /nc     | Set your nickname colour |
| /nickname   | /nick   | Set your chat nickname   |