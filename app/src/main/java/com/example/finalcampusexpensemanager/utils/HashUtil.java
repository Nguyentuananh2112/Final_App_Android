package com.example.finalcampusexpensemanager.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    public static String hashPassword(String password) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String hexString = Integer.toHexString(0xff & b);
                if (hexString.length() == 1) hex.append('0');
                hex.append(hexString);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
