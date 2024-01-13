package main.java.elementalmp4.service;

import main.java.elementalmp4.SebUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PermitService {
    public static void grantPermit(int id, String name) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO plot_permissions (plot_id, player) VALUES (%d, '%s');"
                    .formatted(id, name));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean userHasPermit(int id, String name) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM plot_permissions WHERE plot_id = %d AND player = '%s';"
                    .formatted(id, name));
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void revokePermit(int id, String name) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM plot_permissions WHERE plot_id = %d AND player = '%s';"
                    .formatted(id, name));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getPermits(int id) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            List<String> players = new ArrayList<>();
            ResultSet rs = stmt.executeQuery("SELECT * FROM plot_permissions WHERE plot_id = %d;"
                    .formatted(id));
            while (rs.next()) {
                players.add(rs.getString("player"));
            }
            return players;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
