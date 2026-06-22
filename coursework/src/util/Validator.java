package util;

import java.util.regex.Pattern;

/**
 * Utility class for validating user input fields.
 */
public class Validator {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]{2,30}$");
    private static final Pattern NIN_PATTERN = Pattern.compile("^[A-Z0-9]{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+256[0-9]{9}$");
    private static final Pattern PIN_PATTERN = Pattern.compile("^[0-9]{4,6}$");

    private Validator() {
        // Utility class - prevent instantiation
    }

    /**
     * Validates name: 2-30 letters only.
     */
    public static boolean isValidName(String s) {
        return s != null && NAME_PATTERN.matcher(s).matches();
    }

    /**
     * Validates NIN: exactly 10 uppercase alphanumeric characters.
     */
    public static boolean isValidNIN(String s) {
        return s != null && NIN_PATTERN.matcher(s).matches();
    }

    /**
     * Validates email format.
     */
    public static boolean isValidEmail(String s) {
        return s != null && EMAIL_PATTERN.matcher(s).matches();
    }

    /**
     * Validates phone: +256 followed by 9 digits.
     */
    public static boolean isValidPhone(String s) {
        return s != null && PHONE_PATTERN.matcher(s).matches();
    }

    /**
     * Validates PIN: must be 4-6 digits and not all identical.
     */
    public static boolean isValidPIN(String s) {
        if (s == null || !PIN_PATTERN.matcher(s).matches())
            return false;
        char first = s.charAt(0);
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) != first)
                return true;
        }
        return false;
    }
}