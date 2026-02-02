package main.java.elementalmp4.sebutils.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static main.java.elementalmp4.sebutils.SebUtils.getDatabaseConnection;

public class PVPToggleService {

    private static final HashMap<String, Boolean> PVP_TOGGLE_CACHE = new HashMap<>();

    public static boolean playerHasDisabledPvp(String playerName) {
        return PVP_TOGGLE_CACHE.getOrDefault(playerName, false);
    }

    public static void updatePlayerToggle(String playerName, boolean status) {
        PVP_TOGGLE_CACHE.put(playerName, status);

        String sql = "UPDATE pvp_toggles SET toggle = ? WHERE username = ?";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, status);
            ps.setString(2, playerName);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void cachePlayer(String playerName) {
        boolean defaultToggle = false;
        String sql = "SELECT toggle FROM pvp_toggles WHERE username = ?";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, playerName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean toggleStatus = rs.getBoolean("toggle");
                    PVP_TOGGLE_CACHE.put(playerName, toggleStatus);
                } else {
                    PVP_TOGGLE_CACHE.put(playerName, defaultToggle);
                    addUserPvpToggle(playerName);
                }
            }

        } catch (SQLException e) {
            PVP_TOGGLE_CACHE.put(playerName, defaultToggle);
            throw new RuntimeException(e);
        }
    }

    private static void addUserPvpToggle(String playerName) {
        String sql = "INSERT INTO pvp_toggles (username, toggle) VALUES (?, ?)";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, playerName);
            ps.setBoolean(2, false);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removePlayerCache(String name) {
        PVP_TOGGLE_CACHE.remove(name);
    }
}
