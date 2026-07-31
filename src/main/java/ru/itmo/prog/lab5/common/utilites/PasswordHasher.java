package ru.itmo.prog.lab5.common.utilites;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Класс для хеширования паролей.
 */

public class PasswordHasher {

    private static final int ITERATIONS = 1000;
    private static final int SALT_LENGTH = 16;

    public static String hash(String password) {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            secureRandom.nextBytes(salt);

            byte[] hash = hashWithSalt(password, salt);

            String saltHex = HexFormat.of().formatHex(salt);
            String hashHex = HexFormat.of().formatHex(hash);

            return saltHex + ":" + hashHex;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка хэширования пароля", e);
        }
    }

    /**
     * Проверяет, совпадает ли пароль с сохраненным хэшем.
     */

    public static boolean verify(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }

            String saltHex = parts[0];
            String hashHex = parts[1];

            byte[] salt = HexFormat.of().parseHex(saltHex);
            byte[] computedHash = hashWithSalt(password, salt);

            String computedHex = HexFormat.of().formatHex(computedHash);
            return slowEquals(computedHex, hashHex);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * SHA-256 с солью и множественными интерациями.
     */
    public static byte[] hashWithSalt(String password, byte[] salt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(salt);
            byte[] hash = messageDigest.digest(password.getBytes("UTF-8"));

            for (int i = 1; i < ITERATIONS; i++) {
                messageDigest.reset();
                messageDigest.update(salt);
                hash = messageDigest.digest(hash);
            }
            return hash;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка SHA-256", e);
        }
    }
    /**
     * Безопасное сравнение строк в постоянном времени.
     */
    private static boolean slowEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}
