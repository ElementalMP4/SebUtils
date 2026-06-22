package main.java.elementalmp4.sebutils.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import io.javalin.http.Context;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.entity.Session;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static main.java.elementalmp4.sebutils.SebUtils.getDatabaseConnection;
import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

public class WebAuthService {
    private static final TimeProvider timeProvider = new SystemTimeProvider();
    private static final CodeGenerator codeGenerator = new DefaultCodeGenerator();
    private static final SecretGenerator secretGenerator = new DefaultSecretGenerator(64);
    private static final CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Duration SESSION_TTL = Duration.ofDays(30);

    public static synchronized String verifyOtpAndIssueToken(String otp, String ip, String userAgent) {
        String secret = GlobalConfigService.getValue(GlobalConfig.WEB_TOTP_TOKEN);
        if (!verifier.isValidCode(secret, otp)) {
            return null;
        }

        String token = generateToken();
        insert(UUID.randomUUID(), hashToken(token), Instant.now().plus(SESSION_TTL), ip, userAgent);
        return token;
    }

    public static boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        Optional<Session> session = findActiveByHash(hashToken(token));
        session.ifPresent(s -> touch(s.getId()));
        return session.isPresent();
    }

    public static void revokeToken(String token) {
        if (token != null) {
            revoke(hashToken(token));
        }
    }

    private static String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasAuthenticated(Context ctx) {
        Boolean ok = ctx.attribute("userHasBeenAuthenticated");
        if (ok == null || !ok) {
            ctx.status(403).result("OP required");
            return false;
        }
        return true;
    }

    public static void ensureLoginTokenExists() {
        if (GlobalConfigService.getValue(GlobalConfig.WEB_TOTP_TOKEN).equals(GlobalConfigService.UNSET_VALUE)) {
            String token = secretGenerator.generate();
            GlobalConfigService.set(GlobalConfig.WEB_TOTP_TOKEN, token);
        }

        printTotpQrCode();
    }

    public static void printTotpQrCode() {
        String secret = GlobalConfigService.getValue(GlobalConfig.WEB_TOTP_TOKEN);
        String uri    = "otpauth://totp/SebUtils"
                + "?secret=" + secret
                + "&issuer=" + URLEncoder.encode("SebUtils", StandardCharsets.UTF_8)
                + "&algorithm=SHA1"
                + "&digits=6"
                + "&period=30";

        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix matrix = new QRCodeWriter().encode(uri, BarcodeFormat.QR_CODE, 0, 0, hints);
            getPluginLogger().info("In order to access the SebUtils web dashboard, scan this QR code in your authenticator app:\n");
            getPluginLogger().info(stringifyQrMatrix(matrix));

        } catch (WriterException e) {
            throw new RuntimeException("Failed to generate TOTP QR code", e);
        }
    }

    private static String stringifyQrMatrix(BitMatrix matrix) {
        int width  = matrix.getWidth();
        int height = matrix.getHeight();
        StringBuilder output = new StringBuilder();
        output.append("\n");
        for (int y = 0; y < height; y += 2) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; x++) {
                boolean top    = matrix.get(x, y);
                boolean bottom = (y + 1 < height) && matrix.get(x, y + 1);

                if (top && bottom)       line.append('█');
                else if (top)            line.append('▀');
                else if (bottom)         line.append('▄');
                else                     line.append(' ');
            }
            output.append(line).append("\n");
        }
        return output.toString();
    }

    public static void insert(UUID id, String tokenHash, Instant expiresAt, String ip, String userAgent) {
        String sql = "INSERT INTO web_sessions (id, token_hash, expires_at, ip_address, user_agent) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, id);
            ps.setString(2, tokenHash);
            ps.setTimestamp(3, Timestamp.from(expiresAt));
            ps.setString(4, ip);
            ps.setString(5, userAgent);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<Session> findActiveByHash(String tokenHash) {
        String sql = "SELECT id, expires_at FROM web_sessions WHERE token_hash = ? AND revoked = false AND expires_at > now()";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tokenHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Session(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void touch(UUID sessionId) {
        String sql = "UPDATE web_sessions SET last_used_at = now() WHERE id = ?";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, sessionId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void revoke(String tokenHash) {
        String sql = "UPDATE web_sessions SET revoked = true WHERE token_hash = ?";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tokenHash);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void revokeAll() {
        String sql = "UPDATE web_sessions SET revoked = true WHERE revoked = false";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteExpired() {
        String sql = "DELETE FROM web_sessions WHERE expires_at < now() - interval '7 days'";

        try (Connection conn = getDatabaseConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
