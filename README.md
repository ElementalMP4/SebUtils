# PaperUtils

Lightweight commands to make a vanilla paper server a bit more friendly, without the need for enormous and complicated plugins and APIs and permissions and all that shebang. The bare essentials, and what I usually find myself requiring when I want to start a server for friends.

## What it do

- AFK messages (After 10 minutes of inactivity)
- Homes (multiple per user)
- Teleportation
- Chat nicknames and colours
- Simple permissions

# Installing

Either download this repo and compile it yourself - or download the latest release from the [releases tab](https://github.com/ElementalMP4/PaperUtils/releases/)

## Compiling with Maven

You'll need:
- Java 17
- Maven

Then:
1. Clone this repository 
```
git clone https://github.com/ElementalMP4/PaperUtils
```
2. Build with Maven
```
mvn clean package
```
3. Move the SebUtils jar to your server's plugins directory
```
mv target/SebUtils.*.jar /path/to/server/plugins/
```

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