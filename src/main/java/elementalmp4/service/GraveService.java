package main.java.elementalmp4.service;

import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.utils.Grave;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GraveService {

    public static String createGrave(String owner, int x, int y, int z, String world) {
        Grave grave = new Grave(x, y, z, world);
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO graves (grave_id, grave_owner, pos_x, pos_y, pos_z, world) VALUES ('%s', '%s', %d, %d, %d, '%s')"
                    .formatted(grave.getId(), owner, x, y, z, world));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return grave.getId();
    }

    public static Optional<Grave> getGrave(String name, String graveId) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM graves WHERE grave_id = '%s' AND grave_owner = '%s'".formatted(graveId, name));
            if (rs.next()) return Optional.of(new Grave(
                    rs.getInt("pos_x"),
                    rs.getInt("pos_y"),
                    rs.getInt("pos_z"),
                    rs.getString("world"),
                    rs.getString("grave_id"))
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public static List<Grave> getGraves(String playerName) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT world, pos_x, pos_y, pos_z, grave_id FROM graves WHERE grave_owner = '%s';"
                    .formatted(playerName));
            List<Grave> graves = new ArrayList<>();
            while (rs.next()) {
                graves.add(new Grave(
                        rs.getInt("pos_x"),
                        rs.getInt("pos_y"),
                        rs.getInt("pos_z"),
                        rs.getString("world"),
                        rs.getString("grave_id")));
            }
            return graves;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeGrave(String graveId) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM graves WHERE grave_id = '%s'".formatted(graveId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
