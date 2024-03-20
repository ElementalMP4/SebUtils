package main.java.elementalmp4.service;

import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.utils.Home;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HomeService {

    public static void setHome(String name, String world, int x, int y, int z, String homeName) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM user_homes WHERE username = '%s' AND home_name = '%s';".formatted(name, homeName));
            stmt.executeUpdate("INSERT INTO user_homes VALUES ('%s', '%s', %d, %d, %d, '%s')".formatted(name, world, x, y, z, homeName));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteHome(String username, String homeName) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate(("DELETE FROM user_homes WHERE username = '%s' AND home_name = '%s';")
                    .formatted(username, homeName));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean userHasHome(String username, String homeName) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT username FROM user_homes WHERE username = '%s' AND home_name = '%s';"
                    .formatted(username, homeName));
            return rs.isBeforeFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void teleportUserHome(Player player, String home) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT world, pos_x, pos_y, pos_z FROM user_homes WHERE username = '%s' AND home_name = '%s';"
                    .formatted(player.getName(), home));
            if (rs.next()) {
                World world = SebUtils.getPlugin().getServer().getWorld(rs.getString("world"));
                int x = rs.getInt("pos_x");
                int y = rs.getInt("pos_y");
                int z = rs.getInt("pos_z");
                Chunk oldChunk = player.getLocation().getChunk();
                player.teleport(new Location(world, (x + 0.5), y, (z + 0.5)));
                oldChunk.load(true);
                TeleportService.playTeleportEffects(player);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Home> getHomes(String username) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT world, pos_x, pos_y, pos_z, home_name FROM user_homes WHERE username = '%s';"
                    .formatted(username));
            List<Home> homes = new ArrayList<>();
            while (rs.next()) {
                homes.add(new Home(
                        rs.getString("world"),
                        rs.getString("home_name"),
                        rs.getInt("pos_x"),
                        rs.getInt("pos_y"),
                        rs.getInt("pos_z")));
            }
            return homes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
