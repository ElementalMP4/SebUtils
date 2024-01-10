package main.java.elementalmp4.service;

import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.utils.ConsoleColours;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedHashMap;

public class DatabaseService {

    private static final LinkedHashMap<String, String> MIGRATIONS = new LinkedHashMap<>();

    static {
        MIGRATIONS.put("nicknames", "CREATE TABLE IF NOT EXISTS chat_customisation (username TEXT, nickname TEXT, colourName TEXT);");
        MIGRATIONS.put("homes", "CREATE TABLE IF NOT EXISTS user_homes (username TEXT, world TEXT, pos_x INTEGER, pos_y INTEGER, pos_z INTEGER);");
        MIGRATIONS.put("add home names", "ALTER TABLE user_homes ADD COLUMN IF NOT EXISTS home_name TEXT;");
        MIGRATIONS.put("set empty home names", "UPDATE user_homes SET home_name = 'default' WHERE home_name IS NULL");
        MIGRATIONS.put("global config", "CREATE TABLE IF NOT EXISTS global_config (config_item TEXT, config_value TEXT);");
    }

    private final Connection connection;

    public DatabaseService(String pluginFolderPath) {
        try {
            Class.forName("org.h2.Driver");
            this.connection = DriverManager.getConnection("jdbc:h2:file:" + pluginFolderPath + "/sebutils", "sa", "password");
            migrate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void migrate() {
        for (String migration : MIGRATIONS.keySet()) {
            try {
                connection.createStatement().executeUpdate(MIGRATIONS.get(migration));
                SebUtils.getPluginLogger().info(ConsoleColours.YELLOW + "Ran migration '" + migration + "'");
            } catch (Exception e) {
                SebUtils.getPluginLogger().severe(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

}