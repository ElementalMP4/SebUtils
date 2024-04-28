package main.java.elementalmp4;

public enum GlobalConfig {

    TNT_EXPLODES("Explosions enabled", "tnt_enabled", "true"),
    COWS_EXPLODE("Cows explode when milked", "cows_explode", "false"),
    SHEEP_SMITE("Sheep smite when sheared", "sheep_smite", "false"),
    BILLY_ENABLED("Billy shop enabled", "billy_enabled", "false"),
    ADMIN_PLOT_OVERRIDE("Admins override plot permissions", "admin_plot_override", "true"),
    PLOT_MAX_SIZE("Maximum plot area per user", "plot_max_size", "10000"),
    AFK_ENABLED( "Auto AFK enabled", "afk_enabled", "true"),
    GRAVES_ENABLED("Graves enabled", "graves_enabled", "true");

    private final String key;
    private final String defaultValue;
    private final String displayName;

    GlobalConfig(String displayName, String key, String defaultValue) {
        this.displayName = displayName;
        this.key = key;
        this.defaultValue = defaultValue;
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

}
