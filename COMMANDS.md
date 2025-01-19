# Commands

| Command           | Usage                                          | Permission         | Description                                                                                                               |
|-------------------|------------------------------------------------|--------------------|---------------------------------------------------------------------------------------------------------------------------|
| pvptoggle         | /pvptoggle [true/false]                        | sebutils.pvp       | Toggle whether or not you can attack and be attacked by other players                                                     |
| listgraves        | /listgraves                                    | sebutils.graves    | Show your uncollected graves                                                                                              |
| tpgrave           | /tpgrave                                       | sebutils.graves    | Teleport to a grave                                                                                                       |
| sethome           | /sethome [optional name]                       | sebutils.homes     | Sets your home to your current location. If no name is specified, the home will be 'default'                              |
| home              | /home [optional name]                          | sebutils.homes     | Takes you to your home. If no name is specified, the home will be 'default'                                               |
| homes             | /homes                                         | sebutils.homes     | List all your homes                                                                                                       |
| delhome           | /delhome [optional name]                       | sebutils.homes     | Deletes a home. If no name is specified, the home will be 'default'                                                       |
| tpa               | /tpa [player]                                  | sebutils.tpa       | Ask to teleport to another player                                                                                         |
| tpahere           | /tpahere [player]                              | sebutils.tpa       | Ask to teleport another player to you                                                                                     |
| tpaccept          | /tpaccept                                      | sebutils.tpa       | Let another player teleport to you                                                                                        |
| tpdeny            | /tpdeny                                        | sebutils.tpa       | Deny a teleport request                                                                                                   |
| namecolour        | /namecolour [colour]                           | sebutils.nicknames | Set your name colour                                                                                                      |
| nickname          | /nickname [name]                               | sebutils.nicknames | Set a nickname                                                                                                            |
| allowtnt          | /allowtnt [true/false]                         | sebutils.admin     | Enable or disable TNT explosions                                                                                          |
| ollama            | /ollama [enble/disable/host]                   | sebutils.admin     | Configure the Ollama integration                                                                                          |
| discord           | /discord [config/enable/disable/token/channel] | sebutils.admin     | Configure the Discord integration                                                                                         |
| slack             | /slack [config/enable/disable/webhook]         | sebutils.admin     | Configure the Slack integration                                                                                           |
| enablepvptoggle   | /enablepvptoggle [true/false]                  | sebutils.admin     | Configure individual player PVP toggles. If enabled, players will be able to choose whether or not they have PVP enabled. |
| plotsize          | /plotsize [size]                               | sebutils.admin     | Adjust the maximum plot area players are allowed                                                                          |
| listhomes         | /listhomes [player]                            | sebutils.admin     | Shows a user's homes                                                                                                      |
| cowsexplode       | /cowsexplode [true/false]                      | sebutils.admin     | Toggle whether cows explode when milked                                                                                   |
| sheepsmite        | /sheepsmite [true/false]                       | sebutils.admin     | Toggle whether sheep are smited when shaved                                                                               |
| enablebilly       | /enablebilly [true/false]                      | sebutils.admin     | Toggle whether nitwit villagers will show a special item shop for difficult to acquire items                              |
| adminplotoverride | /adminplotoverride [true/false]                | sebutils.admin     | Toggle whether players with the sebutils.admin permission can override user plots                                         |
| smite             | /smite [player]                                | sebutils.admin     | Smite a player                                                                                                            |
| enableafk         | /enableafk [true/false]                        | sebutils.admin     | Enable or disable automatic AFK messages                                                                                  |
| enablegraves      | /enablegraves [true/false]                     | sebutils.admin     | Enable or disable player graves                                                                                           |
| config            | /config                                        | sebutils.admin     | Shows the plugin configuration                                                                                            |
| plots             | /plots                                         | sebutils.plots     | Shows all your plot locations                                                                                             |
| deleteplot        | /deleteplot [plot ID]                          | sebutils.plots     | Delete a plot by its plot ID                                                                                              |
| plot              | /plot                                          | sebutils.plots     | See if the block you are currently standing on is in a plot                                                               |
| grantpermit       | /grantpermit [player] [plot ID]                | sebutils.plots     | Allow another player to access your plot                                                                                  |
| revokepermit      | /revokepermit [player] [plot ID]               | sebutils.plots     | Revokes a plot permit from a player on your plot                                                                          |
| permits           | /permits [plot ID]                             | sebutils.plots     | Views the permits for a plot                                                                                              |
| ask               | /ask [question]                                | sebutils.ai        | Ask Llama a question                                                                                                      |

# Permissions

| Permission         | Description                                                          |
|--------------------|----------------------------------------------------------------------|
| sebutils.tpa       | Allows players to use the tpa, tpahere, tpaccept and tpdeny commands |
| sebutils.homes     | Allows players to use the home, sethome and delhome commands         |
| sebutils.nicknames | Allows players to use the nickname and namecolour commands           |
| sebutils.admin     | Allows players access to admin commands                              |
| sebutils.plots     | Allows players to create and delete plots                            |
| sebutils.graves    | Allows players to use grave commands                                 |
| sebutils.pvp       | Allows players to use pvp commands                                   |
| sebutils.ai        | Allows players to use AI commands                                    |
