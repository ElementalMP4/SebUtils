package main.java.elementalmp4.sebutils.service;

import main.java.elementalmp4.sebutils.SebUtils;
import main.java.elementalmp4.sebutils.entity.Home;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static main.java.elementalmp4.sebutils.SebUtils.getDatabaseConnection;

public class HomeService {

    public static void setHome(String username, String world, int x, int y, int z, String homeName) {
        String deleteSql = "DELETE FROM user_homes WHERE username = ? AND home_name = ?";
        String insertSql = "INSERT INTO user_homes (username, world, pos_x, pos_y, pos_z, home_name) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getDatabaseConnection()) {
            // Delete existing home
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setString(1, username);
                ps.setString(2, homeName);
                ps.executeUpdate();
            }

            // Insert new home
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, username);
                ps.setString(2, world);
                ps.setInt(3, x);
                ps.setInt(4, y);
                ps.setInt(5, z);
                ps.setString(6, homeName);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteHome(String username, String homeName) {
        String sql = "DELETE FROM user_homes WHERE username = ? AND home_name = ?";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, homeName);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean userHasHome(String username, String homeName) {
        String sql = "SELECT 1 FROM user_homes WHERE username = ? AND home_name = ? LIMIT 1";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, homeName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void teleportUserHome(Player player, String home) {
        String sql = "SELECT world, pos_x, pos_y, pos_z FROM user_homes WHERE username = ? AND home_name = ?";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, player.getName());
            ps.setString(2, home);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    World world = SebUtils.getPlugin().getServer().getWorld(rs.getString("world"));
                    int x = rs.getInt("pos_x");
                    int y = rs.getInt("pos_y");
                    int z = rs.getInt("pos_z");

                    Chunk oldChunk = player.getLocation().getChunk();
                    player.teleport(new Location(world, x + 0.5, y, z + 0.5));
                    oldChunk.load(true);
                    TeleportService.playTeleportEffects(player);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Home> getHomes(String username) {
        String sql = "SELECT world, pos_x, pos_y, pos_z, home_name FROM user_homes WHERE username = ?";
        List<Home> homes = new ArrayList<>();

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    homes.add(new Home(
                            rs.getString("world"),
                            rs.getString("home_name"),
                            rs.getInt("pos_x"),
                            rs.getInt("pos_y"),
                            rs.getInt("pos_z")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return homes;
    }

    public static List<String> getHomeNames(String username) {
        String sql = "SELECT home_name FROM user_homes WHERE username = ?";
        List<String> homeNames = new ArrayList<>();

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    homeNames.add(rs.getString("home_name"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return homeNames;
    }
}
