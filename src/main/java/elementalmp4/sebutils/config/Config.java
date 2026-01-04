package main.java.elementalmp4.sebutils.config;

public class Config {

    private final String displayName;
    private final String defaultValue;
    private final DataType type;
    private final String value;
    private final String group;

    public Config(GlobalConfig globalConfig, String value) {
        this.displayName = globalConfig.getDisplayName();
        this.defaultValue = globalConfig.getDefaultValue();
        this.type = globalConfig.getType();
        this.value = value;
        this.group = globalConfig.getGroup();
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public DataType getType() {
        return type;
    }

    public String getGroup() {
        return group;
    }
}
