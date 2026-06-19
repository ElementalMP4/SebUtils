package main.java.elementalmp4.sebutils.config;

import main.java.elementalmp4.sebutils.modules.*;

import static main.java.elementalmp4.sebutils.service.GlobalConfigService.UNSET_VALUE;

public enum GlobalConfig {
    // Misc
    TNT_EXPLODES("Explosions Cause Damage", "tnt_enabled", "true", DataType.TOGGLE, "Misc", null),
    COWS_EXPLODE("Cows Explode When Milked", "cows_explode", "false", DataType.TOGGLE, "Misc", null),
    SHEEP_SMITE("Sheep Smite When Sheared", "sheep_smite", "false", DataType.TOGGLE, "Misc", null),
    ADMIN_PLOT_OVERRIDE("Admins Override Plot Permissions", "admin_plot_override", "true", DataType.TOGGLE, "Misc", null),
    PLOT_MAX_SIZE("Maximum Plot Area Per User", "plot_max_size", "10000", DataType.NUMBER, "Misc", null),
    AFK_ENABLED("Auto AFK", "afk_enabled", "true", DataType.TOGGLE, "Misc", null),
    GRAVES_ENABLED("Graves", "graves_enabled", "true", DataType.TOGGLE, "Misc", null),
    PVP_TOGGLE_ENABLED("Per-Player PVP Toggle", "pvp_toggle_enabled", "true", DataType.TOGGLE, "Misc", null),

    // Discord
    DISCORD_TOKEN("Discord Token", "discord_token", UNSET_VALUE, DataType.SECURE_STRING, "Discord", DiscordModule.class),
    DISCORD_CHANNEL("Discord Channel", "discord_channel", UNSET_VALUE, DataType.STRING, "Discord", DiscordModule.class),
    DISCORD_ENABLED("Discord Integration Enabled", "discord_enabled", "false", DataType.TOGGLE, "Discord", DiscordModule.class),

    // Ollama
    OLLAMA_HOST("Ollama Host", "ollama_host", "http://localhost:11434", DataType.STRING, "Ollama", OllamaModule.class),
    OLLAMA_ENABLED("Ollama Enabled", "ollama_enabled", "false", DataType.TOGGLE, "Ollama", OllamaModule.class),
    OLLAMA_MODEL("Ollama Model", "ollama_model", "gemma3:12b", DataType.STRING, "Ollama", OllamaModule.class),

    // Database
    DATABASE_URI("Database URI", "db_uri", UNSET_VALUE, DataType.STRING, "Database", DatabaseModule.class),
    DATABASE_USERNAME("Database Username", "db_username", UNSET_VALUE, DataType.STRING, "Database", DatabaseModule.class),
    DATABASE_PASSWORD("Database Password", "db_password", UNSET_VALUE, DataType.SECURE_STRING, "Database", DatabaseModule.class),

    // Web Server
    WEB_BIND("Web Server Bind", "web_bind", "127.0.0.1", DataType.STRING, "Web", WebServerModule.class),
    WEB_PORT("Web Server Port", "web_port", "8080", DataType.NUMBER, "Web", WebServerModule.class),
    WEB_ENABLED("Web Server Enabled", "web_enabled", "true", DataType.TOGGLE, "Web", WebServerModule.class),
    MAP_ENABLED("Map Enabled", "map_enabled", "true", DataType.TOGGLE, "Web", MapModule.class);

    private final String key;
    private final String defaultValue;
    private final String displayName;
    private final DataType type;
    private final String group;
    private final Class<? extends AbstractModule> configures;

    GlobalConfig(String displayName, String key, String defaultValue, DataType type, String group, Class<? extends AbstractModule> configures) {
        this.displayName = displayName;
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
        this.group = group;
        this.configures = configures;
    }

    public String getKey() {
        return this.key;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public DataType getType() {
        return this.type;
    }

    public String getGroup() {
        return this.group;
    }

    public Class<? extends AbstractModule> getConfiguredModule() {
        return this.configures;
    }

    public static GlobalConfig getByKey(String name) {
        for (GlobalConfig gc : values()) {
            if (gc.getKey().equals(name)) {
                return gc;
            }
        }
        return null;
    }
}
