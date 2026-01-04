package main.java.elementalmp4.sebutils;

public enum GlobalConfig {
    // Misc
    TNT_EXPLODES("Explosions enabled", "tnt_enabled", "true", true),
    COWS_EXPLODE("Cows explode when milked", "cows_explode", "false", true),
    SHEEP_SMITE("Sheep smite when sheared", "sheep_smite", "false", true),
    ADMIN_PLOT_OVERRIDE("Admins override plot permissions", "admin_plot_override", "true", true),
    PLOT_MAX_SIZE("Maximum plot area per user", "plot_max_size", "10000", true),
    AFK_ENABLED("Auto AFK enabled", "afk_enabled", "true", true),
    GRAVES_ENABLED("Graves enabled", "graves_enabled", "true", true),
    PVP_TOGGLE_ENABLED("Individual Player PVP Toggle Enabled", "pvp_toggle_enabled", "true", true),

    // Discord
    DISCORD_TOKEN("Discord token", "discord_token", "unset", false),
    DISCORD_CHANNEL("Discord Channel", "discord_channel", "unset", false),
    DISCORD_ENABLED("Discord Integration Enabled", "discord_enabled", "false", true),

    // Ollama
    OLLAMA_HOST("Ollama Host", "ollama_host", "http://localhost:11434", true),
    OLLAMA_ENABLED("Ollama Enabled", "ollama_enabled", "false", true),
    OLLAMA_MODEL("Ollama Model", "ollama_model", "gemma3:12b", true),

    // Database
    DATABASE_URI("Database URI", "db_uri", "unset", false),
    DATABASE_USERNAME("Database Username", "db_username", "unset", false),
    DATABASE_PASSWORD("Database Password", "db_password", "unset", false),

    // Web Server
    WEB_BIND("Web Server Bind", "web_bind", "127.0.0.1", false),
    WEB_PORT("Web Server Port", "web_port", "8080", false),;

    private final String key;
    private final String defaultValue;
    private final String displayName;
    private final boolean visible;

    GlobalConfig(String displayName, String key, String defaultValue, boolean visible) {
        this.displayName = displayName;
        this.key = key;
        this.defaultValue = defaultValue;
        this.visible = visible;
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

    public boolean isVisible() {
        return this.visible;
    }

}
