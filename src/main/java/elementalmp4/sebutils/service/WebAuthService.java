package main.java.elementalmp4.sebutils.service;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static main.java.elementalmp4.sebutils.SebUtils.getDatabaseConnection;

public class WebAuthService {

    private static final SecureRandom RNG = new SecureRandom();
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int OTP_LENGTH = 6;

    // OTPs stay in-memory
    private static final Map<String, String> otps = new ConcurrentHashMap<>();

    public static String generateOtpForUser(String username) {
        String otp = randomString(OTP_LENGTH);
        otps.put(username, otp);
        return otp;
    }

    public static synchronized String verifyOtpAndIssueToken(String username, String otp) {
        String expected = otps.get(username);
        if (expected == null || !expected.equals(otp)) {
            return null;
        }

        otps.remove(username);

        UUID token = UUID.randomUUID();

        // Store token in DB
        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(
                     """
                             INSERT INTO web_auth_tokens (username, token)
                             VALUES (?, ?)
                             ON CONFLICT (username)
                             DO UPDATE SET token = EXCLUDED.token, issued_at = NOW()
                             """
             )) {

            ps.setString(1, username);
            ps.setObject(2, token);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        Player player = Bukkit.getPlayer(username);
        if (player != null) {
            player.sendMessage(
                    Component.text(
                            "You can now access the web dashboard!",
                            NamedTextColor.GREEN
                    )
            );
        }

        return token.toString();
    }

    /**
     * Returns the username for a valid token, or null if invalid
     */
    public static String validateToken(String token) {
        if (token == null) return null;

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT username FROM web_auth_tokens WHERE token = ?"
             )) {

            ps.setObject(1, UUID.fromString(token));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }

        } catch (IllegalArgumentException ignored) {
            // Invalid UUID string
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static synchronized void invalidateTokenForUser(String username) {
        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM web_auth_tokens WHERE username = ?"
             )) {

            ps.setString(1, username);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARSET.charAt(RNG.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }
}
