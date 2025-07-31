package main.java.elementalmp4;

public enum GlobalConfig {

    TNT_EXPLODES("Explosions enabled", "tnt_enabled", "true", true),
    COWS_EXPLODE("Cows explode when milked", "cows_explode", "false", true),
    SHEEP_SMITE("Sheep smite when sheared", "sheep_smite", "false", true),
    BILLY_ENABLED("Billy shop enabled", "billy_enabled", "false", true),
    ADMIN_PLOT_OVERRIDE("Admins override plot permissions", "admin_plot_override", "true", true),
    PLOT_MAX_SIZE("Maximum plot area per user", "plot_max_size", "10000", true),
    AFK_ENABLED("Auto AFK enabled", "afk_enabled", "true", true),
    GRAVES_ENABLED("Graves enabled", "graves_enabled", "true", true),
    DISCORD_TOKEN("Discord token", "discord_token", "Not set", false),
    DISCORD_CHANNEL("Discord Channel", "discord_channel", "Not set", false),
    DISCORD_ENABLED("Discord Integration Enabled", "discord_enabled", "false", true),
    PVP_TOGGLE_ENABLED("Individual Player PVP Toggle Enabled", "pvp_toggle_enabled", "true", true),
    SLACK_INTEGRATION_ENABLED("Slack integration enabled", "slack_enabled", "false", true),
    SLACK_WEBHOOK("Slack integration webhook", "slack_webhook", "Not set", false),
    OLLAMA_HOST("Ollama Host", "ollama_host", "http://localhost:11434", true),
    OLLAMA_ENABLED("Ollama Enabled", "ollama_enabled", "false", true),
    OLLAMA_MODEL("Ollama Model", "ollama_model", "gemma3:12b", true);

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
