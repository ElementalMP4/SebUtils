package main.java.elementalmp4.utils;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.SecretGenerator;
import com.bastiaanjansen.otp.TOTPGenerator;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class GoogleAuthenticatorHelper {

    public static String generateOtpSecret() {
        return new String(SecretGenerator.generate(256), StandardCharsets.UTF_8);
    }

    public static String generateCode(String secret) {
        TOTPGenerator totp = new TOTPGenerator.Builder(secret.getBytes())
                .withHOTPGenerator(builder -> {
                    builder.withPasswordLength(6);
                    builder.withAlgorithm(HMACAlgorithm.SHA512);
                })
                .withPeriod(Duration.ofSeconds(30))
                .build();
        return totp.now();
    }

    public static String generateAuthURL(String secretKey, String accountName) {
        String encodedAccountName = URLEncoder.encode(accountName, StandardCharsets.UTF_8);
        String encodedIssuer = URLEncoder.encode("SebUtils Dashboard", StandardCharsets.UTF_8);
        return String.format(
                "otpauth://totp/%s:%s?period=30&digits=6&secret=%s&algorithm=SHA512",
                encodedIssuer,
                encodedAccountName,
                secretKey
        );

    }
}
