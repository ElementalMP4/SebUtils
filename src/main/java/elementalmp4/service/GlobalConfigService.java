package main.java.elementalmp4.service;

import main.java.elementalmp4.SebUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GlobalConfigService {

    public static String getOrDefault(String key, String def) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT config_value FROM global_config WHERE config_item = '%s';"
                    .formatted(key));
            if (rs.next()) {
                return rs.getString("config_value");
            } else {
                return def;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void set(String key, String value) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM global_config WHERE config_item = '%s';".formatted(key));
            stmt.executeUpdate("INSERT INTO global_config VALUES ('%s', '%s')".formatted(key, value));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
