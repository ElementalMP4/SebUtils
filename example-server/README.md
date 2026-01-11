# Example Paper Server using Docker

If you don't want to do complex configuration of Java & Postgres, you can use this example Docker Compose stack to deploy a server. This stack can be extended to add other containers that might be used by your other plugins, but by default it only has a container to run the game server and a container to run the postgres database. The files from btoh containers are kept on disk.

## Deploying the example stack

1. Save the docker-compose.yml file where you want to launch your server
2. Run `docker compose up` to start the server and create the initial files
3. Once the server has booted, press `ctrl+c` to stop it
4. Now install your plugins (SebUtils!), datapacks etc.
5. Add your config files for the plugins (see an example below for SebUtils)
6. Restart the stack in detached mode by running `docker compose up -d`

## Example Sebutils Config

The config file is saved in `plugins/SebUtils/config.json` - If you haven't installed SebUtils yet, you can manually create the SebUtils folder and config file. The contents should look like this:

```jsonc
{
   "discord_enabled": "false",
   "discord_channel": "unset",
   "discord_token": "unset",
   
   "admin_plot_override": "true",
   "afk_enabled": "true",
   "tnt_enabled": "true",
   "cows_explode": "false",
   "pvp_toggle_enabled": "true",
   "graves_enabled": "true",
   "plot_max_size": "10000",
   "sheep_smite": "false",

   "ollama_model": "nemotron-3-nano:30b",
   "ollama_enabled": "true",
   "ollama_host": "http://192.168.1.90:11434",

   "web_enabled": "true",
   "web_port": "8080",
   "web_bind": "0.0.0.0",
   "map_enabled": "true",

   "db_username": "sebutils",
   "db_uri": "jdbc:postgresql://sebutils-postgres/sebutils",
   "db_password": "password"
}
```

