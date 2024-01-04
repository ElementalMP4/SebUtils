package main.java.elementalmp4.service;

import main.java.elementalmp4.SebUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NicknameService {

    private static final Map<String, String> COLOURS = new HashMap<>();

    static {
        COLOURS.put("black", "0");
        COLOURS.put("darkblue", "1");
        COLOURS.put("darkgreen", "2");
        COLOURS.put("darkaqua", "3");
        COLOURS.put("darkred", "4");
        COLOURS.put("purple", "5");
        COLOURS.put("orange", "6");
        COLOURS.put("gray", "7");
        COLOURS.put("darkgray", "8");
        COLOURS.put("blue", "9");
        COLOURS.put("green", "a");
        COLOURS.put("aqua", "b");
        COLOURS.put("red", "c");
        COLOURS.put("pink", "d");
        COLOURS.put("yellow", "e");
        COLOURS.put("white", "f");
        COLOURS.put("magic", "k");
        COLOURS.put("bold", "l");
        COLOURS.put("strikethrough", "m");
        COLOURS.put("italic", "o");
        COLOURS.put("underline", "n");
    }

    public static Set<String> getColourNamesList() {
        return COLOURS.keySet();
    }

    public static void adaptMessageNickname(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String modifiedPlayerName = getUserColour(playerName) + getUserNickname(playerName) + ChatColor.RESET;
        event.setFormat(modifiedPlayerName + ": %2$s");
    }

    private static String getUserNickname(String playerName) {
        try {
            Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nickname FROM chat_customisation WHERE username = '%s'".formatted(playerName));
            while (rs.next()) {
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return playerName;
    }

    private static String getUserColour(String playerName) {
        try {
            Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT colourName FROM chat_customisation WHERE username = '%s'".formatted(playerName));
            while (rs.next()) {
                String colourCode = COLOURS.get(rs.getString("colourName"));
                return ChatColor.getByChar(colourCode).toString();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ChatColor.WHITE.toString();
    }

    public static void updateNickname(String name, String nickname) {
        try {
            Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement();
            stmt.executeUpdate("UPDATE chat_customisation SET nickname = '%s' WHERE username = '%s';".formatted(nickname, name));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addUser(String name) {
        try {
            Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement();
            stmt.executeUpdate("INSERT INTO chat_customisation VALUES ('%s', '%s', 'white');".formatted(name, name));
            SebUtils.getPluginLogger().info("Added user " + name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateColour(String name, String colour) {
        try {
            Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement();
            stmt.executeUpdate("UPDATE chat_customisation SET colourName = '%s' WHERE username = '%s';".formatted(colour, name));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
