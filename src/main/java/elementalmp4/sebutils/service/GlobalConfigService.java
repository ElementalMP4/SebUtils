package main.java.elementalmp4.sebutils.service;

import main.java.elementalmp4.sebutils.SebUtils;
import main.java.elementalmp4.sebutils.config.Config;
import main.java.elementalmp4.sebutils.config.DataType;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.exception.InvalidConfigException;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class GlobalConfigService {

    private static final Set<GlobalConfig> DATABASE_PARAMETERS = Set.of(
            GlobalConfig.DATABASE_URI,
            GlobalConfig.DATABASE_USERNAME,
            GlobalConfig.DATABASE_PASSWORD
    );

    private static final Map<String, String> CACHE = new LinkedHashMap<>();
    private static final Path CONFIG_PATH = Paths.get(SebUtils.getPlugin().getDataFolder().getAbsolutePath() + "/config.json");

    private static void createDefaultMap() {
        for (GlobalConfig globalConfig : GlobalConfig.values()) {
            CACHE.put(globalConfig.getKey(), globalConfig.getDefaultValue());
        }
    }

    public static void initialiseGlobalConfig() {
        try {
            createDefaultMap();
            if (Files.exists(CONFIG_PATH)) {
                String content = new String(Files.readAllBytes(CONFIG_PATH));
                JSONObject json = new JSONObject(content);
                for (String key : json.keySet()) {
                    CACHE.put(key, json.getString(key));
                }
            }
            saveConfig();
            checkDatabaseParameters();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkDatabaseParameters() {
        for (GlobalConfig conf : DATABASE_PARAMETERS) {
            String value = getValue(conf);
            if (value.equals("unset")) {
                throw new InvalidConfigException(conf.getKey() + " needs to be set!");
            }
        }
    }

    public static String getValue(GlobalConfig config) {
        return CACHE.get(config.getKey());
    }

    public static Config getAsConfig(GlobalConfig conf) {
        String value;
        if (conf.getType() == DataType.SECURE_STRING) {
            value = "REDACTED";
        } else {
            value = getValue(conf);
        }
        return new Config(conf, value);
    }

    public static Map<String, Config> listConfig() {
        Map<String, Config> result = new LinkedHashMap<>();
        for (GlobalConfig conf : GlobalConfig.values()) {
            result.put(conf.getKey(), getAsConfig(conf));
        }
        return result;
    }

    public static boolean getAsBoolean(GlobalConfig config) {
        return Boolean.parseBoolean(getValue(config));
    }

    public static int getAsInteger(GlobalConfig config) {
        return Integer.parseInt(getValue(config));
    }

    public static void set(GlobalConfig config, String value) {
        setValue(config.getKey(), value);
    }

    private static void setValue(String config, String value) {
        CACHE.put(config, value);
        saveConfig();
    }

    private static void saveConfig() {
        try {
            JSONObject json = new JSONObject();
            Path pathToExport = Paths.get(SebUtils.getPlugin().getDataFolder().getAbsolutePath() + "/config.json");
            for (String key : CACHE.keySet()) {
                String value = CACHE.get(key);
                json.put(key, value);
            }
            Files.writeString(pathToExport, json.toString(3));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}