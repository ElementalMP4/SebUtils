package main.java.elementalmp4.sebutils.service;

import main.java.elementalmp4.sebutils.SebUtils;
import main.java.elementalmp4.sebutils.entity.Profile;
import main.java.elementalmp4.sebutils.utils.ConsoleColours;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextFormat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static main.java.elementalmp4.sebutils.SebUtils.getDatabaseConnection;

public class NicknameService {

    private static final Map<String, TextFormat> COLOURS = new HashMap<>();
    private static final List<String> RAINBOW_ORDER = List.of("red", "orange", "yellow", "green", "blue", "purple", "pink");
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
        COLOURS.put("magic", TextDecoration.OBFUSCATED);
        COLOURS.put("bold", TextDecoration.BOLD);
        COLOURS.put("strikethrough", TextDecoration.STRIKETHROUGH);
        COLOURS.put("italic", TextDecoration.ITALIC);
        COLOURS.put("underline", TextDecoration.UNDERLINED);
        COLOURS.put("rainbow", null);
    }

    public static Set<String> getColourNamesList() {
        return COLOURS.keySet();
    }

    public static String getPlayerNameCustomised(String playerName) {
        String userColour = getUserColour(playerName);
        String nickname = getUserNickname(playerName);
        return applyColour(userColour, nickname) + NamedTextColor.WHITE;
    }

    public static void cacheProfile(String playerName) {
        Profile defaultProfile = new Profile(playerName, "white");
        try (Statement stmt = getDatabaseConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT nickname, colourName FROM chat_customisation WHERE username = '%s'".formatted(playerName));
            if (rs.next()) {
                Profile p = new Profile(rs.getString("nickname"), rs.getString("colourName"));
                PROFILE_CACHE.put(playerName, p);
            } else {
                PROFILE_CACHE.put(playerName, defaultProfile);
                addUser(playerName);
            }
        } catch (SQLException e) {
            PROFILE_CACHE.put(playerName, defaultProfile);
            throw new RuntimeException(e);
        }
    }

    public static void removeProfileCache(String playerName) {
        PROFILE_CACHE.remove(playerName);
    }

    private static String applyColour(String userColour, String nickname) {
        if (userColour.equals("rainbow")) {
            StringBuilder rainbowNameBuilder = new StringBuilder();
            int index = 0;
            for (String nameChar : nickname.split("")) {
                if (index == RAINBOW_ORDER.size()) index = 0;
                rainbowNameBuilder.append(getColourByName(RAINBOW_ORDER.get(index))).append(nameChar);
                index++;
            }
            return rainbowNameBuilder.toString();
        } else {
            return getColourByName(userColour) + nickname;
        }
    }

    private static String getUserNickname(String playerName) {
        return PROFILE_CACHE.get(playerName).getNickname();
    }

    private static String getUserColour(String playerName) {
       return PROFILE_CACHE.get(playerName).getColourName();
    }

    public static void updateNickname(String name, String nickname) {
        try (Statement stmt = getDatabaseConnection().createStatement()) {
            stmt.executeUpdate("UPDATE chat_customisation SET nickname = '%s' WHERE username = '%s';".formatted(nickname, name));
            PROFILE_CACHE.get(name).setNickname(nickname);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addUser(String name) {
        try (Statement stmt = getDatabaseConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO chat_customisation VALUES ('%s', '%s', 'white');".formatted(name, name));
            SebUtils.getPluginLogger().info(ConsoleColours.YELLOW + "Added user " + name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateColour(String name, String colour) {
        try (Statement stmt = getDatabaseConnection().createStatement()) {
            stmt.executeUpdate("UPDATE chat_customisation SET colourName = '%s' WHERE username = '%s';".formatted(colour, name));
            PROFILE_CACHE.get(name).setColourName(colour);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static TextFormat getColourByName(String name) {
        return name.equals("rainbow") ? NamedTextColor.WHITE : COLOURS.get(name);
    }

    public static TextFormat getColourByNameAsChatColour(String name) {
        return name.equals("rainbow") ? NamedTextColor.WHITE : COLOURS.get(name);
    }
}