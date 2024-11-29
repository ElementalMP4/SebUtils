package main.java.elementalmp4.service;

import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.entity.WebAuthRequest;
import main.java.elementalmp4.utils.GoogleAuthenticatorHelper;
import main.java.elementalmp4.utils.NamedThreadFactory;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebAuthService {

    private static final String LEXICON = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Map<String, WebAuthRequest> SIGN_UP_REQUESTS = new HashMap<>();

    static {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("authreq"));
        executor.scheduleAtFixedRate(WebAuthService::expireRequests, 0, 1, TimeUnit.SECONDS);
    }

    private static void expireRequests() {
        List<WebAuthRequest> expired = SIGN_UP_REQUESTS.keySet().stream()
                .map(SIGN_UP_REQUESTS::get)
                .filter(WebAuthRequest::expired).toList();
        for (WebAuthRequest r : expired) {
            SIGN_UP_REQUESTS.remove(r.getUsername());
        }
    }

    public static String generateAuthCode() {
        StringBuilder result = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(LEXICON.length());
            result.append(LEXICON.charAt(index));
        }

        return result.toString();
    }

    public static String createSignUpRequest(String username) {
        String authCode = generateAuthCode();
        SIGN_UP_REQUESTS.put(username, new WebAuthRequest(username, authCode));
        return authCode;
    }

    public static boolean validateAuthRequest(String username, String authCode) {
        return SIGN_UP_REQUESTS.containsKey(username) && SIGN_UP_REQUESTS.get(username).getAuthCode().equals(authCode);
    }

    private static void saveAccount(String username, String authCode) {
        try (Statement stmt = SebUtils.getDatabaseService().getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO web_dash_accounts VALUES ('%s', '%s')".formatted(username, authCode));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String createAccount(String username) {
        String secret = GoogleAuthenticatorHelper.generateOtpSecret();
        saveAccount(username, secret);
        return GoogleAuthenticatorHelper.generateAuthURL(secret, username);
    }

}
