package main.java.elementalmp4.sebutils.modules;

import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

public class DatabaseModule extends AbstractModule {

    private static final LinkedHashMap<String, String> MIGRATIONS = new LinkedHashMap<>();

    static {
        MIGRATIONS.put("nicknames", "CREATE TABLE IF NOT EXISTS chat_customisation (username TEXT, nickname TEXT, colourName TEXT);");
        MIGRATIONS.put("homes", "CREATE TABLE IF NOT EXISTS user_homes (username TEXT, world TEXT, pos_x INTEGER, pos_y INTEGER, pos_z INTEGER, home_name TEXT);");
        MIGRATIONS.put("block locker", "CREATE TABLE IF NOT EXISTS block_locker (plot_id BIGSERIAL PRIMARY KEY, owner TEXT, world TEXT, bound_x_a INTEGER, bound_y_a INTEGER, bound_x_b INTEGER, bound_y_b INTEGER);");
        MIGRATIONS.put("plot sharing", "CREATE TABLE IF NOT EXISTS plot_permissions (plot_id BIGINT, player TEXT, FOREIGN KEY (plot_id) REFERENCES block_locker(plot_id) ON DELETE CASCADE);");
        MIGRATIONS.put("graves", "CREATE TABLE IF NOT EXISTS graves (grave_id TEXT, grave_owner TEXT, pos_x INTEGER, pos_y INTEGER, pos_z INTEGER, world TEXT);");
        MIGRATIONS.put("pvp toggles", "CREATE TABLE IF NOT EXISTS pvp_toggles (username TEXT, toggle BOOLEAN);");
    }

    private Connection connection;

    @Override
    public void onStart() {
        try {
            getPluginLogger().info("Connecting to database...");
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                    GlobalConfigService.getValue(GlobalConfig.DATABASE_URI),
                    GlobalConfigService.getValue(GlobalConfig.DATABASE_USERNAME),
                    GlobalConfigService.getValue(GlobalConfig.DATABASE_PASSWORD)
            );
            migrate();
            getPluginLogger().info("Database ready!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void migrate() {
        for (String migration : MIGRATIONS.keySet()) {
            try {
                connection.createStatement().executeUpdate(MIGRATIONS.get(migration));
            } catch (Exception e) {
                getPluginLogger().severe("Failed to run migration " + migration + " - " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void onStop() {
        getPluginLogger().info("Stopping database module");
        try {
            if (connection == null) return;
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}