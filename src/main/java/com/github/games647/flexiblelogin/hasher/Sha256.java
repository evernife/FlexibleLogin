package com.github.games647.flexiblelogin.hasher;

import com.github.games647.flexiblelogin.util.RandomStringUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256 implements Hasher {

    public int getSaltLength() {
        return 16;
    }

    public String generateSalt() {
        return RandomStringUtils.generateHex(getSaltLength());
    }

    @Override
    public String hash(String password) throws Exception {
        String salt = generateSalt();
        return "$SHA$" + salt + "$" + sha256(sha256(password) + salt);
    }

    public String hash(String password, String salt) {
        return "$SHA$" + salt + "$" + sha256(sha256(password) + salt);
    }

    @Override
    public boolean checkPassword(String passwordHash, String userInput) throws Exception {
        String[] line = passwordHash.split("\\$");
        return line.length == 4 && isEqual(passwordHash, hash(userInput, line[2]));
    }

    public static MessageDigest algorithm = null;
    static {
        try {
            algorithm = MessageDigest.getInstance("SHA-256");
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    /**
     * Hash the message with the given algorithm and return the hash in its hexadecimal notation.
     *
     * @param message The message to hash
     * @return The digest in its hexadecimal representation
     */
    public static String sha256(String message) {
        if (algorithm != null){
            algorithm.reset();
            algorithm.update(message.getBytes());
            byte[] digest = algorithm.digest();
            return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest));
        }
        return "ERROR_GETTING_THIS";
    }

    /**
     * Checks whether the two strings are equal to each other in a time-constant manner.
     * This helps to avoid timing side channel attacks,
     * cf. <a href="https://github.com/AuthMe/AuthMeReloaded/issues/1561">issue #1561</a>.
     *
     * @param string1 first string
     * @param string2 second string
     * @return true if the strings are equal to each other, false otherwise
     */
    public static boolean isEqual(String string1, String string2) {
        return MessageDigest.isEqual(
                string1.getBytes(StandardCharsets.UTF_8), string2.getBytes(StandardCharsets.UTF_8));
    }
}
