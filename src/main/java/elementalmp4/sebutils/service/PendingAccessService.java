package main.java.elementalmp4.sebutils.service;

import main.java.elementalmp4.sebutils.modules.DiscordModule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static main.java.elementalmp4.sebutils.SebUtils.*;

public class PendingAccessService {

    public static boolean accessRequestPending(UUID userId) {
        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM pending_access WHERE uuid = ?"
             )) {
            ps.setObject(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            getPluginLogger().severe(e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public static void createAccessRequest(UUID userId, String playerName) {
        String sql = "INSERT INTO pending_access (uuid) VALUES (?)";
        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // No need to check if discord is enabled here, because we just checked it
        getModuleManager().get(DiscordModule.class).sendAccessRequest(userId, playerName);
    }

    public static void removePendingRequest(UUID userId) {
        String sql = "DELETE FROM pending_access WHERE uuid = ?";
        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
