package main.java.elementalmp4.sebutils.service;

import main.java.elementalmp4.sebutils.entity.Grave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static main.java.elementalmp4.sebutils.SebUtils.getDatabaseConnection;

public class GraveService {

    public static String createGrave(String owner, int x, int y, int z, String world) {
        Grave grave = new Grave(x, y, z, world);

        String sql = """
            INSERT INTO graves (grave_id, grave_owner, pos_x, pos_y, pos_z, world)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, grave.getId());
            ps.setString(2, owner);
            ps.setInt(3, x);
            ps.setInt(4, y);
            ps.setInt(5, z);
            ps.setString(6, world);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return grave.getId();
    }

    public static Optional<Grave> getGrave(String name, String graveId) {
        String sql = """
            SELECT pos_x, pos_y, pos_z, world, grave_id
            FROM graves
            WHERE grave_id = ? AND grave_owner = ?
            """;

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, graveId);
            ps.setString(2, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Grave(
                            rs.getInt("pos_x"),
                            rs.getInt("pos_y"),
                            rs.getInt("pos_z"),
                            rs.getString("world"),
                            rs.getString("grave_id")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public static List<Grave> getGraves(String playerName) {
        String sql = """
            SELECT world, pos_x, pos_y, pos_z, grave_id
            FROM graves
            WHERE grave_owner = ?
            """;

        List<Grave> graves = new ArrayList<>();

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, playerName);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    graves.add(new Grave(
                            rs.getInt("pos_x"),
                            rs.getInt("pos_y"),
                            rs.getInt("pos_z"),
                            rs.getString("world"),
                            rs.getString("grave_id")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return graves;
    }

    public static void removeGrave(String graveId) {
        String sql = "DELETE FROM graves WHERE grave_id = ?";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, graveId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getGraveIds(String name) {
        String sql = "SELECT grave_id FROM graves WHERE grave_owner = ?";

        List<String> graveIds = new ArrayList<>();

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    graveIds.add(rs.getString("grave_id"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return graveIds;
    }
}
