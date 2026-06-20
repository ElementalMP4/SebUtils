![SebUtils](logo.png "SebUtils Logo")

Lightweight commands to make a vanilla paper server a bit more friendly, without the need for enormous and complicated
plugins and APIs and permissions and all that shebang. The bare essentials, and what I usually find myself requiring
when I want to start a server for friends.

# Requirements

- Paper
- PostgreSQL

# What it does

- AFK messages (After 10 minutes of inactivity)
- Homes (multiple per user)
- Teleportation Requests
- Graves
- Chat nicknames and colours
- Browser-based Map
- Discord integrations
- Ollama integration
- Web Dashboard

It's super easy to configure, and your configuration can easily be exported and shared between your servers.

# Installing 

There's some ongoing work to improve the features and reliability of SebUtils, so the installation docs will be updated once this work is finished. Thank you for your patience!

# Commands

For a list of all the commands, please read the [commands documentation](COMMANDS.md)

# Permissions

The permissions are fairly straightforward and simple, and should work for you out-of-the-box. However, should you need to change anything, a list of permissions can be viewed at the bottom of the [commands documentation](COMMANDS.md) - a permissions editor such as LuckPerms is recommended for editing permissions.

# Discord Integration

The Discord integration serves two purposes - forward chat messages between Discord & Minecraft, and allow or deny access to the server (via the whitelist) via a prompt in a Discord channel.

## Discord Configuration

### Bots & Tokens

The Discord integration requires you to provide your own bot. You can create one for free by following Discord's guide [here](https://docs.discord.com/developers/quick-start/getting-started)

You will need to save the token, as SebUtils uses this to log in as your bot. You can either put the token into the config file, or you can upload it via the Web Dashboard.

### Toggles

There are two toggles in the config: One toggles the entire Discord integration on and off, and the other only toggles the access control. Please note, if the main toggle is off, the access control will be turned off as well.

The main toggle will automatically enable chat forwarding between the server and Discord.

### Channels

There are two channels that need to be configured: the chat channel and the approvals channel. It is recommended to make the approvals channel private. 

You need to copy just the channel ID into the config - not the channel name.

### Roles

One role is required, as this prevents unauthorised users from allowing or denying access to other players. You need to create a role, assign it to people who will be able to approve players, and then copy the role ID into the config.

# Contributing

All contributions are welcome, please feel free to start a discussion on the issue tab, or open a pull request! 