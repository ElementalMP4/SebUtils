package main.java.elementalmp4.service;

import main.java.elementalmp4.SebUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HomeService {
    public static void setOrUpdateHome(String name, String world, int x, int y, int z) {
        try {
            Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement();
            stmt.executeUpdate("DELETE FROM user_homes WHERE username = '%s';".formatted(name));
            stmt.executeUpdate("INSERT INTO user_homes VALUES ('%s', '%s', %d, %d, %d)".formatted(name, world, x, y, z));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteHome(String name) {
        try {
            Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement();
            stmt.executeUpdate("DELETE FROM user_homes WHERE username = '%s';".formatted(name));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean userHasHome(String name) {
        try {
            Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM user_homes WHERE username = '%s';".formatted(name));
            return !rs.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void teleportUserHome(Player player) {
        try {
            Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT world, pos_x, pos_y, pos_z FROM user_homes WHERE username = '%s';".formatted(player.getName()));
            if (rs.next()) {
                World world = SebUtils.getPlugin(SebUtils.class).getServer().getWorld(rs.getString("world"));
                int x = rs.getInt("pos_x");
                int y = rs.getInt("pos_y");
                int z = rs.getInt("pos_z");
                player.teleport(new Location(world, (x + 0.5), y, (z + 0.5)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
