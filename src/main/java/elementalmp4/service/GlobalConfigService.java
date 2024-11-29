package main.java.elementalmp4.service;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.SebUtils;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalConfigService {

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getValue(GlobalConfig config) {
        return CACHE.get(config.getKey());
    }

    public static Map<String, String> getAllConfig() {
        return CACHE;
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
            e.printStackTrace();
        }
    }

}
