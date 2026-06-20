package main.java.elementalmp4.sebutils.modules;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

public class DatabaseModule extends AbstractModule {

    private static final LinkedHashMap<String, String> MIGRATIONS = new LinkedHashMap<>();

    static {
        MIGRATIONS.put("nicknames",
                "CREATE TABLE IF NOT EXISTS chat_customisation (username TEXT, nickname TEXT, colourName TEXT);");
        MIGRATIONS.put("homes",
                "CREATE TABLE IF NOT EXISTS user_homes (username TEXT, world TEXT, pos_x INTEGER, pos_y INTEGER, pos_z INTEGER, home_name TEXT);");
        MIGRATIONS.put("block locker",
                "CREATE TABLE IF NOT EXISTS block_locker (plot_id BIGSERIAL PRIMARY KEY, owner TEXT, world TEXT, bound_x_a INTEGER, bound_y_a INTEGER, bound_x_b INTEGER, bound_y_b INTEGER);");
        MIGRATIONS.put("plot sharing",
                "CREATE TABLE IF NOT EXISTS plot_permissions (plot_id BIGINT, player TEXT, FOREIGN KEY (plot_id) REFERENCES block_locker(plot_id) ON DELETE CASCADE);");
        MIGRATIONS.put("graves",
                "CREATE TABLE IF NOT EXISTS graves (grave_id TEXT, grave_owner TEXT, pos_x INTEGER, pos_y INTEGER, pos_z INTEGER, world TEXT);");
        MIGRATIONS.put("pvp toggles",
                "CREATE TABLE IF NOT EXISTS pvp_toggles (username TEXT, toggle BOOLEAN);");
        MIGRATIONS.put("web auth",
                "CREATE TABLE IF NOT EXISTS web_auth_tokens (username TEXT PRIMARY KEY, token UUID NOT NULL UNIQUE, issued_at TIMESTAMPTZ NOT NULL DEFAULT NOW());");
        MIGRATIONS.put("pending access list",
                "CREATE TABLE IF NOT EXISTS pending_access (uuid UUID PRIMARY KEY, username TEXT NOT NULL);");
    }

    private HikariDataSource dataSource;

    @Override
    public void onStart() {
        try {
            getPluginLogger().info("Starting database connection pool...");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(GlobalConfigService.getValue(GlobalConfig.DATABASE_URI));
            config.setUsername(GlobalConfigService.getValue(GlobalConfig.DATABASE_USERNAME));
            config.setPassword(GlobalConfigService.getValue(GlobalConfig.DATABASE_PASSWORD));
            config.setDriverClassName("org.postgresql.Driver");

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(10_000);
            config.setIdleTimeout(600_000);
            config.setMaxLifetime(1_800_000);

            config.setPoolName("SebUtils-DB");

            dataSource = new HikariDataSource(config);

            migrate();

            getPluginLogger().info("Database pool ready!");
        } catch (Exception e) {
            throw new RuntimeException("Failed to start database module", e);
        }
    }

    private void migrate() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            for (String migration : MIGRATIONS.keySet()) {
                stmt.executeUpdate(MIGRATIONS.get(migration));
            }

        } catch (Exception e) {
            getPluginLogger().severe("Database migration failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void onStop() {
        getPluginLogger().info("Shutting down database pool...");
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
