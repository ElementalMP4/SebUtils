package main.java.elementalmp4.sebutils.service;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WebAuthService {

    private static final SecureRandom RNG = new SecureRandom();
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int OTP_LENGTH = 6;

    private static final Map<String, String> otps = new ConcurrentHashMap<>();
    private static final Map<String, String> userTokens = new ConcurrentHashMap<>();
    private static final Map<String, String> tokenToUser = new ConcurrentHashMap<>();

    public static String generateOtpForUser(String username) {
        String otp = randomString(OTP_LENGTH);
        otps.put(username, otp);
        return otp;
    }

    public static synchronized String verifyOtpAndIssueToken(String username, String otp) {
        String expected = otps.get(username);
        if (expected == null) return null;
        if (!expected.equals(otp)) return null;

        otps.remove(username);

        String token = UUID.randomUUID().toString();

        String old = userTokens.put(username, token);
        if (old != null) {
            tokenToUser.remove(old);
        }
        tokenToUser.put(token, username);

        Player player = Bukkit.getPlayer(username);
        if (player != null) {
            player.sendMessage(Component.text("You can now access the web dashboard!", NamedTextColor.GREEN));
        }

        return token;
    }

    public static String validateToken(String token) {
        if (token == null) return null;
        return tokenToUser.get(token);
    }

    public static synchronized void invalidateTokenForUser(String username) {
        String tok = userTokens.remove(username);
        if (tok != null) tokenToUser.remove(tok);
    }

    private static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARSET.charAt(RNG.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }
}
