![SebUtils](logo.png "SebUtils Logo")

Lightweight commands to make a vanilla paper server a bit more friendly, without the need for enormous and complicated
plugins and APIs and permissions and all that shebang. The bare essentials, and what I usually find myself requiring
when I want to start a server for friends.

## What it do

- AFK messages (After 10 minutes of inactivity)
- Homes (multiple per user)
- Teleportation
- Chat nicknames and colours
- Disabling TNT
- Simple permissions
- Cows can explode when milked
- Sheep can be smited when sheared
- Players can be smited by admins
- Nitwit Villagers can sell expensive items
- Graves to save your items when you die
- Plots to protect your builds from griefing
- Discord & Slack integrations
- Ollama integration

It's super easy to configure, and your configuration can easily be exported and shared between your servers.

# Installing

Either download this repo and compile it yourself - or download the latest release from
the [releases tab](https://github.com/ElementalMP4/SebUtils/releases/)

## Compiling with Maven

You'll need:

- Java 21
- Maven

Then:

1. Clone this repository

```
git clone https://github.com/ElementalMP4/SebUtils
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

All permissions are granted by default, but can be customised if required. Using a plugin such as LuckPerms is
recommended for this.

For a list of all the commands, please read the [commands documentation](COMMANDS.md)