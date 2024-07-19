package main.java.elementalmp4.service;

import main.java.elementalmp4.SebUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class PVPToggleService {

    private static final HashMap<String, Boolean> PVP_TOGGLE_CACHE = new HashMap<>();

    public static boolean playerHasDisabledPvp(String playerName) {
        if (PVP_TOGGLE_CACHE.containsKey(playerName)) return false;
        else return PVP_TOGGLE_CACHE.get(playerName);
    }

    public static void updatePlayerToggle(String playerName, boolean status) {
        if (PVP_TOGGLE_CACHE.containsKey(playerName)) PVP_TOGGLE_CACHE.put(playerName, status);
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("UPDATE pvp_toggles SET toggle = %b WHERE username = '%s';".formatted(status, playerName));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void cachePlayer(String playerName) {
        boolean defaultToggle = false;
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM pvp_toggles WHERE username = '%s'".formatted(playerName));
            if (rs.next()) {
                boolean toggleStatus = rs.getBoolean("toggle");
                PVP_TOGGLE_CACHE.put(playerName, toggleStatus);
            } else {
                PVP_TOGGLE_CACHE.put(playerName, defaultToggle);
                addUserPvpToggle(playerName);
            }
        } catch (SQLException e) {
            PVP_TOGGLE_CACHE.put(playerName, defaultToggle);
            throw new RuntimeException(e);
        }
    }

    private static void addUserPvpToggle(String playerName) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO pvp_toggles VALUES ('%s', %b);".formatted(playerName, false));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removePlayerCache(String name) {
        PVP_TOGGLE_CACHE.remove(name);
    }
}