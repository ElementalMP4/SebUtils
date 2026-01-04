package main.java.elementalmp4.sebutils.config;

public enum GlobalConfig {
    // Misc
    TNT_EXPLODES("Explosions Cause Damage", "tnt_enabled", "true", DataType.TOGGLE, "Misc"),
    COWS_EXPLODE("Cows Explode When Milked", "cows_explode", "false", DataType.TOGGLE, "Misc"),
    SHEEP_SMITE("Sheep Smite When Sheared", "sheep_smite", "false", DataType.TOGGLE, "Misc"),
    ADMIN_PLOT_OVERRIDE("Admins Override Plot Permissions", "admin_plot_override", "true", DataType.TOGGLE, "Misc"),
    PLOT_MAX_SIZE("Maximum Plot Area Per User", "plot_max_size", "10000", DataType.NUMBER, "Misc"),
    AFK_ENABLED("Auto AFK", "afk_enabled", "true", DataType.TOGGLE, "Misc"),
    GRAVES_ENABLED("Graves", "graves_enabled", "true", DataType.TOGGLE, "Misc"),
    PVP_TOGGLE_ENABLED("Per-Player PVP Toggle", "pvp_toggle_enabled", "true", DataType.TOGGLE, "Misc"),

    // Discord
    DISCORD_TOKEN("Discord Token", "discord_token", "unset", DataType.SECURE_STRING, "Discord"),
    DISCORD_CHANNEL("Discord Channel", "discord_channel", "unset", DataType.STRING, "Discord"),
    DISCORD_ENABLED("Discord Integration Enabled", "discord_enabled", "false", DataType.TOGGLE, "Discord"),

    // Ollama
    OLLAMA_HOST("Ollama Host", "ollama_host", "http://localhost:11434", DataType.STRING, "Ollama"),
    OLLAMA_ENABLED("Ollama Enabled", "ollama_enabled", "false", DataType.TOGGLE, "Ollama"),
    OLLAMA_MODEL("Ollama Model", "ollama_model", "gemma3:12b", DataType.STRING, "Ollama"),

    // Database
    DATABASE_URI("Database URI", "db_uri", "unset", DataType.STRING, "Database"),
    DATABASE_USERNAME("Database Username", "db_username", "unset", DataType.STRING, "Database"),
    DATABASE_PASSWORD("Database Password", "db_password", "unset", DataType.SECURE_STRING, "Database"),

    // Web Server
    WEB_BIND("Web Server Bind", "web_bind", "127.0.0.1", DataType.STRING, "Web"),
    WEB_PORT("Web Server Port", "web_port", "8080", DataType.NUMBER, "Web"),
    WEB_ENABLED("Web Server Enabled", "web_enabled", "true", DataType.TOGGLE, "Web");

    private final String key;
    private final String defaultValue;
    private final String displayName;
    private final DataType type;
    private final String group;

    GlobalConfig(String displayName, String key, String defaultValue, DataType type, String group) {
        this.displayName = displayName;
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
        this.group = group;
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
}
