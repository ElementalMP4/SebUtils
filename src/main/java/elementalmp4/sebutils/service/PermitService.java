package main.java.elementalmp4.sebutils.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static main.java.elementalmp4.sebutils.SebUtils.getDatabaseConnection;

public class PermitService {

    public static void grantPermit(int plotId, String playerName) {
        String sql = "INSERT INTO plot_permissions (plot_id, player) VALUES (?, ?)";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, plotId);
            ps.setString(2, playerName);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean userHasPermit(int plotId, String playerName) {
        String sql = "SELECT 1 FROM plot_permissions WHERE plot_id = ? AND player = ? LIMIT 1";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, plotId);
            ps.setString(2, playerName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void revokePermit(int plotId, String playerName) {
        String sql = "DELETE FROM plot_permissions WHERE plot_id = ? AND player = ?";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, plotId);
            ps.setString(2, playerName);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getPermits(int plotId) {
        String sql = "SELECT player FROM plot_permissions WHERE plot_id = ?";
        List<String> players = new ArrayList<>();

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, plotId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    players.add(rs.getString("player"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return players;
    }
}
