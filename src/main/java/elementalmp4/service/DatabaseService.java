package main.java.elementalmp4.service;

import main.java.elementalmp4.SebUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedHashMap;

public class DatabaseService {

    private final Connection connection;

    private static final LinkedHashMap<String, String> MIGRATIONS = new LinkedHashMap<>();

    static {
        MIGRATIONS.put("nicknames", "CREATE TABLE IF NOT EXISTS chat_customisation (username TEXT, nickname TEXT, colourName TEXT);");
        MIGRATIONS.put("homes", "CREATE TABLE IF NOT EXISTS user_homes (username TEXT, world TEXT, pos_x INTEGER, pos_y INTEGER, pos_z INTEGER);");
    }

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
                SebUtils.getPluginLogger().info("Running migration " + migration);
                connection.createStatement().executeUpdate(MIGRATIONS.get(migration));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}