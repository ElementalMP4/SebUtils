package main.java.elementalmp4.service;

import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.GlobalConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalConfigService {

    static {
        for (GlobalConfig config : GlobalConfig.values()) {
            try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT config_value FROM global_config WHERE config_item = '%s';"
                        .formatted(config.getKey()));
                if (rs.isClosed()) {
                    stmt.executeUpdate("INSERT INTO global_config VALUES ('%s', '%s');".formatted(config.getKey(), config.getDefaultValue()));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String getValue(GlobalConfig config) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT config_value FROM global_config WHERE config_item = '%s';"
                    .formatted(config.getKey()));
            if (rs.next()) {
                return rs.getString("config_value");
            } else {
                return config.getDefaultValue();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> getAllConfig() {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            Map<String, String> config = new LinkedHashMap<>();
            ResultSet rs = stmt.executeQuery("SELECT * FROM global_config;");
            while (rs.next()) {
                config.put(rs.getString("config_item"), rs.getString("config_value"));
            }
            return config;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean getAsBoolean(GlobalConfig config) {
        return Boolean.parseBoolean(getValue(config));
    }

    public static void set(GlobalConfig config, String value) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE global_config SET config_value = '%s' WHERE config_item = '%s'".formatted(value, config.getKey()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
