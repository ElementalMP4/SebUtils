package main.java.elementalmp4;

public enum GlobalConfig {

    TNT_EXPLODES("tnt_enabled", "true"),
    COWS_EXPLODE("cows_explode", "false"),
    SHEEP_SMITE("sheep_smite", "false"),
    BILLY_ENABLED("billy_enabled", "false"),
    ADMIN_PLOT_OVERRIDE("admin_plot_override", "true"),
    PLOT_MAX_SIZE("plot_max_size", "10000"),
    AFK_ENABLED("afk_enabled", "true"),
    GRAVES_ENABLED("graves_enabled", "true");

    private final String key;
    private final String defaultValue;

    GlobalConfig(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return this.key;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

}
