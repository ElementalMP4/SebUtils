package main.java.elementalmp4.service;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.SebUtils;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalConfigService {

    private static final Map<String, String> CACHE = new LinkedHashMap<>();

    public static void initialiseGlobalConfig() {
        for (GlobalConfig config : GlobalConfig.values()) {
            try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT config_value FROM global_config WHERE config_item = '%s';"
                        .formatted(config.getKey()));
                if (!rs.next()) {
                    stmt.executeUpdate("INSERT INTO global_config VALUES ('%s', '%s');".formatted(config.getKey(), config.getDefaultValue()));
                    CACHE.put(config.getKey(), config.getDefaultValue());
                } else {
                    CACHE.put(config.getKey(), rs.getString("config_value"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE global_config SET config_value = '%s' WHERE config_item = '%s'".formatted(value, config));
            CACHE.put(config, value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean importConfig() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(SebUtils.getPlugin().getDataFolder().getAbsolutePath() + "/config.json")));
            JSONObject json = new JSONObject(content);
            for (String key : json.keySet()) {
                setValue(key, json.getString(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean exportConfig() {
        try {
            JSONObject json = new JSONObject();
            Path pathToExport = Paths.get(SebUtils.getPlugin().getDataFolder().getAbsolutePath() + "/config.json");
            for (String key : CACHE.keySet()) {
                String value = CACHE.get(key);
                json.put(key, value);
            }
            Files.writeString(pathToExport, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
