package main.java.elementalmp4.sebutils.service;

import main.java.elementalmp4.sebutils.SebUtils;
import main.java.elementalmp4.sebutils.entity.Profile;
import main.java.elementalmp4.sebutils.utils.ConsoleColours;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static main.java.elementalmp4.sebutils.SebUtils.getDatabaseConnection;

public class NicknameService {

    private static final Map<String, NamedTextColor> COLOURS = new HashMap<>();
    private static final Map<String, Profile> PROFILE_CACHE = new HashMap<>();

    static {
        COLOURS.put("black", NamedTextColor.BLACK);
        COLOURS.put("darkblue", NamedTextColor.DARK_BLUE);
        COLOURS.put("darkgreen", NamedTextColor.DARK_GREEN);
        COLOURS.put("darkaqua", NamedTextColor.DARK_AQUA);
        COLOURS.put("darkred", NamedTextColor.DARK_RED);
        COLOURS.put("purple", NamedTextColor.DARK_PURPLE);
        COLOURS.put("orange", NamedTextColor.GOLD);
        COLOURS.put("gray", NamedTextColor.GRAY);
        COLOURS.put("darkgray", NamedTextColor.DARK_GRAY);
        COLOURS.put("blue", NamedTextColor.BLUE);
        COLOURS.put("green", NamedTextColor.GREEN);
        COLOURS.put("aqua", NamedTextColor.AQUA);
        COLOURS.put("red", NamedTextColor.RED);
        COLOURS.put("pink", NamedTextColor.LIGHT_PURPLE);
        COLOURS.put("yellow", NamedTextColor.YELLOW);
        COLOURS.put("white", NamedTextColor.WHITE);
    }

    public static Set<String> getColourNamesList() {
        return COLOURS.keySet();
    }

    public static NamedTextColor getColour(String name) {
        return COLOURS.get(name);
    }

    public static TextComponent getPlayerNameCustomised(String playerName) {
        String userColour = getUserColour(playerName);
        String nickname = getUserNickname(playerName);
        return applyColour(userColour, nickname);
    }

    public static void cacheProfile(String playerName) {
        Profile defaultProfile = new Profile(playerName, "white");
        String sql = "SELECT nickname, colourName FROM chat_customisation WHERE username = ?";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, playerName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Profile p = new Profile(rs.getString("nickname"), rs.getString("colourName"));
                    PROFILE_CACHE.put(playerName, p);
                } else {
                    PROFILE_CACHE.put(playerName, defaultProfile);
                    addUser(playerName);
                }
            }

        } catch (SQLException e) {
            PROFILE_CACHE.put(playerName, defaultProfile);
            throw new RuntimeException(e);
        }
    }

    public static void removeProfileCache(String playerName) {
        PROFILE_CACHE.remove(playerName);
    }

    private static TextComponent applyColour(String userColour, String nickname) {
        return Component.text(nickname, getColour(userColour));
    }

    private static String getUserNickname(String playerName) {
        return PROFILE_CACHE.get(playerName).getNickname();
    }

    private static String getUserColour(String playerName) {
        return PROFILE_CACHE.get(playerName).getColourName();
    }

    public static void updateNickname(String name, String nickname) {
        String sql = "UPDATE chat_customisation SET nickname = ? WHERE username = ?";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nickname);
            ps.setString(2, name);
            ps.executeUpdate();

            PROFILE_CACHE.get(name).setNickname(nickname);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addUser(String name) {
        String sql = "INSERT INTO chat_customisation (username, nickname, colourName) VALUES (?, ?, 'white')";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, name);
            ps.executeUpdate();

            SebUtils.getPluginLogger().info(ConsoleColours.YELLOW + "Added user " + name);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateColour(String name, String colour) {
        String sql = "UPDATE chat_customisation SET colourName = ? WHERE username = ?";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, colour);
            ps.setString(2, name);
            ps.executeUpdate();

            PROFILE_CACHE.get(name).setColourName(colour);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
