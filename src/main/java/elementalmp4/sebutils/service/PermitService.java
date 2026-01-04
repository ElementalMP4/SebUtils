package main.java.elementalmp4.sebutils.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static main.java.elementalmp4.sebutils.SebUtils.getDatabaseConnection;

public class PermitService {
    public static void grantPermit(int id, String name) {
        try (Statement stmt = getDatabaseConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO plot_permissions (plot_id, player) VALUES (%d, '%s');"
                    .formatted(id, name));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean userHasPermit(int id, String name) {
        try (Statement stmt = getDatabaseConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM plot_permissions WHERE plot_id = %d AND player = '%s';"
                    .formatted(id, name));
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void revokePermit(int id, String name) {
        try (Statement stmt = getDatabaseConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM plot_permissions WHERE plot_id = %d AND player = '%s';"
                    .formatted(id, name));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getPermits(int id) {
        try (Statement stmt = getDatabaseConnection().createStatement()) {
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
